import { Component, DestroyRef, ElementRef, afterRenderEffect, inject, input, viewChild } from '@angular/core';
import {
    ArcElement,
    BarController,
    BarElement,
    CategoryScale,
    Chart,
    ChartConfiguration,
    DoughnutController,
    Legend,
    LinearScale,
    Tooltip,
} from 'chart.js';

Chart.register(ArcElement, BarController, BarElement, CategoryScale, DoughnutController, Legend, LinearScale, Tooltip);

@Component({
    selector: 'app-chart-canvas',
    templateUrl: './chart-canvas.html',
    host: { class: 'd-block position-relative h-100' }, 
})
export class ChartCanvas {
    readonly config = input.required<ChartConfiguration>(); 
    readonly label = input.required<string>(); 

    private readonly canvas = viewChild.required<ElementRef<HTMLCanvasElement>>('canvas'); 
    private chart?: Chart; 

    constructor() {
        afterRenderEffect(() => {
            const config = this.config(); 
            this.chart?.destroy(); 
            this.chart = new Chart(this.canvas().nativeElement, config);
        }); 

        inject(DestroyRef).onDestroy(() => this.chart?.destroy()); 
    }
}