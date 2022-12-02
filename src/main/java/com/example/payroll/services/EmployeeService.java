package com.example.payroll.services;

import com.example.payroll.controller.EmployeeModelAssembler;
import com.example.payroll.entities.Employee;
import com.example.payroll.errorhandling.EmployeeNotFoundException;
import com.example.payroll.repositories.EmployeeRepository;
import org.springframework.hateoas.EntityModel;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EmployeeService {

    private final EmployeeRepository employeeRepository;
    private final EmployeeModelAssembler assembler;

    public EmployeeService(EmployeeRepository employeeRepository, EmployeeModelAssembler assembler) {
        this.employeeRepository = employeeRepository;
        this.assembler = assembler;
    }

    public List<EntityModel<Employee>> getAllEmployees() {
        return employeeRepository.findAll().stream()
                .map(assembler::toModel)
                .toList();
    }

    public EntityModel<Employee> getEmployee(long id) {
        return assembler.toModel(employeeRepository.findById(id)
                .orElseThrow(() -> new EmployeeNotFoundException(id)));
    }

    public EntityModel<Employee> addEmployee(Employee newEmployee) {
        return assembler.toModel(employeeRepository.save(newEmployee));
    }

    public EntityModel<Employee> updateEmployee(Employee changedEmployee, long id) {
        Employee updatedEmployee = employeeRepository.findById(id)
                .map(employee -> {
                    employee.setName(changedEmployee.getName());
                    employee.setRole(changedEmployee.getRole());
                    return employeeRepository.save(employee);
                })
                .orElseGet(() -> {
                    changedEmployee.setId(id);
                    return employeeRepository.save(changedEmployee);
                });

        return assembler.toModel(updatedEmployee);
    }

    public void deleteEmployee(long id) {
        employeeRepository.deleteById(id);
    }


}
