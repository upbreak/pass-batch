package com.jinwoo.pass.passbatch.job.pass;

import com.jinwoo.pass.passbatch.repository.pass.PassEntity;
import com.jinwoo.pass.passbatch.repository.pass.PassStatus;
import jakarta.persistence.EntityManagerFactory;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.database.JpaCursorItemReader;
import org.springframework.batch.item.database.JpaItemWriter;
import org.springframework.batch.item.database.builder.JpaCursorItemReaderBuilder;
import org.springframework.batch.item.database.builder.JpaItemWriterBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

import java.time.LocalDateTime;
import java.util.Map;

@Configuration
public class ExpirePassesJobConfig {

    private final int CHUNK_SIZE = 5;

    private final EntityManagerFactory entityManagerFactory;

    public ExpirePassesJobConfig(EntityManagerFactory entityManagerFactory) {
        this.entityManagerFactory = entityManagerFactory;
    }

    @Bean
    public Job expirePassJob(JobRepository jobRepository, PlatformTransactionManager platformTransactionManager){
        return new JobBuilder("expirePassJob", jobRepository)
                .start(expirePassStep(jobRepository, platformTransactionManager))
                .build();
    }

    @Bean
    public Step expirePassStep(JobRepository jobRepository, PlatformTransactionManager platformTransactionManager){
        return new StepBuilder("expirePassStep", jobRepository)
                .<PassEntity, PassEntity>chunk(CHUNK_SIZE, platformTransactionManager)
                .reader(expirePassItemReader())
                .processor(expirePassItemProcessor())
                .writer(expirePassItemWriter())
                .build();
    }

    @Bean
    @StepScope
    public JpaCursorItemReader<PassEntity> expirePassItemReader(){
        return new JpaCursorItemReaderBuilder<PassEntity>()
                .name("expirePassItemReader")
                .entityManagerFactory(entityManagerFactory)
                .queryString("select p from PassEntity p where p.status = :status and p.endedAt <= :endedAt")
                .parameterValues(Map.of("status", PassStatus.PROGRESSED, "endedAt", LocalDateTime.now()))
                .build();
    }

    @Bean
    public ItemProcessor<PassEntity, PassEntity> expirePassItemProcessor(){
        return passEntity -> {
             passEntity.setStatus(PassStatus.EXPIRED);
             passEntity.setExpiredAt(LocalDateTime.now());
             return passEntity;
        };
    }

    @Bean
    public JpaItemWriter<PassEntity> expirePassItemWriter(){
        return new JpaItemWriterBuilder<PassEntity>()
                .entityManagerFactory(entityManagerFactory)
                .build();
    }
}
