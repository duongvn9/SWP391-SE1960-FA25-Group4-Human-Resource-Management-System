package group4.hrms.service.impl;

import group4.hrms.dao.DepartmentDao;
import group4.hrms.model.Department;
import group4.hrms.service.DepartmentService;
import jakarta.inject.Inject;

import java.util.List;

/**
 * Implementation of DepartmentService
 */
public class DepartmentServiceImpl implements DepartmentService {
    
    @Inject
    private DepartmentDao departmentDao;
    
    @Override
    public List<Department> getAllDepartments() {
        return departmentDao.findAll();
    }
    
    @Override
    public Department getDepartmentById(long id) {
        return departmentDao.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Department not found: " + id));
    }
}