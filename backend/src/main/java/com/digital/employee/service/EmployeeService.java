package com.digital.employee.service;

import com.digital.employee.entity.Employee;
import com.digital.employee.repository.EmployeeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EmployeeService {
    
    private final EmployeeRepository employeeRepository;
    
    public List<Employee> getAllEmployees() {
        return employeeRepository.findAll();
    }
    
    public List<Employee> getEmployeesByCategory(String category) {
        return employeeRepository.findByCategory(category);
    }
    
    public Employee getEmployeeById(Long id) {
        return employeeRepository.findById(id).orElse(null);
    }
    
    @Transactional
    public Employee createEmployee(Employee employee) {
        return employeeRepository.save(employee);
    }
    
    @Transactional
    public Employee updateEmployee(Employee employee) {
        return employeeRepository.save(employee);
    }
    
    @Transactional
    public void deleteEmployee(Long id) {
        employeeRepository.deleteById(id);
    }
}
