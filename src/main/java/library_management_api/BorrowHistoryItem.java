package library_management_api;

import java.time.LocalDateTime;

public class BorrowHistoryItem {

    private int recordId;
    private int bookId;
    private String title;
    private LocalDateTime borrowDate;
    private LocalDateTime dueDate;
    private LocalDateTime returnDate;
    private int borrowDays;
    private String recordStatus;

    public BorrowHistoryItem(int recordId, int bookId, String title, LocalDateTime borrowDate,
            LocalDateTime dueDate, LocalDateTime returnDate, int borrowDays, String recordStatus) {
        this.recordId = recordId;
        this.bookId = bookId;
        this.title = title;
        this.borrowDate = borrowDate;
        this.dueDate = dueDate;
        this.returnDate = returnDate;
        this.borrowDays = borrowDays;
        this.recordStatus = recordStatus;
    }

    public int getRecordId() {
        return recordId;
    }

    public int getBookId() {
        return bookId;
    }

    public String getTitle() {
        return title;
    }

    public LocalDateTime getBorrowDate() {
        return borrowDate;
    }

    public LocalDateTime getDueDate() {
        return dueDate;
    }

    public LocalDateTime getReturnDate() {
        return returnDate;
    }

    public int getBorrowDays() {
        return borrowDays;
    }

    public String getRecordStatus() {
        return recordStatus;
    }
}
