package com.academic.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("support_resources")
public class SupportResource extends BaseEntity {

    private String name;  // 资源名称

    private String type;  // 资源类型：group（帮扶小组）、lecture（讲座）、mentor（朋辈导师）

    private String courseCode;  // 相关课程代码

    private String description;  // 资源描述

    private String link;  // 资源链接

    private String status;  // 状态：active（活跃）、inactive（不活跃）

    private String schedule;  // 时间安排（讲座用）

    private Long mentorId;  // 导师ID（朋辈导师用）

}
