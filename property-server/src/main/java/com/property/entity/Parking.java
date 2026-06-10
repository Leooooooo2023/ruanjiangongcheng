package com.property.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@TableName("parking")
public class Parking {

    @TableId(type = IdType.AUTO)
    private Integer id;

    private String number;

    private String location;

    private Integer status;

    private Integer ownerId;

    private LocalDate startDate;

    private LocalDate endDate;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    /** 业主姓名（非数据库字段，查询时 LEFT JOIN 填充） */
    @TableField(exist = false)
    private String ownerName;

    /** 业主账号（非数据库字段，查询时 LEFT JOIN 填充） */
    @TableField(exist = false)
    private String ownerUsername;
}
