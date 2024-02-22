package com.jinwoo.pass.passbatch.job.notification;

import com.jinwoo.pass.passbatch.adapter.message.KakaoTalkMessageAdapter;
import com.jinwoo.pass.passbatch.config.KakaoTalkMessageConfig;
import com.jinwoo.pass.passbatch.config.TestBatchConfig;
import com.jinwoo.pass.passbatch.repository.booking.BookingEntity;
import com.jinwoo.pass.passbatch.repository.booking.BookingRepository;
import com.jinwoo.pass.passbatch.repository.booking.BookingStatus;
import com.jinwoo.pass.passbatch.repository.notification.NotificationRepository;
import com.jinwoo.pass.passbatch.repository.pass.PassEntity;
import com.jinwoo.pass.passbatch.repository.pass.PassRepository;
import com.jinwoo.pass.passbatch.repository.pass.PassStatus;
import com.jinwoo.pass.passbatch.repository.user.UserEntity;
import com.jinwoo.pass.passbatch.repository.user.UserRepository;
import com.jinwoo.pass.passbatch.repository.user.UserStatus;
import org.junit.jupiter.api.Test;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.batch.test.context.SpringBatchTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.testcontainers.shaded.org.apache.commons.lang3.RandomStringUtils;

import java.time.LocalDateTime;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBatchTest
@SpringBootTest
@ActiveProfiles("test")
@ContextConfiguration(classes = {
        SendNotificationBeforeClassJobConfig.class,
        TestBatchConfig.class,
        SendNotificationItemWriter.class,
        KakaoTalkMessageConfig.class,
        KakaoTalkMessageAdapter.class
})
class SendNotificationBeforeClassJobConfigTest {
    public final JobLauncherTestUtils jobLauncherTestUtils;
    public final PassRepository passRepository;
    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;

    public SendNotificationBeforeClassJobConfigTest(
            @Autowired JobLauncherTestUtils jobLauncherTestUtils
            , @Autowired BookingRepository bookingRepository
            , @Autowired UserRepository userRepository
            , @Autowired PassRepository passRepository
    ) {
        this.jobLauncherTestUtils = jobLauncherTestUtils;
        this.bookingRepository = bookingRepository;
        this.userRepository = userRepository;
        this.passRepository = passRepository;
    }

    @Test
    void given_when_then() {
        // Given
        addBookingEntity();

        // When
        JobExecution jobExecution = jobLauncherTestUtils.launchStep("addNotificationStep");

        // Then
        assertThat(jobExecution).isEqualTo(ExitStatus.COMPLETED);
    }

    private void addBookingEntity() {
        final LocalDateTime now = LocalDateTime.now();
        final String userId = "A100" + RandomStringUtils.randomNumeric(4);

        UserEntity userEntity = new UserEntity();
        userEntity.setUserId(userId);
        userEntity.setUserName("김영희");
        userEntity.setStatus(UserStatus.ACTIVE);
        userEntity.setPhone("01033334444");
        userEntity.setMeta(Map.of("uuid", "abcd1234"));
        userRepository.save(userEntity);

        PassEntity passEntity = new PassEntity();
        passEntity.setPackageSeq(1);
        passEntity.setUserId(userId);
        passEntity.setStatus(PassStatus.PROGRESSED);
        passEntity.setRemainingCount(10);
        passEntity.setStartedAt(now.minusDays(60));
        passEntity.setEndedAt(now.minusDays(1));
        passRepository.save(passEntity);

        BookingEntity bookingEntity = new BookingEntity();
        bookingEntity.setPassSeq(passEntity.getPassSeq());
        bookingEntity.setUserId(userId);
        bookingEntity.setStatus(BookingStatus.READY);
        bookingEntity.setStartedAt(now.plusMinutes(10));
        bookingEntity.setEndedAt(bookingEntity.getStartedAt().plusMinutes(50));
        bookingRepository.save(bookingEntity);

    }
}