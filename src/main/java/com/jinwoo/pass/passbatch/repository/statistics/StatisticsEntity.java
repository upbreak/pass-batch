package com.jinwoo.pass.passbatch.repository.statistics;

import com.jinwoo.pass.passbatch.repository.booking.BookingEntity;
import com.jinwoo.pass.passbatch.repository.booking.BookingStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.cglib.core.Local;

import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@Entity
@Table(name = "statistics")
public class StatisticsEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer statisticsSeq;

    private LocalDateTime statisticsAt;
    private int allCount;
    private int attendedCount;
    private int cancelledCount;

    public static StatisticsEntity create(final BookingEntity bookingEntity){
        StatisticsEntity statisticsEntity = new StatisticsEntity();
        statisticsEntity.setStatisticsAt(bookingEntity.getStatisticsAt());
        statisticsEntity.setAllCount(1);

        if(bookingEntity.isAttended()){
            statisticsEntity.setAttendedCount(1);
        }

        if(bookingEntity.getStatus().equals(BookingStatus.CANCELLED)){
            statisticsEntity.setCancelledCount(1);
        }

        return statisticsEntity;
    }

    public void add(final BookingEntity bookingEntity){
        this.allCount++;

        if(bookingEntity.isAttended()){
            this.attendedCount++;
        }

        if(bookingEntity.getStatus().equals(BookingStatus.CANCELLED)){
            this.cancelledCount++;
        }
    }

}
