package group4.hrms.dao;

import group4.hrms.model.Department;
import group4.hrms.model.Position;
import group4.hrms.model.Role;
import group4.hrms.model.Setting;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * DAO class cho Setting - Tổng hợp dữ liệu từ Department, Position, Role
 */
public class SettingDao {
    
    private static final Logger logger = LoggerFactory.getLogger(SettingDao.class);
    
    private final DepartmentDao departmentDao = new DepartmentDao();
    private final PositionDao positionDao = new PositionDao();
    private final RoleDao roleDao = new RoleDao();
    
    /**
     * Tạo mới setting
     */
    public Setting create(Setting setting) {
        logger.info("Tạo mới setting: {} - {}", setting.getType(), setting.getName());
        
        switch (setting.getType()) {
            case "Department":
                Department dept = new Department(setting.getName());
                dept = departmentDao.create(dept);
                return convertDepartmentToSetting(dept);
                
            case "Position":
                Position pos = new Position(setting.getValue(), setting.getName(), setting.getPriority());
                pos = positionDao.create(pos);
                return convertPositionToSetting(pos);
                
            case "Role":
                Role role = new Role(setting.getValue(), setting.getName(), setting.getPriority());
                role = roleDao.create(role).orElse(null);
                if (role == null) {
                    throw new RuntimeException("Không thể tạo role");
                }
                return convertRoleToSetting(role);
                
            default:
                throw new RuntimeException("Type không hợp lệ: " + setting.getType());
        }
    }
    
    /**
     * Cập nhật setting
     */
    public Setting update(Setting setting) {
        logger.info("Cập nhật setting ID: {} - Type: {}", setting.getId(), setting.getType());
        
        switch (setting.getType()) {
            case "Department":
                Department dept = new Department();
                dept.setId(setting.getId());
                dept.setName(setting.getName());
                dept = departmentDao.update(dept);
                return dept != null ? convertDepartmentToSetting(dept) : null;
                
            case "Position":
                Position pos = new Position();
                pos.setId(setting.getId());
                pos.setCode(setting.getValue());
                pos.setName(setting.getName());
                pos.setJobLevel(setting.getPriority());
                pos = positionDao.update(pos);
                return pos != null ? convertPositionToSetting(pos) : null;
                
            case "Role":
                Role role = new Role();
                role.setId(setting.getId());
                role.setCode(setting.getValue());
                role.setName(setting.getName());
                role.setPriority(setting.getPriority());
                boolean updated = roleDao.update(role);
                if (updated) {
                    Optional<Role> updatedRole = roleDao.findById(setting.getId());
                    return updatedRole.map(this::convertRoleToSetting).orElse(null);
                }
                return null;
                
            default:
                throw new RuntimeException("Type không hợp lệ: " + setting.getType());
        }
    }
    
    /**
     * Xóa setting theo ID và Type
     */
    public boolean delete(Long settingId, String type) {
        logger.info("Xóa setting ID: {} - Type: {}", settingId, type);
        
        switch (type) {
            case "Department":
                return departmentDao.delete(settingId);
            case "Position":
                return positionDao.delete(settingId);
            case "Role":
                return roleDao.delete(settingId);
            default:
                throw new RuntimeException("Type không hợp lệ: " + type);
        }
    }
    
    /**
     * Tìm setting theo ID và Type
     */
    public Optional<Setting> findById(Long settingId, String type) {
        logger.debug("Tìm setting theo ID: {} - Type: {}", settingId, type);
        
        switch (type) {
            case "Department":
                return departmentDao.findById(settingId).map(this::convertDepartmentToSetting);
            case "Position":
                return positionDao.findById(settingId).map(this::convertPositionToSetting);
            case "Role":
                return roleDao.findById(settingId).map(this::convertRoleToSetting);
            default:
                return Optional.empty();
        }
    }
    
