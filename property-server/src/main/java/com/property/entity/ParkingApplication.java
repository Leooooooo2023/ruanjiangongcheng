package com.property.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("parking_application")
public class ParkingApplication {

    @TableId(type = IdType.AUTO)
    private Integer id;

    /** 车位 ID */
    private Integer parkingId;

    /** 业主 ID */
    private Integer ownerId;

    /** 状态：0=待审核, 1=通过, 2=拒绝 */
    private Integer status;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    // ===== 以下为非数据库字段，查询时 LEFT JOIN 填充 =====

    /** 车位号 */
    @TableField(exist = false)
    private String parkingNumber;

    /** 车位位置 */
    @TableField(exist = false)
    private String parkingLocation;

    /** 业主姓名 */
    @TableField(exist = false)
    private String ownerName;

    /** 业主账号 */
    @TableField(exist = false)
    private String ownerUsername;
}
