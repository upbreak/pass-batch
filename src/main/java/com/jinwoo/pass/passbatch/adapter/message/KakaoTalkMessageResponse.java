package com.jinwoo.pass.passbatch.adapter.message;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Getter
@ToString
public class KakaoTalkMessageResponse {
    @JsonProperty("successful_receiver_uuids")
    private List<String> successfulReceiverUuids;
}
