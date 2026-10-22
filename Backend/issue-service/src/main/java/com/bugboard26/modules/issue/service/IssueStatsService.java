package com.bugboard26.modules.issue.service;

import com.bugboard26.modules.issue.dto.IssueStatsResponse;
import com.bugboard26.modules.issue.dto.IssueStatsResponse.WeekTrend;
import com.bugboard26.modules.issue.model.CommentKind;
import com.bugboard26.modules.issue.model.IssuePriority;
import com.bugboard26.modules.issue.model.IssueStatus;
import com.bugboard26.modules.issue.model.IssueType;
import com.bugboard26.modules.issue.repository.CommentRepository;
import com.bugboard26.modules.issue.repository.IssueRepository;
import com.bugboard26.modules.issue.repository.PriorityCount;
import com.bugboard26.modules.issue.repository.TypeStatusCount;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

@Service 
@RequiredArgsConstructor 
public class IssueStatsService {
    
    private static final ZoneId ZONE = ZoneId.of("Europe/Rome");
    private static final int WEEKS = 8; 
    private static final List<IssueStatus> OPEN = List.of(IssueStatus.TODO, IssueStatus.IN_PROGRESS); 

    private final IssueRepository issueRepository; 
    private final CommentRepository commentRepository; 

    @Transactional(readOnly = true)
    public IssueStatsResponse getStats() {
        Map<IssueStatus, Long> byStatus = zeros(IssueStatus.class); 
        Map<IssueType, Map<IssueStatus, Long>> byTypeAndStatus = new EnumMap<>(IssueType.class); 
        for (IssueType type : IssueType.values()) {
            byTypeAndStatus.put(type, zeros(IssueStatus.class)); 
        }

        for (TypeStatusCount row : issueRepository.countByTypeAndStatus()) {
            byTypeAndStatus.get(row.type()).put(row.status(), row.total()); 
            byStatus.merge(row.status(), row.total(), Long::sum);
        }

        Map<IssuePriority, Long> openByPriority = zeros(IssuePriority.class); 
        for (PriorityCount row : issueRepository.countByPriorityForStatuses(OPEN)) {
            if (row.priority() != null) {
                openByPriority.put(row.priority(), row.total());
            }
        }

        return new IssueStatsResponse(byStatus, byTypeAndStatus, openByPriority, lastWeeks()); 
    }

    private List<WeekTrend> lastWeeks() {
        LocalDate firstWeek = LocalDate.now(ZONE)
            .with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))
            .minusWeeks(WEEKS - 1); 
        Instant since = firstWeek.atStartOfDay(ZONE).toInstant(); 

        long[] created = countPerWeek(issueRepository.findCreationTimesSince(since), firstWeek);
        long[] closed = countPerWeek(
                commentRepository.findTransitionTimesSince(CommentKind.STATUS_CHANGE, IssueStatus.CLOSED, since), 
                        firstWeek); 
        
        List<WeekTrend> weeks = new ArrayList<>(); 
        for(int i = 0; i < WEEKS; i++) {
            weeks.add(new WeekTrend(firstWeek.plusWeeks(i), created[i], closed[i]));
        }
        return weeks; 
    }

    private long[] countPerWeek(List<Instant> times, LocalDate firstWeek) {
        long[] counts = new long[WEEKS]; 
        for (Instant time : times) {
            LocalDate day = time.atZone(ZONE).toLocalDate(); 
            int week = (int) ChronoUnit.WEEKS.between(firstWeek, day); 
            if (week >= 0 && week < WEEKS) {
                counts[week]++; 
            }
        }
        return counts; 
    }

    private static <E extends Enum<E>> Map<E, Long> zeros(Class<E> type) {
        Map<E, Long> map = new EnumMap<>(type); 
        for (E value : type.getEnumConstants()) {
            map.put(value, 0L); 
        }
        return map; 
    }
}
