package com.property.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.property.dao.ComplaintMapper;
import com.property.dto.ComplaintDTO;
import com.property.dto.PageQuery;
import com.property.dto.PageResult;
import com.property.entity.Complaint;
import com.property.exception.BusinessException;
import com.property.service.ComplaintService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

@Service
public class ComplaintServiceImpl extends ServiceImpl<ComplaintMapper, Complaint> implements ComplaintService {

    @Override
    public PageResult<Complaint> queryPage(PageQuery query) {
        Page<Complaint> page = new Page<>(query.getPage(), query.getSize());
        LambdaQueryWrapper<Complaint> wrapper = new LambdaQueryWrapper<>();
        if (query.getKeyword() != null && !query.getKeyword().isEmpty()) {
            wrapper.like(Complaint::getTitle, query.getKeyword());
        }
        wrapper.orderByDesc(Complaint::getCreateTime);
        Page<Complaint> result = this.page(page, wrapper);
        return new PageResult<>(result.getTotal(), result.getRecords());
    }

    @Override
    public Complaint getDetail(Integer id) {
        Complaint complaint = this.getById(id);
        if (complaint == null) {
            throw new BusinessException("投诉记录不存在");
        }
        return complaint;
    }

    @Override
    public void reply(Integer complaintId, String replyContent) {
        Complaint complaint = this.getById(complaintId);
        if (complaint == null) {
            throw new BusinessException("投诉记录不存在");
        }
        complaint.setReply(replyContent);
        complaint.setStatus(1);
        this.updateById(complaint);
    }

    @Override
    public void submit(Integer ownerId, ComplaintDTO dto) {
        Complaint complaint = new Complaint();
        BeanUtils.copyProperties(dto, complaint);
        complaint.setOwnerId(ownerId);
        complaint.setStatus(0);
        this.save(complaint);
    }

    @Override
    public PageResult<Complaint> queryByOwner(Integer ownerId, PageQuery query) {
        Page<Complaint> page = new Page<>(query.getPage(), query.getSize());
        LambdaQueryWrapper<Complaint> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Complaint::getOwnerId, ownerId);
        if (query.getKeyword() != null && !query.getKeyword().isEmpty()) {
            wrapper.like(Complaint::getTitle, query.getKeyword());
        }
        wrapper.orderByDesc(Complaint::getCreateTime);
        Page<Complaint> result = this.page(page, wrapper);
        return new PageResult<>(result.getTotal(), result.getRecords());
    }

    @Override
    public Complaint getOwnerDetail(Integer ownerId, Integer complaintId) {
        Complaint complaint = this.getById(complaintId);
        if (complaint == null || !complaint.getOwnerId().equals(ownerId)) {
            throw new BusinessException("投诉记录不存在或无权查看");
        }
        return complaint;
    }
}
