package library_management_api;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class LoginResponse {

    private boolean ok;
    private String message;
    private Integer userId;
    private String studentNo;
    private String name;
    private String roleLevel;
    private String status;

    public LoginResponse(boolean ok, String message) {
        this.ok = ok;
        this.message = message;
    }

    public LoginResponse(boolean ok, User user) {
        this.ok = ok;
        this.userId = user.getUserId();
        this.studentNo = user.getStudentNo();
        this.name = user.getName();
        this.roleLevel = user.getRoleLevel();
        this.status = user.getStatus();
    }

    public boolean isOk() {
        return ok;
    }

    public String getMessage() {
        return message;
    }

    public Integer getUserId() {
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
}
