package group4.hrms.service;

import group4.hrms.model.Department;
import java.util.List;

/**
 * Service for managing departments
 */
public interface DepartmentService {
    /**
     * Get all active departments
     */
    List<Department> getAllDepartments();
    
    /**
     * Get department by ID
     */
    Department getDepartmentById(long id);
}