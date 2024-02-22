package com.jinwoo.pass.passbatch.repository.user;

import com.jinwoo.pass.passbatch.repository.BaseEntity;
import com.vladmihalcea.hibernate.type.json.JsonStringType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.Type;
import org.hibernate.type.SqlTypes;

import java.util.Map;

@Getter
@Setter
@ToString
@Entity
@Table(name = "user")
public class UserEntity extends BaseEntity {

    @Id
    private String userId;

    private String userName;

    @Enumerated(EnumType.STRING)
    private UserStatus status;
    private String phone;

    @JdbcTypeCode(SqlTypes.JSON)
    private Map<String, Object> meta;

    public String getUuid(){
        String uuid = null;
        if(meta.containsValue("uuid")){
            uuid = String.valueOf(meta.get(uuid));
        }

        return uuid;
    }
}
