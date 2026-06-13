package library_management_api;

public class User {

    private int userId;
    private String studentNo;
    private String name;
    private String roleLevel;
    private String status;

    public User(int userId, String studentNo, String name, String roleLevel, String status) {
        this.userId = userId;
        this.studentNo = studentNo;
        this.name = name;
        this.roleLevel = roleLevel;
        this.status = status;
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
}
