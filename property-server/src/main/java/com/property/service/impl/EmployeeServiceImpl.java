package com.property.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.property.dao.EmployeeMapper;
import com.property.dto.EmployeeDTO;
import com.property.dto.PageQuery;
import com.property.dto.PageResult;
import com.property.entity.Employee;
import com.property.exception.BusinessException;
import com.property.service.EmployeeService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

@Service
public class EmployeeServiceImpl extends ServiceImpl<EmployeeMapper, Employee> implements EmployeeService {

    @Override
    public PageResult<Employee> queryPage(PageQuery query) {
        Page<Employee> page = new Page<>(query.getPage(), query.getSize());
        LambdaQueryWrapper<Employee> wrapper = new LambdaQueryWrapper<>();
        if (query.getKeyword() != null && !query.getKeyword().isEmpty()) {
            wrapper.like(Employee::getName, query.getKeyword())
                    .or().like(Employee::getDepartment, query.getKeyword());
        }
        wrapper.orderByDesc(Employee::getCreateTime);
        Page<Employee> result = this.page(page, wrapper);
        return new PageResult<>(result.getTotal(), result.getRecords());
    }

    @Override
    public Employee getDetail(Integer id) {
        Employee employee = this.getById(id);
        if (employee == null) {
            throw new BusinessException("员工不存在");
        }
        return employee;
    }

    @Override
    public void add(EmployeeDTO dto) {
        // 检查手机号唯一
        if (dto.getPhone() != null && !dto.getPhone().isEmpty()) {
            Long count = this.count(new LambdaQueryWrapper<Employee>().eq(Employee::getPhone, dto.getPhone()));
            if (count > 0) {
                throw new BusinessException("手机号已存在");
            }
        }
        Employee employee = new Employee();
        BeanUtils.copyProperties(dto, employee);
        this.save(employee);
    }

    @Override
    public void update(Integer id, EmployeeDTO dto) {
        Employee employee = this.getById(id);
        if (employee == null) {
            throw new BusinessException("员工不存在");
        }
        // 检查手机号唯一（排除自己）
        if (dto.getPhone() != null && !dto.getPhone().isEmpty()) {
            Long count = this.count(new LambdaQueryWrapper<Employee>()
                    .eq(Employee::getPhone, dto.getPhone())
                    .ne(Employee::getId, id));
            if (count > 0) {
                throw new BusinessException("手机号已存在");
            }
        }
        BeanUtils.copyProperties(dto, employee);
        employee.setId(id);
        this.updateById(employee);
    }

    @Override
    public void remove(Integer id) {
        Employee employee = this.getById(id);
        if (employee == null) {
            throw new BusinessException("员工不存在");
        }
        this.removeById(id);
    }
}
