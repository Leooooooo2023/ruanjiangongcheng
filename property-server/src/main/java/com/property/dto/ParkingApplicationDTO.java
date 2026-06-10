package com.property.dto;

import lombok.Data;

/**
 * 停车位申请请求 DTO（业主端使用，仅需传递车位 ID）
 */
@Data
public class ParkingApplicationDTO {

    /** 申请的车位 ID */
    private Integer parkingId;
}
