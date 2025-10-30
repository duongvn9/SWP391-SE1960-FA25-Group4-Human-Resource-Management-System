package group4.hrms.controller.contract;

import group4.hrms.dao.EmploymentContractDao;
import group4.hrms.dao.UserDao;
import group4.hrms.dto.EmploymentContractDto;
import group4.hrms.model.EmploymentContract;
import group4.hrms.model.User;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Controller để hiển thị danh sách contracts
 */
@WebServlet("/contracts")
public class ContractListController extends HttpServlet {
    private EmploymentContractDao contractDao;
    private UserDao userDao;
    
    @Override
    public void init() throws ServletException {
        contractDao = new EmploymentContractDao();
        userDao = new UserDao();
    }
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        try {
            // Get all contracts
            List<EmploymentContract> contracts = contractDao.findAll();
            
            // Convert to DTOs with user info
            List<EmploymentContractDto> contractDtos = new ArrayList<>();
            for (EmploymentContract contract : contracts) {
                EmploymentContractDto dto = new EmploymentContractDto(contract);
                
                // Get user info
                Optional<User> userOpt = userDao.findById(contract.getUserId());
                if (userOpt.isPresent()) {
                    User user = userOpt.get();
                    dto.setUserFullName(user.getFullName());
                    dto.setUsername(user.getEmployeeCode());
                }
                
                contractDtos.add(dto);
            }
            
            request.setAttribute("contracts", contractDtos);
            request.getRequestDispatcher("/WEB-INF/views/contracts/contract-list.jsp")
                    .forward(request, response);
                    
        } catch (SQLException e) {
            throw new ServletException("Error loading contracts", e);
        }
    }
}
