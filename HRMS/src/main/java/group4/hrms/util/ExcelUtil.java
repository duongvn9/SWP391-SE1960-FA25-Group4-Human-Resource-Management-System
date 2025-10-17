package group4.hrms.util;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import static org.apache.poi.ss.usermodel.CellType.BOOLEAN;
import static org.apache.poi.ss.usermodel.CellType.FORMULA;
import static org.apache.poi.ss.usermodel.CellType.NUMERIC;
import static org.apache.poi.ss.usermodel.CellType.STRING;
import org.apache.poi.ss.usermodel.CellValue;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.FormulaEvaluator;

public class ExcelUtil {

    public static String parseString(Cell cell) {
        if (cell == null) {
            return null;
        }

        try {
            return switch (cell.getCellType()) {
                case STRING ->
                    cell.getStringCellValue().trim();
                case NUMERIC -> {
                    double val = cell.getNumericCellValue();
                    // Nếu là số nguyên (vd: 8.0 → "8"), cắt .0 đi
                    if (val == Math.floor(val)) {
                        yield String.valueOf((long) val);
                    } else {
                        yield String.valueOf(val);
                    }
                }
                case BOOLEAN ->
                    String.valueOf(cell.getBooleanCellValue());
                case FORMULA -> {
                    // Nếu công thức trả về kiểu chuỗi
                    FormulaEvaluator evaluator = cell.getSheet().getWorkbook()
                            .getCreationHelper().createFormulaEvaluator();
                    CellValue evaluatedValue = evaluator.evaluate(cell);
                    if (evaluatedValue == null) {
                        yield null;
                    }
                    yield switch (evaluatedValue.getCellType()) {
                        case STRING ->
                            evaluatedValue.getStringValue().trim();
                        case NUMERIC -> {
                            double val = evaluatedValue.getNumberValue();
                            if (val == Math.floor(val)) {
                                yield String.valueOf((long) val);
                            } else {
                                yield String.valueOf(val);
                            }
                        }
                        case BOOLEAN ->
                            String.valueOf(evaluatedValue.getBooleanValue());
                        default ->
                            null;
                    };
                }
                default ->
                    null;
            };
        } catch (Exception e) {
            return null;
        }
    }

    public static Long parseLong(Cell cell) {
        if (cell == null) {
            return null;
        }
        try {
            if (cell.getCellType() == CellType.NUMERIC) {
                return (long) cell.getNumericCellValue();
            }
            String val = cell.getStringCellValue().trim();
            return val.isEmpty() ? null : Long.valueOf(val);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    public static LocalDate parseDate(Cell cell) {
        if (cell == null) {
            return null;
        }

        try {
            switch (cell.getCellType()) {
                case NUMERIC -> {
                    // Nếu là kiểu ngày thực sự hoặc số serial Excel
                    if (DateUtil.isCellDateFormatted(cell)) {
                        return cell.getDateCellValue().toInstant()
                                .atZone(ZoneId.systemDefault())
                                .toLocalDate();
                    } else {
                        // Nếu là số serial ngày (General format)
                        double numericValue = cell.getNumericCellValue();
                        return LocalDate.ofEpochDay((long) numericValue - 25569); // Excel epoch 1900
                    }
                }
                case STRING -> {
                    String val = cell.getStringCellValue().trim();
                    if (val.isEmpty()) {
                        return null;
                    }

                    // Thử nhiều pattern khác nhau
                    String[] patterns = {
                        "M/d/yyyy", "MM/dd/yyyy", "d/M/yyyy",
                        "yyyy-MM-dd", "dd-MM-yyyy", "dd/MM/yyyy",
                        "M/d/yy", "dd/MM/yy"
                    };

                    for (String p : patterns) {
                        try {
                            return LocalDate.parse(val,
                                    java.time.format.DateTimeFormatter.ofPattern(p)
                                            .withResolverStyle(java.time.format.ResolverStyle.LENIENT));
                        } catch (Exception ignore) {
                        }
                    }

                    // Cuối cùng thử ISO format mặc định (ví dụ "2025-10-11")
                    try {
                        return LocalDate.parse(val);
                    } catch (Exception ignore) {
                    }
                }
                case FORMULA -> {
                    FormulaEvaluator evaluator = cell.getSheet().getWorkbook()
                            .getCreationHelper().createFormulaEvaluator();
                    CellValue evaluated = evaluator.evaluate(cell);
                    if (evaluated == null) {
                        return null;
                    }
                    if (evaluated.getCellType() == CellType.NUMERIC) {
                        return evaluated.getNumberValue() > 25569
                                ? LocalDate.ofEpochDay((long) evaluated.getNumberValue() - 25569)
                                : null;
                    } else if (evaluated.getCellType() == CellType.STRING) {
                        cell.setCellValue(evaluated.getStringValue());
                        return parseDate(cell);
                    }
                }
                default -> {
                    return null;
                }
            }
        } catch (Exception e) {
        }
        return null;
    }

    public static LocalTime parseTime(Cell cell) {
        if (cell == null) {
            return null;
        }

        try {
            switch (cell.getCellType()) {
                case NUMERIC -> {
                    double numericValue = cell.getNumericCellValue();

                    // Nếu là kiểu time thật trong Excel (0 <= value < 1) hoặc có kèm ngày
                    if (DateUtil.isCellDateFormatted(cell) || numericValue > 0) {
                        // Nếu lớn hơn 1 (nghĩa là có cả phần ngày), lấy phần thập phân (phần thời gian)
                        double timePortion = numericValue % 1;
                        long totalSeconds = Math.round(timePortion * 24 * 60 * 60);
                        return LocalTime.ofSecondOfDay(totalSeconds);
                    }
                }

                case STRING -> {
                    String val = cell.getStringCellValue().trim();
                    if (val.isEmpty()) {
                        return null;
                    }

                    String[] patterns = {
                        "H:mm", "HH:mm", "H:mm:ss", "HH:mm:ss",
                        "h:mm a", "hh:mm a" // 12h format
                    };

                    for (String p : patterns) {
                        try {
                            return LocalTime.parse(val,
                                    java.time.format.DateTimeFormatter.ofPattern(p)
                                            .withResolverStyle(java.time.format.ResolverStyle.LENIENT));
                        } catch (Exception ignore) {
                        }
                    }
                }

                case FORMULA -> {
                    FormulaEvaluator evaluator = cell.getSheet().getWorkbook()
                            .getCreationHelper().createFormulaEvaluator();
                    CellValue evaluated = evaluator.evaluate(cell);
                    if (evaluated == null) {
                        return null;
                    }

                    if (evaluated.getCellType() == CellType.NUMERIC) {
                        double numericValue = evaluated.getNumberValue();
                        double timePortion = numericValue % 1;
                        long totalSeconds = Math.round(timePortion * 24 * 60 * 60);
                        return LocalTime.ofSecondOfDay(totalSeconds);
                    } else if (evaluated.getCellType() == CellType.STRING) {
                        return parseTime(cell);
                    }
                }
                default ->
                    throw new IllegalArgumentException("Unexpected value: " + cell.getCellType());
            }
        } catch (IllegalArgumentException ignored) {
        }
        return null;
    }
}
