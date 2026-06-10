package com.property.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.property.dto.ComplaintDTO;
import com.property.dto.PageQuery;
import com.property.dto.PageResult;
import com.property.entity.Complaint;

public interface ComplaintService extends IService<Complaint> {

    /**
     * 管理员分页查询
     */
    PageResult<Complaint> queryPage(PageQuery query);

    /**
     * 管理员查看详情
     */
    Complaint getDetail(Integer id);

    /**
     * 管理员回复
     */
    void reply(Integer complaintId, String replyContent);

    /**
     * 业主提交投诉
     */
    void submit(Integer ownerId, ComplaintDTO dto);

    /**
     * 业主查看自己的投诉列表
     */
    PageResult<Complaint> queryByOwner(Integer ownerId, PageQuery query);

    /**
     * 业主查看自己的投诉详情
     */
    Complaint getOwnerDetail(Integer ownerId, Integer complaintId);
}
