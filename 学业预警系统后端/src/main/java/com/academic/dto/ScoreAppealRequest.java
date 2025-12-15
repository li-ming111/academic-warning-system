package com.academic.dto;

import lombok.Data;

@Data
public class ScoreAppealRequest {

    private Long warningId;  // 预警ID

    private String reason;  // 申诉原因

    private String attachments;  // 附件

}
