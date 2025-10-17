package group4.hrms.service;

import group4.hrms.dao.TimesheetPeriodDao;
import group4.hrms.dto.AttendanceLogDto;
import group4.hrms.model.AttendanceLog;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class AttendanceMapper {

    public static List<AttendanceLog> convertDtoToEntity(
            List<AttendanceLogDto> dtos) throws SQLException {
        TimesheetPeriodDao periodDao = new TimesheetPeriodDao();
        List<AttendanceLog> entities = new ArrayList<>();

        for (AttendanceLogDto dto : dtos) {
            if (dto == null) {
                continue;
            }

            Optional<Long> periodIdOpt = Optional.empty();

            if (dto.getPeriod() != null) {
                periodIdOpt = periodDao.findIdByName(dto.getPeriod());
            }

            Long periodId = periodIdOpt.orElse(null);
            System.out.println(periodId);
            if (dto.getCheckIn() != null && dto.getDate() != null) {
                AttendanceLog checkInLog = new AttendanceLog();
                checkInLog.setUserId(dto.getUserId());
                checkInLog.setCheckType("IN");
                checkInLog.setCheckedAt(LocalDateTime.of(dto.getDate(), dto.getCheckIn()));
                checkInLog.setSource(dto.getSource());
                checkInLog.setNote(dto.getStatus());
                checkInLog.setPeriodId(periodId);
                entities.add(checkInLog);
            }

            if (dto.getCheckOut() != null && dto.getDate() != null) {
                AttendanceLog checkOutLog = new AttendanceLog();
                checkOutLog.setUserId(dto.getUserId());
                checkOutLog.setCheckType("OUT");
                checkOutLog.setCheckedAt(LocalDateTime.of(dto.getDate(), dto.getCheckOut()));
                checkOutLog.setSource(dto.getSource());
                checkOutLog.setNote(dto.getStatus());
                checkOutLog.setPeriodId(periodId);
                entities.add(checkOutLog);
            }
        }
        return entities;
    }
}
