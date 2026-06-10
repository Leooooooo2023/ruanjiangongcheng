package com.property.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.property.entity.Parking;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface ParkingMapper extends BaseMapper<Parking> {

    /**
     * 分页查询停车位（含业主姓名、账号 LEFT JOIN）
     */
    @Select("<script>" +
            "SELECT p.*, o.name AS owner_name, o.username AS owner_username " +
            "FROM parking p " +
            "LEFT JOIN owner o ON p.owner_id = o.id " +
            "<where>" +
            "<if test=\"keyword != null and keyword != ''\">" +
            "  AND (p.number LIKE CONCAT('%', #{keyword}, '%') " +
            "       OR p.location LIKE CONCAT('%', #{keyword}, '%')) " +
            "</if>" +
            "</where>" +
            "ORDER BY p.create_time DESC " +
            "LIMIT #{offset}, #{size}" +
            "</script>")
    List<Parking> selectParkingWithOwner(@Param("keyword") String keyword,
                                          @Param("offset") int offset,
                                          @Param("size") int size);

    /**
     * 查询停车位总数
     */
    @Select("<script>" +
            "SELECT COUNT(*) FROM parking p " +
            "<where>" +
            "<if test=\"keyword != null and keyword != ''\">" +
            "  AND (p.number LIKE CONCAT('%', #{keyword}, '%') " +
            "       OR p.location LIKE CONCAT('%', #{keyword}, '%')) " +
            "</if>" +
            "</where>" +
            "</script>")
    Long countParkingWithOwner(@Param("keyword") String keyword);
}
