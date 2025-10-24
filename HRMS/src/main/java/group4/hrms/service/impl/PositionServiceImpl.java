package group4.hrms.service.impl;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import group4.hrms.dao.PositionDao;
import group4.hrms.model.Position;
import group4.hrms.service.PositionService;

/**
 * Implementation of PositionService
 */
public class PositionServiceImpl implements PositionService {
    
    private static final Logger logger = LoggerFactory.getLogger(PositionServiceImpl.class);
    private final PositionDao positionDao;
    
    public PositionServiceImpl(PositionDao positionDao) {
        if (positionDao == null) {
            throw new IllegalArgumentException("PositionDao cannot be null");
        }
        this.positionDao = positionDao;
        logger.info("PositionServiceImpl initialized with PositionDao");
    }
    
    @Override
    public List<Position> getAllPositions() {
        try {
            List<Position> positions = positionDao.findAll();
            logger.info("Retrieved {} positions", positions.size());
            return positions;
        } catch (Exception e) {
            logger.error("Error retrieving all positions", e);
            throw new RuntimeException("Failed to retrieve positions", e);
        }
    }
    
    @Override
    public Position getPositionById(long id) {
        try {
            logger.debug("Finding position by id: {}", id);
            return positionDao.findById(id)
                .orElseThrow(() -> {
                    logger.warn("Position not found with id: {}", id);
                    return new IllegalArgumentException("Position not found: " + id);
                });
        } catch (Exception e) {
            logger.error("Error finding position by id: {}", id, e);
            throw new RuntimeException("Failed to retrieve position by id: " + id, e);
        }
    }
    
    @Override
    public Position getPositionByCode(String code) {
        try {
            logger.debug("Finding position by code: {}", code);
            return positionDao.findByCode(code)
                .orElseThrow(() -> {
                    logger.warn("Position not found with code: {}", code);
                    return new IllegalArgumentException("Position not found with code: " + code);
                });
        } catch (Exception e) {
            logger.error("Error finding position by code: {}", code, e);
            throw new RuntimeException("Failed to retrieve position by code: " + code, e);
        }
    }
}