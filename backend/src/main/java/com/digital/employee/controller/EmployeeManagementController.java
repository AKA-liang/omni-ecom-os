package com.digital.employee.controller;

import com.digital.employee.entity.Employee;
import com.digital.employee.service.EmployeeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/employees")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class EmployeeManagementController {
    
    private final EmployeeService employeeService;
    
    @GetMapping
    public ResponseEntity<List<Employee>> getAllEmployees(
            @RequestParam(required = false) String category) {
        if (category != null && !category.isEmpty()) {
            return ResponseEntity.ok(employeeService.getEmployeesByCategory(category));
        }
        return ResponseEntity.ok(employeeService.getAllEmployees());
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<Employee> getEmployeeById(@PathVariable Long id) {
        Employee employee = employeeService.getEmployeeById(id);
        if (employee != null) {
            return ResponseEntity.ok(employee);
        }
        return ResponseEntity.notFound().build();
    }
    
    @PostMapping
    public ResponseEntity<Employee> createEmployee(@RequestBody Employee employee) {
        Employee created = employeeService.createEmployee(employee);
        return ResponseEntity.ok(created);
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<Employee> updateEmployee(
            @PathVariable Long id,
            @RequestBody Employee employee) {
        Employee existing = employeeService.getEmployeeById(id);
        if (existing == null) {
            return ResponseEntity.notFound().build();
        }
        
        if (employee.getName() != null) existing.setName(employee.getName());
        if (employee.getRole() != null) existing.setRole(employee.getRole());
        if (employee.getPosition() != null) existing.setPosition(employee.getPosition());
        if (employee.getAvatar() != null) existing.setAvatar(employee.getAvatar());
        if (employee.getStatus() != null) existing.setStatus(employee.getStatus());
        if (employee.getCurrentTask() != null) existing.setCurrentTask(employee.getCurrentTask());
        if (employee.getRecentOutput() != null) existing.setRecentOutput(employee.getRecentOutput());
        if (employee.getIsOnDuty() != null) existing.setIsOnDuty(employee.getIsOnDuty());
        if (employee.getSchedule() != null) existing.setSchedule(employee.getSchedule());
        if (employee.getCategory() != null) existing.setCategory(employee.getCategory());
        if (employee.getSkills() != null) existing.setSkills(employee.getSkills());
        if (employee.getTasksCompleted() != null) existing.setTasksCompleted(employee.getTasksCompleted());
        if (employee.getAccuracy() != null) existing.setAccuracy(employee.getAccuracy());
        if (employee.getAvgResponseTime() != null) existing.setAvgResponseTime(employee.getAvgResponseTime());
        if (employee.getPrompt() != null) existing.setPrompt(employee.getPrompt());
        
        return ResponseEntity.ok(employeeService.updateEmployee(existing));
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEmployee(@PathVariable Long id) {
        Employee employee = employeeService.getEmployeeById(id);
        if (employee == null) {
            return ResponseEntity.notFound().build();
        }
        employeeService.deleteEmployee(id);
        return ResponseEntity.ok().build();
    }
}
