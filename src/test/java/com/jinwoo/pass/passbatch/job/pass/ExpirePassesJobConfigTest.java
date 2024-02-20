package com.jinwoo.pass.passbatch.job.pass;

import com.jinwoo.pass.passbatch.config.TestBatchConfig;
import com.jinwoo.pass.passbatch.repository.pass.PassEntity;
import com.jinwoo.pass.passbatch.repository.pass.PassRepository;
import com.jinwoo.pass.passbatch.repository.pass.PassStatus;
import org.junit.jupiter.api.Test;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobInstance;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.batch.test.context.SpringBatchTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static org.assertj.core.api.Assertions.*;


@SpringBatchTest
@SpringBootTest
@ActiveProfiles("test")
@ContextConfiguration(classes = {ExpirePassesJobConfig.class, TestBatchConfig.class})
class ExpirePassesJobConfigTest {

    public final JobLauncherTestUtils jobLauncherTestUtils;
    public final PassRepository passRepository;

    public ExpirePassesJobConfigTest(
            @Autowired JobLauncherTestUtils jobLauncherTestUtils
            , @Autowired PassRepository passRepository) {
        this.jobLauncherTestUtils = jobLauncherTestUtils;
        this.passRepository = passRepository;
    }

    @Test
    void givenNewPassEntities_whenJobLaunch_thenExpireJobExecution() throws Exception {
        // Given
        addPassEntities(10);

        // When
        JobExecution jobExecution = jobLauncherTestUtils.launchJob();
        JobInstance jobInstance = jobExecution.getJobInstance();

        // Then
        assertThat(jobExecution.getExitStatus()).isEqualTo(ExitStatus.COMPLETED);
        assertThat(jobInstance.getJobName()).isEqualTo("expirePassJob");
    }

    private void addPassEntities(int size) {
        final LocalDateTime now = LocalDateTime.now();
        final Random random = new Random();

        List<PassEntity> passEntities = new ArrayList<>();
        for (int i = 0; i < size; ++i) {
            PassEntity passEntity = new PassEntity();
            passEntity.setPackageSeq(1);
            passEntity.setUserId("A" + 1000000 + i);
            passEntity.setStatus(PassStatus.PROGRESSED);
            passEntity.setRemainingCount(random.nextInt(11));
            passEntity.setStartedAt(now.minusDays(60));
            passEntity.setEndedAt(now.minusDays(1));
            passEntities.add(passEntity);

        }
        passRepository.saveAll(passEntities);

    }
}