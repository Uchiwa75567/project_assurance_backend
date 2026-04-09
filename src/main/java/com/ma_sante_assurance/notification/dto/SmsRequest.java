package com.ma_sante_assurance.notification.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SmsRequest {

    @JsonProperty("sender_name")
    private String senderName;

    @JsonProperty("sms_type")
    private String smsType;

    private String phone;

    private String text;

    @JsonProperty("scheduled_at")
    private String scheduledAt;
}
