package com.jinwoo.pass.passbatch.config;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "kakaotalk")
public class KakaoTalkMessageConfig {
    private String host;
    private String token;
}
