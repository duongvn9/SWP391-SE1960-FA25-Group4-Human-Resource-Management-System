# System tree — cấu trúc thư mục dự án (cập nhật mới nhất)

Dưới đây là cây thư mục chính của workspace `NewProject` tại thời điểm cập nhật file này. File này dùng để tham khảo nhanh cấu trúc dự án và các thư mục view/asset cho webapp.

NewProject/
├─ .git/
├─ .github/
├─ docs/
└─ HRMS/
├─ src/
│ ├─ main/
│ │ ├─ java/
│ │ │ └─ group4/hrms/
│ │ │ ├─ config/
│ │ │ ├─ controller/
│ │ │ ├─ dto/
│ │ │ ├─ filter/
│ │ │ ├─ mapper/
│ │ │ ├─ model/
│ │ │ ├─ repository/
│ │ │ ├─ service/
│ │ │ └─ util/
│ │ ├─ resources/
│ │ │ └─ META-INF/
│ │ └─ webapp/
│ │ ├─ assets/
│ │ │ ├─ css/
│ │ │ ├─ js/
│ │ │ └─ img/
│ │ ├─ META-INF/
│ │ └─ WEB-INF/
│ │ └─ views/
│ │ ├─ layout/
│ │ ├─ auth/
│ │ ├─ home/
│ │ ├─ dashboard/
│ │ ├─ requests/
│ │ ├─ attendance/
│ │ └─ payroll/
│ └─ test/
└─ target/

## Ghi chú cấu trúc:

### **Architecture Pattern**

- **MVC Pattern**: Controller (Servlet) → Service → Repository → Model
- **Layout Components**: Tách biệt head.jsp, header.jsp, footer.jsp để tái sử dụng
- **CSS Architecture**: Page-specific CSS files với common.css cho shared styles
- **Responsive Design**: Bootstrap 5.3 + custom CSS với mobile-first approach

### **Implemented Features**

- ✅ **Authentication System**: Login/Logout với session management
- ✅ **Landing Page**: Hero section, features, testimonials với AOS animations
- ✅ **Dashboard**: Admin dashboard với charts, statistics, sidebar navigation
- ✅ **Contact Page**: Contact form với validation và success/error handling
- ✅ **Layout System**: Reusable components với parameter passing

### **Technology Stack**

- **Backend**: Java 17, Jakarta EE 10, Servlet API 6.0
- **Frontend**: JSP + JSTL, Bootstrap 5.3, FontAwesome 6.0, AOS animations
- **Database**: MySQL 8.x (configured, not yet implemented)
- **Build**: Maven 3.9, WAR packaging cho Tomcat 10.1+
- **Styling**: CSS Variables, External stylesheets, Dynamic loading

### **Servlet Mappings**

- `/` → HomeServlet (landing page)
- `/login` → LoginServlet (authentication)
- `/logout` → LogoutServlet (session cleanup)
- `/dashboard` → DashboardServlet (protected admin area)
- `/contact` → ContactServlet (contact form handling)

### **CSS Files Structure**

- `common.css`: Shared styles, variables, utilities
- `layout.css`: Navigation, header, footer styles
- `landing.css`: Home page specific styles
- `login.css`: Authentication pages styles
- `dashboard.css`: Admin dashboard styles
- `contact.css`: Contact page styles

### **Layout Components**

- `head.jsp`: Dynamic HTML head với page-specific CSS loading
- `header.jsp`: Public navigation với active page detection
- `footer.jsp`: Footer với scripts và animations
- `sidebar.jsp`: Dashboard sidebar với role-based menu
- `dashboard-header.jsp`: Admin header với user dropdown
- `dashboard-footer.jsp`: Dashboard footer với JavaScript

### **Development Notes**

- **CSRF Protection**: Structure ready for implementation
- **Session Management**: User authentication với role-based access
- **Form Validation**: Client-side + server-side validation
- **Error Handling**: Graceful error messages và user feedback
- **Mobile Responsive**: Tested trên các device sizes
- **Code Organization**: Proper separation of concerns

### **Next Steps Ready**

- Database integration với JPA/Hibernate
- Employee management features
- Attendance tracking system
- Payroll management
- Request/Leave management
- Report generation

Xem nhanh file này trong PowerShell:

```powershell
Get-Content d:\Ky5\SWP391\NewProject\docs\code-base-temp.md -Raw
```

**Tạo ngày**: 2025-10-06
**Cập nhật**: 2025-10-06 23:55
**Status**: Production-ready base structure ✅
