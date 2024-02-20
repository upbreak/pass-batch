package com.jinwoo.pass.passbatch.job.pass;

import com.jinwoo.pass.passbatch.repository.pass.*;
import com.jinwoo.pass.passbatch.repository.user.UserGroupMappingEntity;
import com.jinwoo.pass.passbatch.repository.user.UserGroupMappingRepository;
import lombok.extern.java.Log;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
public class AddPassTasklet implements Tasklet {

    private final PassRepository passRepository;
    private final BulkPassRepository bulkPassRepository;
    private final UserGroupMappingRepository userGroupMappingRepository;

    public AddPassTasklet(PassRepository passRepository, BulkPassRepository bulkPassRepository, UserGroupMappingRepository userGroupMappingRepository) {
        this.passRepository = passRepository;
        this.bulkPassRepository = bulkPassRepository;
        this.userGroupMappingRepository = userGroupMappingRepository;
    }

    @Override
    public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
        // 이용권 시작 1일전 User group 내 각 사용자에게 이용권을 추가해준다.
        final LocalDateTime startedAt = LocalDateTime.now().minusDays(1);
        final List<BulkPassEntity> bulkPassEntities = bulkPassRepository.findByStatusAndStartedAtGreaterThan(BulkPassStatus.READY, startedAt);

        int count = 0;
        // 대량 이용권 정보를 돌면서 User group에 속한 userId를 조회하고 해당 userId로 이용권을 추가한다.
        for(BulkPassEntity bulkPass : bulkPassEntities){
            final List<String> userIds = userGroupMappingRepository.findByUserGroupId(bulkPass.getUserGroupId()).stream()
                    .map(UserGroupMappingEntity::getUserId)
                    .toList();

            count += addPass(bulkPass, userIds);
            bulkPass.setStatus(BulkPassStatus.COMPLETED);
        }
        log.info("addPassTasklet execute: 이용권 {}건 추가 완료, startedAt={}", count, startedAt);
        return RepeatStatus.FINISHED;
    }

    public int addPass(BulkPassEntity bulkPassEntity, List<String> userIds){
        List<PassEntity> passEntities = new ArrayList<>();
        for(String userId : userIds){
            PassEntity passEntity = PassModelMapper.INSTANCE.toPassEntity(bulkPassEntity, userId);
            passEntities.add(passEntity);
        }

        return passRepository.saveAll(passEntities).size();
    }
}