    /**
     * Lấy tất cả settings từ 3 bảng
     */
    public List<Setting> findAll() {
        logger.debug("Lấy tất cả settings từ Department, Position, Role");
        
        List<Setting> settings = new ArrayList<>();
        
        try {
            // Lấy từ Department
            List<Department> departments = departmentDao.findAll();
            for (Department dept : departments) {
                settings.add(convertDepartmentToSetting(dept));
            }
            
            // Lấy từ Position
            List<Position> positions = positionDao.findAll();
            for (Position pos : positions) {
                settings.add(convertPositionToSetting(pos));
            }
            
            // Lấy từ Role
            List<Role> roles = roleDao.findAll();
            for (Role role : roles) {
                settings.add(convertRoleToSetting(role));
            }
            
            logger.debug("Tổng cộng: {} settings", settings.size());
        } catch (Exception e) {
            logger.error("Lỗi khi lấy settings: ", e);
            throw e;
        }
        
        return settings;
    }
    
    /**
     * Lấy settings theo type
     */
    public List<Setting> findByType(String type) {
        logger.debug("Tìm settings theo type: {}", type);
        
        List<Setting> settings = new ArrayList<>();
        
        switch (type) {
            case "Department":
                List<Department> departments = departmentDao.findAll();
                for (Department dept : departments) {
                    settings.add(convertDepartmentToSetting(dept));
                }
                break;
                
            case "Position":
                List<Position> positions = positionDao.findAll();
                for (Position pos : positions) {
                    settings.add(convertPositionToSetting(pos));
                }
                break;
                
            case "Role":
                List<Role> roles = roleDao.findAll();
                for (Role role : roles) {
                    settings.add(convertRoleToSetting(role));
                }
                break;
        }
        
        logger.debug("Tìm thấy {} settings cho type: {}", settings.size(), type);
        return settings;
    }
    
    /**
     * Tìm kiếm settings
     */
    public List<Setting> search(String keyword) {
        logger.debug("Tìm kiếm settings với keyword: {}", keyword);
        
        List<Setting> allSettings = findAll();
        String lowerKeyword = keyword.toLowerCase();
        
        return allSettings.stream()
            .filter(s -> s.getName().toLowerCase().contains(lowerKeyword) || 
                        (s.getValue() != null && s.getValue().toLowerCase().contains(lowerKeyword)))
            .collect(Collectors.toList());
    }
    
    /**
     * Tìm kiếm settings theo type và keyword
     */
    public List<Setting> searchByType(String type, String keyword) {
        logger.debug("Tìm kiếm settings type {} với keyword: {}", type, keyword);
        
        List<Setting> typeSettings = findByType(type);
        String lowerKeyword = keyword.toLowerCase();
        
        return typeSettings.stream()
            .filter(s -> s.getName().toLowerCase().contains(lowerKeyword) || 
                        (s.getValue() != null && s.getValue().toLowerCase().contains(lowerKeyword)))
            .collect(Collectors.toList());
    }
    
    // Helper methods - Convert entities to Setting
    
    private Setting convertDepartmentToSetting(Department dept) {
        Setting setting = new Setting();
        setting.setId(dept.getId());
        setting.setName(dept.getName());
        setting.setType("Department");
        setting.setValue(null);
        setting.setPriority(null);
        setting.setCreatedAt(dept.getCreatedAt());
        setting.setUpdatedAt(dept.getUpdatedAt());
        return setting;
    }
    
    private Setting convertPositionToSetting(Position pos) {
        Setting setting = new Setting();
        setting.setId(pos.getId());
        setting.setName(pos.getName());
        setting.setType("Position");
        setting.setValue(pos.getCode());
        setting.setPriority(pos.getJobLevel());
        setting.setCreatedAt(pos.getCreatedAt());
        setting.setUpdatedAt(pos.getUpdatedAt());
        return setting;
    }
    
    private Setting convertRoleToSetting(Role role) {
        Setting setting = new Setting();
        setting.setId(role.getId());
        setting.setName(role.getName());
        setting.setType("Role");
        setting.setValue(role.getCode());
        setting.setPriority(role.getPriority());
        setting.setCreatedAt(role.getCreatedAt());
        setting.setUpdatedAt(role.getUpdatedAt());
        return setting;
    }
}
