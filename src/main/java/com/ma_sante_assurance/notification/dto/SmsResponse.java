package com.ma_sante_assurance.notification.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class SmsResponse {

    private String type;
    private String text;
    private String phone;

    @JsonAlias({"sender_name", "senderName"})
    private String senderName;

    @JsonAlias({"message_id", "messageId"})
    private String messageId;

    @JsonAlias({"scheduled_at", "scheduledAt"})
    private String scheduledAt;

    @JsonAlias({"sendtext_sms_count", "sendtextSmsCount"})
    private Integer sendtextSmsCount;

    @JsonAlias({"status_id", "statusId"})
    private Integer statusId;

    @JsonAlias({"status_description", "statusDescription"})
    private String statusDescription;
}
