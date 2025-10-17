package group4.hrms.util;

import jakarta.servlet.http.HttpServletRequest;

public class PaginationUtil {

    private PaginationUtil() {
    }

    public static int getCurrentPage(HttpServletRequest req) {
        String pageParam = req.getParameter("page");
        try {
            return pageParam != null && !pageParam.isEmpty() ? Integer.parseInt(pageParam) : 1;
        } catch (NumberFormatException e) {
            return 1;
        }
    }

    public static int calculateTotalPages(int totalRecords, int recordsPerPage) {
        return (int) Math.ceil((double) totalRecords / recordsPerPage);
    }
}
