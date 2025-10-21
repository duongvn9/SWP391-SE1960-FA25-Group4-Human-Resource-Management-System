package group4.hrms.service;

import group4.hrms.model.Position;
import java.util.List;

/**
 * Service for managing positions
 */
public interface PositionService {
    /**
     * Get all active positions
     */
    List<Position> getAllPositions();
    
    /**
     * Get position by ID
     */
    Position getPositionById(long id);
    
    /**
     * Get position by code
     */
    Position getPositionByCode(String code);
}