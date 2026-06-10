package com.property.controller;

import com.property.dto.EmployeeDTO;
import com.property.dto.PageQuery;
import com.property.dto.PageResult;
import com.property.dto.Result;
import com.property.entity.Employee;
import com.property.service.EmployeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/employees")
public class EmployeeController {

    @Autowired
    private EmployeeService employeeService;

    @GetMapping
    public Result<PageResult<Employee>> list(PageQuery query) {
        return Result.success(employeeService.queryPage(query));
    }

    /**
     * 获取全部员工（不分页，供下拉选择）
     */
    @GetMapping("/all")
    public Result<List<Employee>> all() {
        return Result.success(employeeService.list());
    }

    @GetMapping("/{id}")
    public Result<Employee> detail(@PathVariable Integer id) {
        return Result.success(employeeService.getDetail(id));
    }

    @PostMapping
    public Result<Void> add(@Validated @RequestBody EmployeeDTO dto) {
        employeeService.add(dto);
        return Result.success("新增成功", null);
    }

    @PutMapping("/{id}")
    public Result<Void> update(@PathVariable Integer id, @Validated @RequestBody EmployeeDTO dto) {
        employeeService.update(id, dto);
        return Result.success("修改成功", null);
    }

    @DeleteMapping("/{id}")
    public Result<Void> remove(@PathVariable Integer id) {
        employeeService.remove(id);
        return Result.success("删除成功", null);
    }
}
