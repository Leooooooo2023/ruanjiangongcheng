package com.property.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("repair")
public class Repair {

    @TableId(type = IdType.AUTO)
    private Integer id;

    private Integer ownerId;

    private String title;

    private String content;

    private String image;

    private Integer status;

    private Integer employeeId;

    private Integer rating;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    /** 员工姓名（非数据库字段，查询时 LEFT JOIN 填充） */
    @TableField(exist = false)
    private String employeeName;

    /** 业主姓名（非数据库字段，查询时 LEFT JOIN 填充） */
    @TableField(exist = false)
    private String ownerName;
}
