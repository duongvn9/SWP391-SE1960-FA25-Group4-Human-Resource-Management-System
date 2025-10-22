package group4.hrms.service.impl;

import group4.hrms.dao.PositionDao;
import group4.hrms.model.Position;
import group4.hrms.service.PositionService;
import jakarta.inject.Inject;

import java.util.List;

/**
 * Implementation of PositionService
 */
public class PositionServiceImpl implements PositionService {
    
    @Inject
    private PositionDao positionDao;
    
    @Override
    public List<Position> getAllPositions() {
        return positionDao.findAll();
    }
    
    @Override
    public Position getPositionById(long id) {
        return positionDao.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Position not found: " + id));
    }
    
    @Override
    public Position getPositionByCode(String code) {
        return positionDao.findByCode(code)
            .orElseThrow(() -> new IllegalArgumentException("Position not found with code: " + code));
    }
}