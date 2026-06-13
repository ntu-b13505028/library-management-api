package library_management_api;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class AdminLoginResponse {

    private boolean ok;
    private String message;
    private Integer adminId;
    private String username;

    public AdminLoginResponse(boolean ok, String message) {
        this.ok = ok;
        this.message = message;
    }

    public AdminLoginResponse(boolean ok, Admin admin) {
        this.ok = ok;
        this.adminId = admin.getAdminId();
        this.username = admin.getUsername();
    }

    public boolean isOk() {
        return ok;
    }

    public String getMessage() {
        return message;
    }

    public Integer getAdminId() {
        return adminId;
    }

    public String getUsername() {
        return username;
    }
}
