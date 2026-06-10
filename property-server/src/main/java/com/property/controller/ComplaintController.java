package com.property.controller;

import com.property.dto.ComplaintDTO;
import com.property.dto.PageQuery;
import com.property.dto.PageResult;
import com.property.dto.Result;
import com.property.entity.Complaint;
import com.property.service.ComplaintService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class ComplaintController {

    @Autowired
    private ComplaintService complaintService;

    // ===== 管理员接口 =====

    @GetMapping("/complaints")
    public Result<PageResult<Complaint>> list(PageQuery query) {
        return Result.success(complaintService.queryPage(query));
    }

    @GetMapping("/complaints/{id}")
    public Result<Complaint> detail(@PathVariable Integer id) {
        return Result.success(complaintService.getDetail(id));
    }

    @PutMapping("/complaints/{id}/reply")
    public Result<Void> reply(@PathVariable Integer id, @RequestBody Map<String, String> params) {
        complaintService.reply(id, params.get("reply"));
        return Result.success("回复成功", null);
    }

    // ===== 业主接口 =====

    @GetMapping("/owner/complaints")
    public Result<PageResult<Complaint>> ownerList(PageQuery query, HttpServletRequest request) {
        Integer ownerId = (Integer) request.getAttribute("userId");
        return Result.success(complaintService.queryByOwner(ownerId, query));
    }

    @PostMapping("/owner/complaints")
    public Result<Void> submit(@Validated @RequestBody ComplaintDTO dto, HttpServletRequest request) {
        Integer ownerId = (Integer) request.getAttribute("userId");
        complaintService.submit(ownerId, dto);
        return Result.success("提交成功", null);
    }
}
