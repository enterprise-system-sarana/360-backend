package com.saranaresturantsystem.dto;

import lombok.*;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class ResponseCodeDto implements Serializable {
    private Long id;
    private String code;
    private String httpStatus;
    private String key;
    private String type;
    private String description;
    private String message;
    private String messageKm;
    private String messageCn;
    private String status;
    public static Map<String, String> getErrorMessage(ResponseCodeDto responseCode) {
        Map<String, String> message = new HashMap<>();

        // Base message (default to empty string if null)
        String baseMessage = StringUtils.defaultIfEmpty(responseCode.message, "");

        // Khmer message: use Khmer if present, otherwise fallback to baseMessage
        String kmMessage = StringUtils.defaultIfEmpty(responseCode.messageKm, baseMessage);

        // Chinese message: use Chinese if present, otherwise fallback to baseMessage
        String cnMessage = StringUtils.defaultIfEmpty(responseCode.messageCn, baseMessage);

        message.put("message", baseMessage);
        message.put("messageKm", kmMessage);
        message.put("messageCn", cnMessage);
        message.put("httpStatus", responseCode.httpStatus);

        return message;
    }

    public void getDefault() {
        this.code = "SUCCESS";
        this.message = "Default message Success";
        this.messageKm = "សារលំនាំដើមជោគជ័យ";
        this.messageCn = "默认消息成功";
        this.httpStatus = "200";
    }
}
