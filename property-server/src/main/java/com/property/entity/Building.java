package com.property.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("building")
public class Building {

    @TableId(type = IdType.AUTO)
    private Integer id;

    private String name;

    private Integer units;

    private Integer floors;

    private String description;

    @TableLogic
    private Integer isDeleted;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}
