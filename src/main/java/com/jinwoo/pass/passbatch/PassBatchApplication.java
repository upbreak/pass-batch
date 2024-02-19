package com.jinwoo.pass.passbatch;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.transaction.PlatformTransactionManager;

/**
 * 스프링부트3.x 부터 spring batch5사용 이후 변경점
 * @EnableBatchProcessing 어노테이션을 사용하면 오히려 의도한 것과 다르게 작동한다.
 * 만일 Spring Boot 없이 Spring Batch만 사용하는 프로젝트라면 기존과 동일하게 @EnaleBatchProcessing 어노테이션을 사용한다.
 * JobBuilderFactory, StepBuilderFactory는 사용이 중지되었고 -> JobBuilder, StepBuilder 사용을 하게 되었다.
 */
//@EnableBatchProcessing
@SpringBootApplication
public class PassBatchApplication {

	@Bean
	public Step passStep(JobRepository jobRepository, PlatformTransactionManager platformTransactionManager){
		return new StepBuilder("passStep", jobRepository)
				.tasklet(new Tasklet() {
					@Override
					public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
						System.out.println("Execute PassStep");
						return RepeatStatus.FINISHED;
					}
				}, platformTransactionManager)
				.build();
	}

	@Bean
	public Job passJob(JobRepository jobRepository, PlatformTransactionManager platformTransactionManager){
		return new JobBuilder("passJob", jobRepository)
				.start(passStep(jobRepository, platformTransactionManager))
				.build();
	}

	public static void main(String[] args) {
		SpringApplication.run(PassBatchApplication.class, args);
	}

}
