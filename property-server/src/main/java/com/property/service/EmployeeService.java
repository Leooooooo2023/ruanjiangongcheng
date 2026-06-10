package com.property.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.property.dto.EmployeeDTO;
import com.property.dto.PageQuery;
import com.property.dto.PageResult;
import com.property.entity.Employee;

public interface EmployeeService extends IService<Employee> {

    PageResult<Employee> queryPage(PageQuery query);

    Employee getDetail(Integer id);

    void add(EmployeeDTO dto);

    void update(Integer id, EmployeeDTO dto);

    void remove(Integer id);
}
