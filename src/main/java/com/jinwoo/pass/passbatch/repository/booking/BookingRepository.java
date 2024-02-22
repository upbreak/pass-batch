package com.jinwoo.pass.passbatch.repository.booking;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

public interface BookingRepository extends JpaRepository<BookingEntity, Integer> {

    @Transactional
    @Modifying
    @Query(value = "UPDATE BookingEntity " +
            "          SET usedPass = :usedPass " +
            "            , modifiedAt = CURRENT_TIMESTAMP " +
            "        WHERE passSeq = :passSeq")
    int updateUsedPass(boolean usedPass, Integer passSeq);
}
