package com.property.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.property.dto.PageQuery;
import com.property.dto.PageResult;
import com.property.dto.RepairDTO;
import com.property.entity.Repair;

public interface RepairService extends IService<Repair> {

    /**
     * 管理员分页查询
     */
    PageResult<Repair> queryPage(PageQuery query);

    /**
     * 管理员查看详情
     */
    Repair getDetail(Integer id);

    /**
     * 分配维修员工
     */
    void assignEmployee(Integer repairId, Integer employeeId);

    /**
     * 更新状态
     */
    void updateStatus(Integer repairId, Integer status);

    /**
     * 业主提交报修
     */
    void submit(Integer ownerId, RepairDTO dto);

    /**
     * 业主查看自己的报修列表
     */
    PageResult<Repair> queryByOwner(Integer ownerId, PageQuery query);

    /**
     * 业主查看自己的报修详情
     */
    Repair getOwnerDetail(Integer ownerId, Integer repairId);

    /**
     * 业主评价
     */
    void rate(Integer ownerId, Integer repairId, Integer rating);
}
