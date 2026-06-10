package com.property.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.property.entity.Repair;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface RepairMapper extends BaseMapper<Repair> {

    /**
     * 分页查询报修列表（含员工姓名、业主姓名 LEFT JOIN）
     */
    @Select("<script>" +
            "SELECT r.*, e.name AS employee_name, o.name AS owner_name " +
            "FROM repair r " +
            "LEFT JOIN employee e ON r.employee_id = e.id " +
            "LEFT JOIN owner o ON r.owner_id = o.id " +
            "<where>" +
            "<if test=\"keyword != null and keyword != ''\">" +
            "  AND r.title LIKE CONCAT('%', #{keyword}, '%') " +
            "</if>" +
            "</where>" +
            "ORDER BY r.create_time DESC " +
            "LIMIT #{offset}, #{size}" +
            "</script>")
    List<Repair> selectRepairWithNames(@Param("keyword") String keyword,
                                       @Param("offset") int offset,
                                       @Param("size") int size);

    /**
     * 查询报修总数
     */
    @Select("<script>" +
            "SELECT COUNT(*) FROM repair r " +
            "<where>" +
            "<if test=\"keyword != null and keyword != ''\">" +
            "  AND r.title LIKE CONCAT('%', #{keyword}, '%') " +
            "</if>" +
            "</where>" +
            "</script>")
    Long countRepairWithNames(@Param("keyword") String keyword);
}
