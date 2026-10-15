import { HttpErrorResponse } from '@angular/common/http';
import { Component, computed, inject, signal } from '@angular/core';
import { ChartConfiguration } from 'chart.js';
import { AuthService } from '../../../core/auth/auth.service';
import { ChartCanvas } from '../../../core/charts/chart-canvas/chart-canvas';
import { describeHttpError } from '../../../core/http/describe-error';
import {
    IssuePriority,
    IssueStats,
    IssueStatus,
    IssueType,
    PRIORITY_LABELS,
    STATUS_LABELS,
    TYPE_LABELS,
} from '../../../core/issue/issue.models';
import { IssueService } from '../../../core/issue/issue.service';

const STATUSES: IssueStatus[] = ['TODO', 'IN_PROGRESS', 'RESOLVED', 'CLOSED'];
const TYPES: IssueType[] = ['BUG', 'FEATURE', 'QUESTION', 'DOCUMENTATION'];
const PRIORITIES: IssuePriority[] = ['CRITICAL', 'HIGH', 'MEDIUM', 'LOW'];

const STATUS_COLOR: Record<IssueStatus, string> = {
    TODO: '--bs-secondary',
    IN_PROGRESS: '--bs-primary',
    RESOLVED: '--bs-success',
    CLOSED: '--bs-dark',
};
const PRIORITY_COLOR: Record<IssuePriority, string> = {
    CRITICAL: '--bs-danger',
    HIGH: '--bs-warning',
    MEDIUM: '--bs-info',
    LOW: '--bs-gray-400',
};

function cssColor(variable: string): string {
    return getComputedStyle(document.documentElement).getPropertyValue(variable).trim();
}

const ANIMATION = !window.matchMedia('(prefers-reduced-motion: reduce)').matches;

@Component({
    selector: 'app-dashboard-overview',
    imports: [ChartCanvas],
    templateUrl: './overview.html',
    styleUrl: './overview.scss',
})
export class DashboardOverview {
    private readonly issueService = inject(IssueService);
    protected readonly auth = inject(AuthService);

    protected readonly stats = signal<IssueStats | null>(null);
    protected readonly loading = signal(false);
    protected readonly error = signal<string | null>(null);

    protected readonly total = computed(() => {
        const stats = this.stats();
        return stats ? STATUSES.reduce((sum, status) => sum + stats.byStatus[status], 0) : 0;
    });

    protected readonly open = computed(() => {
        const stats = this.stats();
        return stats ? stats.byStatus.TODO + stats.byStatus.IN_PROGRESS : 0;
    });

    protected readonly statusChart = computed<ChartConfiguration | null>(() => {
        const stats = this.stats();
        if (!stats) return null;
        return {
            type: 'doughnut',
            data: {
                labels: STATUSES.map((status) => STATUS_LABELS[status]),
                datasets: [{
                    data: STATUSES.map((status) => stats.byStatus[status]),
                    backgroundColor: STATUSES.map((status) => cssColor(STATUS_COLOR[status])),
                }],
            },
            options: {
                responsive: true,
                maintainAspectRatio: false,
                animation: ANIMATION ? undefined : false,
                plugins: { legend: { position: 'bottom' } },
            },
        };
    });

    protected readonly trendChart = computed<ChartConfiguration | null>(() => {
        const stats = this.stats();
        if (!stats) return null;
        return {
            type: 'bar',
            data: {
                labels: stats.lastWeeks.map((week) => weekLabel(week.weekStart)),
                datasets: [
                    {
                        label: 'Create',
                        data: stats.lastWeeks.map((week) => week.created),
                        backgroundColor: cssColor('--bs-primary'),
                    },
                    {
                        label: 'Chiuse',
                        data: stats.lastWeeks.map((week) => week.closed),
                        backgroundColor: cssColor('--bs-dark'),
                    },
                ],
            },
            options: {
                responsive: true,
                maintainAspectRatio: false,
                animation: ANIMATION ? undefined : false,
                scales: { y: { beginAtZero: true, ticks: { precision: 0 } } },
                plugins: { legend: { position: 'bottom' } },
            },
        };
    });

    protected readonly typeChart = computed<ChartConfiguration | null>(() => {
        const stats = this.stats();
        if (!stats) return null;
        return {
            type: 'bar',
            data: {
                labels: TYPES.map((type) => TYPE_LABELS[type]),
                
                datasets: STATUSES.map((status) => ({
                    label: STATUS_LABELS[status],
                    data: TYPES.map((type) => stats.byTypeAndStatus[type][status]),
                    backgroundColor: cssColor(STATUS_COLOR[status]),
                })),
            },
            options: {
                responsive: true,
                maintainAspectRatio: false,
                animation: ANIMATION ? undefined : false,
                scales: {
                    x: { stacked: true },
                    y: { stacked: true, beginAtZero: true, ticks: { precision: 0 } },
                },
                plugins: { legend: { position: 'bottom' } },
            },
        };
    });

    protected readonly priorityChart = computed<ChartConfiguration | null>(() => {
        const stats = this.stats();
        if (!stats) return null;
        return {
            type: 'bar',
            data: {
                labels: PRIORITIES.map((priority) => PRIORITY_LABELS[priority]),
                datasets: [{
                    label: 'Issue aperte',
                    data: PRIORITIES.map((priority) => stats.openByPriority[priority]),
                    backgroundColor: PRIORITIES.map((priority) => cssColor(PRIORITY_COLOR[priority])),
                }],
            },
            options: {
                indexAxis: 'y',
                responsive: true,
                maintainAspectRatio: false,
                animation: ANIMATION ? undefined : false,
                scales: { x: { beginAtZero: true, ticks: { precision: 0 } } },
                plugins: { legend: { display: false } },
            },
        };
    });

    protected readonly statusSummary = computed(() =>
        describe(STATUSES.map((status) => [STATUS_LABELS[status], this.stats()?.byStatus[status] ?? 0])),
    );
    protected readonly trendSummary = computed(() =>
        (this.stats()?.lastWeeks ?? [])
            .map((week) => `settimana del ${weekLabel(week.weekStart)}: ${week.created} create, ${week.closed} chiuse`)
            .join('; '),
    );
    protected readonly typeSummary = computed(() =>
        TYPES.map((type) => `${TYPE_LABELS[type]}: ` + describe(
            STATUSES.map((status) => [STATUS_LABELS[status], this.stats()?.byTypeAndStatus[type][status] ?? 0]),
        )).join('; '),
    );
    protected readonly prioritySummary = computed(() =>
        'Issue aperte per priorità: ' + describe(
            PRIORITIES.map((priority) => [PRIORITY_LABELS[priority], this.stats()?.openByPriority[priority] ?? 0]),
        ),
    );

    constructor() {
        this.load();
    }

    protected load(): void {
        this.loading.set(true);
        this.error.set(null);

        this.issueService.getStats().subscribe({
            next: (stats) => {
                this.stats.set(stats);
                this.loading.set(false);
            },
            error: (error: HttpErrorResponse) => {
                this.error.set(describeHttpError(error));
                this.loading.set(false);
            },
        });
    }
}

function weekLabel(weekStart: string): string {
    const [, month, day] = weekStart.split('-');
    return `${Number(day)}/${Number(month)}`;
}

function describe(pairs: [string, number][]): string {
    return pairs.map(([label, value]) => `${label} ${value}`).join(', ');
}