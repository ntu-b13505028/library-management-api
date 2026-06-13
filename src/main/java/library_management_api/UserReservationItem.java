package library_management_api;

import java.time.LocalDateTime;

public class UserReservationItem {

    private int reservationId;
    private int bookId;
    private String title;
    private LocalDateTime reservationTime;
    private String status;

    public UserReservationItem(int reservationId, int bookId, String title,
            LocalDateTime reservationTime, String status) {
        this.reservationId = reservationId;
        this.bookId = bookId;
        this.title = title;
        this.reservationTime = reservationTime;
        this.status = status;
    }

    public int getReservationId() {
        return reservationId;
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
