package library_management_api;

import java.time.LocalDateTime;

public class AdminUserItem {

    private int userId;
    private String studentNo;
    private String name;
    private String roleLevel;
    private String status;
    private LocalDateTime createdAt;

    public AdminUserItem(int userId, String studentNo, String name,
            String roleLevel, String status, LocalDateTime createdAt) {
        this.userId = userId;
        this.studentNo = studentNo;
        this.name = name;
        this.roleLevel = roleLevel;
        this.status = status;
        this.createdAt = createdAt;
    }

    public int getUserId() {
        return userId;
    }

    public String getStudentNo() {
        return studentNo;
    }

    public String getName() {
        return name;
    }

    public String getRoleLevel() {
        return roleLevel;
    }

    public String getStatus() {
        return status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}
