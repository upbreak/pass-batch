package com.jinwoo.pass.passbatch.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@Configuration
public class JpaConfig {
    // JPA auditing을 활성화. entity의 생성일시와 수정일시를 자동화하는 용도로 사용.
    // 생성자, 수정자는 사용하지 않기에 AuditorAware을 빈으로 등록하지 않음.
}
