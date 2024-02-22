package com.jinwoo.pass.passbatch.job.statistics;

import com.jinwoo.pass.passbatch.repository.statistics.AggregatedStatistics;
import com.jinwoo.pass.passbatch.repository.statistics.StatisticsRepository;
import com.jinwoo.pass.passbatch.util.CustomCSVWriter;
import com.jinwoo.pass.passbatch.util.LocalDateTimeUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Component
@StepScope
public class MakeDailyStatisticsTasklet implements Tasklet {

    @Value("#{jobParameters[from]}")
    private String fromString;

    @Value("#{jobParameters[to]}")
    private String toString;

    private final StatisticsRepository statisticsRepository;

    @Override
    public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
        final LocalDateTime from = LocalDateTimeUtils.parse(fromString);
        final LocalDateTime to = LocalDateTimeUtils.parse(toString);

        List<AggregatedStatistics> aggregatedStatistics = statisticsRepository.findByStatisticsAtBetweenAndGroupBy(from, to);

        List<String[]> data = new ArrayList<>();
        data.add(new String[]{"statisticsAt", "allCount", "attendedCount", "cancelledCount"});
        for(AggregatedStatistics statistics : aggregatedStatistics){
            data.add(new String[]{
                    LocalDateTimeUtils.format(statistics.getStatisticsAt())
                    , String.valueOf(statistics.getAllCount())
                    , String.valueOf(statistics.getAttendedCount())
                    , String.valueOf(statistics.getCancelledCount())
            });
        }

        CustomCSVWriter.write("daily_statistics_" + LocalDateTimeUtils.format(from, LocalDateTimeUtils.YYYY_MM_DD) + ".csv", data);

        return RepeatStatus.FINISHED;
    }
}
