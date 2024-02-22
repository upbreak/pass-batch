package com.jinwoo.pass.passbatch.adapter.message;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

@Getter
@Setter
@ToString
public class KakaoTalkMessageRequest {

    @JsonProperty("receiver_uuids")
    private List<String> receiverUuids;
    @JsonProperty("template_object")
    private TemplateObject templateObject;

    @Getter
    @Setter
    @ToString
    public static class TemplateObject{
        @JsonProperty("object_type")
        private String objectType;
        private String text;
        private Link link;

        @Getter
        @Setter
        @ToString
        public static class Link{
            @JsonProperty("web_url")
            private String webUrl;
        }
    }

    public KakaoTalkMessageRequest(String uuid, String text) {
        this.templateObject = new TemplateObject();
        this.templateObject.objectType = text;
        this.templateObject.text = text;
        this.templateObject.link = new TemplateObject.Link();
        this.receiverUuids = Collections.singletonList(uuid);
    }
}
