package com.property.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.property.entity.ParkingApplication;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface ParkingApplicationMapper extends BaseMapper<ParkingApplication> {

    /**
     * 查询所有申请（含车位号、车位位置、业主姓名、业主账号）
     */
    @Select("SELECT pa.*, p.number AS parking_number, p.location AS parking_location, " +
            "o.name AS owner_name, o.username AS owner_username " +
            "FROM parking_application pa " +
            "LEFT JOIN parking p ON pa.parking_id = p.id " +
            "LEFT JOIN owner o ON pa.owner_id = o.id " +
            "ORDER BY pa.create_time DESC")
    List<ParkingApplication> selectAllWithDetails();

    /**
     * 查询指定业主的申请列表
     */
    @Select("SELECT pa.*, p.number AS parking_number, p.location AS parking_location, " +
            "o.name AS owner_name, o.username AS owner_username " +
            "FROM parking_application pa " +
            "LEFT JOIN parking p ON pa.parking_id = p.id " +
            "LEFT JOIN owner o ON pa.owner_id = o.id " +
            "WHERE pa.owner_id = #{ownerId} " +
            "ORDER BY pa.create_time DESC")
    List<ParkingApplication> selectByOwnerId(@Param("ownerId") Integer ownerId);

    /**
     * 查询某个车位的申请中记录（status=0）
     */
    @Select("SELECT pa.*, p.number AS parking_number, p.location AS parking_location, " +
            "o.name AS owner_name, o.username AS owner_username " +
            "FROM parking_application pa " +
            "LEFT JOIN parking p ON pa.parking_id = p.id " +
            "LEFT JOIN owner o ON pa.owner_id = o.id " +
            "WHERE pa.parking_id = #{parkingId} AND pa.status = 0 " +
            "LIMIT 1")
    ParkingApplication selectPendingByParkingId(@Param("parkingId") Integer parkingId);
}
