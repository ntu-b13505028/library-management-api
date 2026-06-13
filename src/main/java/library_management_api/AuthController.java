package library_management_api;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AuthController {

    private final UserDAO userDAO;
    private final AdminDAO adminDAO;

    public AuthController(UserDAO userDAO, AdminDAO adminDAO) {
        this.userDAO = userDAO;
        this.adminDAO = adminDAO;
    }

    @PostMapping("/api/auth/login")
    public LoginResponse login(@RequestBody LoginRequest request) {
        return userDAO.findActiveUserForLogin(request.getStudentNo(), request.getPassword())
                .map(user -> new LoginResponse(true, user))
                .orElse(new LoginResponse(false, "Invalid student number or password"));
    }

    @PostMapping("/api/auth/admin-login")
    public AdminLoginResponse adminLogin(@RequestBody AdminLoginRequest request) {
        return adminDAO.findAdminForLogin(request.getUsername(), request.getPassword())
                .map(admin -> new AdminLoginResponse(true, admin))
                .orElse(new AdminLoginResponse(false, "Invalid admin username or password"));
    }

    @PostMapping("/api/auth/register")
    public SimpleMessageResponse register(@RequestBody RegisterRequest request) {
        if (isBlank(request.getStudentNo()) || isBlank(request.getName())
                || isBlank(request.getPassword()) || isBlank(request.getRoleLevel())) {
            return new SimpleMessageResponse(false, "All fields are required");
        }

        String roleLevel = request.getRoleLevel().trim().toUpperCase();

        if (!"NORMAL".equals(roleLevel) && !"VIP".equals(roleLevel)) {
            return new SimpleMessageResponse(false, "Role level must be NORMAL or VIP");
        }

        if (userDAO.studentNoExists(request.getStudentNo())) {
            return new SimpleMessageResponse(false, "Student number already exists");
        }

        request.setStudentNo(request.getStudentNo().trim());
        request.setName(request.getName().trim());
        request.setRoleLevel(roleLevel);

        userDAO.registerStudent(request);
        return new SimpleMessageResponse(true, "Register successful");
    }

    private boolean isBlank(String text) {
        return text == null || text.trim().isEmpty();
    }
}
