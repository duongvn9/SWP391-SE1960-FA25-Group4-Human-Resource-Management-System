package group4.hrms.service.impl;

import group4.hrms.dao.DepartmentDao;
import group4.hrms.model.Department;
import group4.hrms.service.DepartmentService;

import java.util.List;

/**
 * Implementation of DepartmentService
 */
public class DepartmentServiceImpl implements DepartmentService {
    
    private final DepartmentDao departmentDao;
    
    public DepartmentServiceImpl(DepartmentDao departmentDao) {
        this.departmentDao = departmentDao;
    }
    
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