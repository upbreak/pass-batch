package com.jinwoo.pass.passbatch.repository.statistics;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@AllArgsConstructor
public class AggregatedStatistics {
    private LocalDateTime statisticsAt;
    private long allCount;
    private long attendedCount;
    private long cancelledCount;

    public void merge(AggregatedStatistics statistics){
        this.allCount += statistics.allCount;
        this.attendedCount += statistics.attendedCount;
        this.cancelledCount += statistics.cancelledCount;
    }
}
