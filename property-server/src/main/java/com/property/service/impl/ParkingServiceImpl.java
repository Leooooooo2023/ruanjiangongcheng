package com.property.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.property.dao.ParkingMapper;
import com.property.dto.PageQuery;
import com.property.dto.PageResult;
import com.property.entity.Parking;
import com.property.exception.BusinessException;
import com.property.service.ParkingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class ParkingServiceImpl extends ServiceImpl<ParkingMapper, Parking> implements ParkingService {

    @Autowired
    private ParkingMapper parkingMapper;

    @Override
    public PageResult<Parking> queryPage(PageQuery query) {
        int offset = (query.getPage() - 1) * query.getSize();
        List<Parking> list = parkingMapper.selectParkingWithOwner(query.getKeyword(), offset, query.getSize());
        Long total = parkingMapper.countParkingWithOwner(query.getKeyword());
        return new PageResult<>(total, list);
    }

    @Override
    public Parking getDetail(Integer id) {
        Parking parking = this.getById(id);
        if (parking == null) {
            throw new BusinessException("车位不存在");
        }
        return parking;
    }

    @Override
    public void add(Parking parking) {
        parking.setStatus(0);
        parking.setOwnerId(null);
        this.save(parking);
    }

    @Override
    public void update(Integer id, Parking parking) {
        Parking existing = this.getById(id);
        if (existing == null) {
            throw new BusinessException("车位不存在");
        }
        // 只更新车位号和位置，不覆盖 ownerId/startDate/endDate（这些由 assign/revoke 管理）
        existing.setNumber(parking.getNumber());
        existing.setLocation(parking.getLocation());
        this.updateById(existing);
    }

    @Override
    public void remove(Integer id) {
        Parking parking = this.getById(id);
        if (parking == null) {
            throw new BusinessException("车位不存在");
        }
        this.removeById(id);
    }

    @Override
    public void assign(Integer parkingId, Integer ownerId, LocalDate startDate, LocalDate endDate) {
        Parking parking = this.getById(parkingId);
        if (parking == null) {
            throw new BusinessException("车位不存在");
        }
        if (parking.getStatus() == 1) {
            throw new BusinessException("车位已被占用");
        }
        UpdateWrapper<Parking> wrapper = new UpdateWrapper<>();
        wrapper.eq("id", parkingId)
               .set("status", 1)
               .set("owner_id", ownerId)
               .set("start_date", startDate)
               .set("end_date", endDate);
        this.update(wrapper);
    }

    @Override
    public void revoke(Integer parkingId) {
        Parking parking = this.getById(parkingId);
        if (parking == null) {
            throw new BusinessException("车位不存在");
        }
        // 使用 UpdateWrapper 显式将字段设为 null（updateById 默认跳过 null 字段）
        UpdateWrapper<Parking> wrapper = new UpdateWrapper<>();
        wrapper.eq("id", parkingId)
               .set("status", 0)
               .set("owner_id", null)
               .set("start_date", null)
               .set("end_date", null);
        this.update(wrapper);
    }

    @Override
    public Parking getByOwnerId(Integer ownerId) {
        return this.getOne(new LambdaQueryWrapper<Parking>().eq(Parking::getOwnerId, ownerId));
    }
}
