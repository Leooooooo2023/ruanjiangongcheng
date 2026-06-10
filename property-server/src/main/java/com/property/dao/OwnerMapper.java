package com.property.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.property.entity.Owner;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface OwnerMapper extends BaseMapper<Owner> {

    /**
     * 查询业主列表（含楼栋名称 LEFT JOIN）
     */
    @Select("<script>" +
            "SELECT o.*, b.name AS building_name " +
            "FROM owner o " +
            "LEFT JOIN building b ON o.building_id = b.id " +
            "WHERE o.is_deleted = 0 " +
            "<if test=\"keyword != null and keyword != ''\">" +
            "  AND (o.name LIKE CONCAT('%', #{keyword}, '%') " +
            "       OR o.username LIKE CONCAT('%', #{keyword}, '%') " +
            "       OR o.phone LIKE CONCAT('%', #{keyword}, '%')) " +
            "</if>" +
            "ORDER BY o.create_time DESC " +
            "LIMIT #{offset}, #{size}" +
            "</script>")
    List<Owner> selectOwnerWithBuildingName(@Param("keyword") String keyword,
                                            @Param("offset") int offset,
                                            @Param("size") int size);

    /**
     * 查询业主总数（含关键字搜索）
     */
    @Select("<script>" +
            "SELECT COUNT(*) FROM owner o " +
            "WHERE o.is_deleted = 0 " +
            "<if test=\"keyword != null and keyword != ''\">" +
            "  AND (o.name LIKE CONCAT('%', #{keyword}, '%') " +
            "       OR o.username LIKE CONCAT('%', #{keyword}, '%') " +
            "       OR o.phone LIKE CONCAT('%', #{keyword}, '%')) " +
            "</if>" +
            "</script>")
    Long countOwnerWithBuildingName(@Param("keyword") String keyword);

    /**
     * 查询业主详情（含楼栋名称 LEFT JOIN）
     */
    @Select("SELECT o.*, b.name AS building_name " +
            "FROM owner o " +
            "LEFT JOIN building b ON o.building_id = b.id " +
            "WHERE o.id = #{id}")
    Owner selectOwnerDetailWithBuildingName(@Param("id") Integer id);
}
