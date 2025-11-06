package group4.hrms.service;

import group4.hrms.dao.TimesheetPeriodDao;
import group4.hrms.dto.AttendanceLogDto;
import group4.hrms.model.AttendanceLog;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
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

    public static List<AttendanceLog> convertDtoToEntity(AttendanceLogDto dto) throws SQLException {
        List<AttendanceLog> entities = new ArrayList<>();
        if (dto == null) {
            return entities;
        }

        TimesheetPeriodDao periodDao = new TimesheetPeriodDao();
        Optional<Long> periodIdOpt = Optional.empty();

        if (dto.getPeriod() != null) {
            periodIdOpt = periodDao.findIdByName(dto.getPeriod());
        }

        Long periodId = periodIdOpt.orElse(null);

        if (dto.getCheckIn() != null && dto.getDate() != null) {
            AttendanceLog checkInLog = new AttendanceLog();
            checkInLog.setUserId(dto.getUserId());
            checkInLog.setCheckType("IN");
            checkInLog.setCheckedAt(LocalDateTime.of(dto.getDate(), dto.getCheckIn()));
            checkInLog.setNote(dto.getStatus());
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
            checkOutLog.setNote(dto.getStatus());
            checkOutLog.setSource(dto.getSource());
            checkOutLog.setNote(dto.getStatus());
            checkOutLog.setPeriodId(periodId);
            entities.add(checkOutLog);
        }

        return entities;
    }

    public static List<AttendanceLog> convertDtoToEntity(AttendanceLogDto dto, LocalTime oldCheckIn, LocalTime oldCheckOut) throws SQLException {
        List<AttendanceLog> logs = new ArrayList<>();
        TimesheetPeriodDao dao = new TimesheetPeriodDao();
        LocalDate date = dto.getDate();
        
        // Chỉ tạo log IN nếu có cả checkIn mới và oldCheckIn
        if (dto.getCheckIn() != null && oldCheckIn != null) {
            AttendanceLog logIn = new AttendanceLog();
            logIn.setUserId(dto.getUserId());
            logIn.setCheckType("IN");
            logIn.setCheckedAt(LocalDateTime.of(date, oldCheckIn)); 
            logIn.setSource(dto.getSource());
            logIn.setNote(dto.getStatus());
            logIn.setPeriodId(dao.findIdByName(dto.getPeriod()).orElse(null));
            logs.add(logIn);

            // Nếu muốn lưu giá trị mới (checkIn) cho update, sẽ được DAO dùng trong UPDATE
            logIn.setCheckedAtNew(LocalDateTime.of(date, dto.getCheckIn())); 
        }
        // Nếu chỉ có checkIn mới mà không có oldCheckIn (tức là thêm mới check-in)
        else if (dto.getCheckIn() != null && oldCheckIn == null) {
            AttendanceLog logIn = new AttendanceLog();
            logIn.setUserId(dto.getUserId());
            logIn.setCheckType("IN");
            logIn.setCheckedAt(LocalDateTime.of(date, dto.getCheckIn())); 
            logIn.setSource(dto.getSource());
            logIn.setNote(dto.getStatus());
            logIn.setPeriodId(dao.findIdByName(dto.getPeriod()).orElse(null));
            logs.add(logIn);
        }

        // Chỉ tạo log OUT nếu có cả checkOut mới và oldCheckOut
        if (dto.getCheckOut() != null && oldCheckOut != null) {
            AttendanceLog logOut = new AttendanceLog();
            logOut.setUserId(dto.getUserId());
            logOut.setCheckType("OUT");
            logOut.setCheckedAt(LocalDateTime.of(date, oldCheckOut)); 
            logOut.setSource(dto.getSource());
            logOut.setNote(dto.getStatus());
            logOut.setPeriodId(dao.findIdByName(dto.getPeriod()).orElse(null));
            logs.add(logOut);

            logOut.setCheckedAtNew(LocalDateTime.of(date, dto.getCheckOut())); 
        }
        // Nếu chỉ có checkOut mới mà không có oldCheckOut (tức là thêm mới check-out)
        else if (dto.getCheckOut() != null && oldCheckOut == null) {
            AttendanceLog logOut = new AttendanceLog();
            logOut.setUserId(dto.getUserId());
            logOut.setCheckType("OUT");
            logOut.setCheckedAt(LocalDateTime.of(date, dto.getCheckOut())); 
            logOut.setSource(dto.getSource());
            logOut.setNote(dto.getStatus());
            logOut.setPeriodId(dao.findIdByName(dto.getPeriod()).orElse(null));
            logs.add(logOut);
        }

        return logs;
    }
}
