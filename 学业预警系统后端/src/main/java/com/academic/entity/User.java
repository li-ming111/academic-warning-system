package com.academic.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;
import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@TableName("users")
public class User implements Serializable {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String username;

    private String name;

    @TableField("password")
    private String password;

    private String email;

    private String phone;

    private Integer role;

    private Integer status;

    @TableField("updated_at")
    private LocalDateTime updatedAt;

}
