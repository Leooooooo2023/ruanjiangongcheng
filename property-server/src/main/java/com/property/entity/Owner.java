package com.property.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("owner")
public class Owner {

    @TableId(type = IdType.AUTO)
    private Integer id;

    private String username;

    private String password;

    private String name;

    private String phone;

    private String email;

    private Integer buildingId;

    private String unit;

    private String room;

    @TableLogic
    private Integer isDeleted;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    /** 楼栋名称（非数据库字段，查询时 LEFT JOIN 填充） */
    @TableField(exist = false)
    private String buildingName;

    /** 登录失败次数 */
    private Integer loginFailCount;

    /** 锁定截止时间 */
    private LocalDateTime lockUntil;
}
