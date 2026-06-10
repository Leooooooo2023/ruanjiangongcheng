package com.property.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.property.dao.RepairMapper;
import com.property.dto.PageQuery;
import com.property.dto.PageResult;
import com.property.dto.RepairDTO;
import com.property.entity.Repair;
import com.property.exception.BusinessException;
import com.property.service.RepairService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RepairServiceImpl extends ServiceImpl<RepairMapper, Repair> implements RepairService {

    @Autowired
    private RepairMapper repairMapper;

    @Override
    public PageResult<Repair> queryPage(PageQuery query) {
        int offset = (query.getPage() - 1) * query.getSize();
        List<Repair> list = repairMapper.selectRepairWithNames(query.getKeyword(), offset, query.getSize());
        Long total = repairMapper.countRepairWithNames(query.getKeyword());
        return new PageResult<>(total, list);
    }

    @Override
    public Repair getDetail(Integer id) {
        Repair repair = this.getById(id);
        if (repair == null) {
            throw new BusinessException("报修记录不存在");
        }
        return repair;
    }

    @Override
    public void assignEmployee(Integer repairId, Integer employeeId) {
        Repair repair = this.getById(repairId);
        if (repair == null) {
            throw new BusinessException("报修记录不存在");
        }
        repair.setEmployeeId(employeeId);
        repair.setStatus(1); // 分配员工后状态变为维修中
        this.updateById(repair);
    }

    @Override
    public void updateStatus(Integer repairId, Integer status) {
        Repair repair = this.getById(repairId);
        if (repair == null) {
            throw new BusinessException("报修记录不存在");
        }
        repair.setStatus(status);
        this.updateById(repair);
    }

    @Override
    public void submit(Integer ownerId, RepairDTO dto) {
        Repair repair = new Repair();
        BeanUtils.copyProperties(dto, repair);
        repair.setOwnerId(ownerId);
        repair.setStatus(0);
        this.save(repair);
    }

    @Override
    public PageResult<Repair> queryByOwner(Integer ownerId, PageQuery query) {
        Page<Repair> page = new Page<>(query.getPage(), query.getSize());
        LambdaQueryWrapper<Repair> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Repair::getOwnerId, ownerId);
        if (query.getKeyword() != null && !query.getKeyword().isEmpty()) {
            wrapper.like(Repair::getTitle, query.getKeyword());
        }
        wrapper.orderByDesc(Repair::getCreateTime);
        Page<Repair> result = this.page(page, wrapper);
        return new PageResult<>(result.getTotal(), result.getRecords());
    }

    @Override
    public Repair getOwnerDetail(Integer ownerId, Integer repairId) {
        Repair repair = this.getById(repairId);
        if (repair == null || !repair.getOwnerId().equals(ownerId)) {
            throw new BusinessException("报修记录不存在或无权查看");
        }
        return repair;
    }

    @Override
    public void rate(Integer ownerId, Integer repairId, Integer rating) {
        Repair repair = this.getById(repairId);
        if (repair == null || !repair.getOwnerId().equals(ownerId)) {
            throw new BusinessException("报修记录不存在或无权操作");
        }
        if (repair.getStatus() != 2) {
            throw new BusinessException("只有已完成的报修才能评价");
        }
        if (rating < 1 || rating > 5) {
            throw new BusinessException("评价必须在1-5星之间");
        }
        repair.setRating(rating);
        this.updateById(repair);
    }
}
