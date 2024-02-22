package com.jinwoo.pass.passbatch.job.pass;

import com.jinwoo.pass.passbatch.repository.booking.BookingEntity;
import com.jinwoo.pass.passbatch.repository.booking.BookingRepository;
import com.jinwoo.pass.passbatch.repository.booking.BookingStatus;
import com.jinwoo.pass.passbatch.repository.pass.PassEntity;
import com.jinwoo.pass.passbatch.repository.pass.PassRepository;
import jakarta.persistence.EntityManagerFactory;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.integration.async.AsyncItemProcessor;
import org.springframework.batch.integration.async.AsyncItemWriter;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.JpaCursorItemReader;
import org.springframework.batch.item.database.builder.JpaCursorItemReaderBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.transaction.PlatformTransactionManager;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.Future;

@Configuration
public class UsePassJobConfig {
    private final int CHUNK_SIZE = 10;

    private final EntityManagerFactory entityManagerFactory;
    private final PassRepository passRepository;
    private final BookingRepository bookingRepository;

    public UsePassJobConfig(EntityManagerFactory entityManagerFactory, PassRepository passRepository, BookingRepository bookingRepository) {
        this.entityManagerFactory = entityManagerFactory;
        this.passRepository = passRepository;
        this.bookingRepository = bookingRepository;
    }

    @Bean
    public Job Job(JobRepository jobRepository, PlatformTransactionManager platformTransactionManager){
        return new JobBuilder("Job", jobRepository)
                .start(Step(jobRepository, platformTransactionManager))
                .build();
    }

    @Bean
    public Step Step(JobRepository jobRepository, PlatformTransactionManager platformTransactionManager){
        return new StepBuilder("Step", jobRepository)
                .<BookingEntity, Future<BookingEntity>>chunk(CHUNK_SIZE, platformTransactionManager)
                .reader(usePassItemReader())
                .processor(usePassAsyncItemProcessor())
                .writer(usePassAsyncItemWriter())
                .build();

    }

    @Bean
    public JpaCursorItemReader<BookingEntity> usePassItemReader(){
        return new JpaCursorItemReaderBuilder<BookingEntity>()
                .name("usePassItemReader")
                .entityManagerFactory(entityManagerFactory)
                .queryString("select b from BookingEntity b join fetch b.passEntity where b.status = :status and b.usedPass = false and b.endedAt < :endedAt")
                .parameterValues(Map.of("status", BookingStatus.COMPLETED, "endedAt", LocalDateTime.now()))
                .build();
    }

    @Bean
    public AsyncItemProcessor<BookingEntity, BookingEntity> usePassAsyncItemProcessor(){
        AsyncItemProcessor<BookingEntity, BookingEntity> asyncItemProcessor = new AsyncItemProcessor<>();
        asyncItemProcessor.setDelegate(usePassItemProcessor());
        asyncItemProcessor.setTaskExecutor(new SimpleAsyncTaskExecutor());
        return asyncItemProcessor;
    }

    @Bean
    public ItemProcessor<BookingEntity, BookingEntity> usePassItemProcessor(){
        return bookingEntity -> {
            PassEntity passEntity = bookingEntity.getPassEntity();
            passEntity.setRemainingCount(passEntity.getRemainingCount() - 1);

            bookingEntity.setPassEntity(passEntity);
            bookingEntity.setUsedPass(true);

            return bookingEntity;
        };
    }

    @Bean
    public AsyncItemWriter<BookingEntity> usePassAsyncItemWriter(){
        AsyncItemWriter<BookingEntity> asyncItemWriter = new AsyncItemWriter<>();
        asyncItemWriter.setDelegate(usePassItemWriter());
        return asyncItemWriter;
    }

    @Bean
    public ItemWriter<BookingEntity> usePassItemWriter(){
        return bookingEntities -> {
            for(BookingEntity bookingEntity : bookingEntities){
                int updateCount = passRepository.updateRemainingCount(bookingEntity.getPassSeq(), bookingEntity.getPassEntity().getRemainingCount());

                if(updateCount > 0){
                    bookingRepository.updateUsedPass(bookingEntity.isUsedPass(), bookingEntity.getPassSeq());
                }
            }
        };
    }
}
