package com.jinwoo.pass.passbatch.job.pass;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
public class AddPassJobConfig {

    private final AddPassTasklet addPassTasklet;

    public AddPassJobConfig(AddPassTasklet addPassTasklet) {
        this.addPassTasklet = addPassTasklet;
    }

    @Bean
    public Job addPassJob(JobRepository jobRepository, PlatformTransactionManager platformTransactionManager){
        return new JobBuilder("addPassJob", jobRepository)
                .start(addPassStep(jobRepository, platformTransactionManager))
                .build();
    }

    @Bean
    public Step addPassStep(JobRepository jobRepository, PlatformTransactionManager platformTransactionManager){
        return new StepBuilder("addPassStep", jobRepository)
                .tasklet(addPassTasklet, platformTransactionManager)
                .build();
    }
}
