package library_management_api;

import java.time.LocalDateTime;

public class AdminReservationItem {

    private int reservationId;
    private String studentNo;
    private String userName;
    private int bookId;
    private String title;
    private LocalDateTime reservationTime;
    private String status;

    public AdminReservationItem(int reservationId, String studentNo, String userName,
            int bookId, String title, LocalDateTime reservationTime, String status) {
        this.reservationId = reservationId;
        this.studentNo = studentNo;
        this.userName = userName;
        this.bookId = bookId;
        this.title = title;
        this.reservationTime = reservationTime;
        this.status = status;
    }

    public int getReservationId() {
        return reservationId;
    }

    public String getStudentNo() {
        return studentNo;
    }

    public String getUserName() {
        return userName;
    }

    public int getBookId() {
        return bookId;
    }

    public String getTitle() {
        return title;
    }

    public LocalDateTime getReservationTime() {
        return reservationTime;
    }

    public String getStatus() {
        return status;
    }
}
