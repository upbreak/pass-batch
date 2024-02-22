package com.jinwoo.pass.passbatch.job.notification;

import com.jinwoo.pass.passbatch.adapter.message.KakaoTalkMessageAdapter;
import com.jinwoo.pass.passbatch.repository.notification.NotificationEntity;
import com.jinwoo.pass.passbatch.repository.notification.NotificationRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Component
public class SendNotificationItemWriter implements ItemWriter<NotificationEntity> {

    private final NotificationRepository notificationRepository;
    private final KakaoTalkMessageAdapter kakaoTalkMessageAdapter;

    public SendNotificationItemWriter(NotificationRepository notificationRepository, KakaoTalkMessageAdapter kakaoTalkMessageAdapter) {
        this.notificationRepository = notificationRepository;
        this.kakaoTalkMessageAdapter = kakaoTalkMessageAdapter;
    }

    @Override
    public void write(Chunk<? extends NotificationEntity> chunk) throws Exception {
        int count = 0;

        for(NotificationEntity entity : chunk){
            boolean successful = kakaoTalkMessageAdapter.sendKakaoTalkMessage(entity.getUuid(), entity.getText());

            if(successful){
                entity.setSent(true);
                entity.setSentAt(LocalDateTime.now());
                notificationRepository.save(entity);
                count++;
            }
        }

        log.info("SendNotificationItemWriter - write : 수업 전 알람 {}/{}건 전송 성공", count, chunk.size());
    }
}
