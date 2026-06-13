package library_management_api;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class BorrowController {

    private final BorrowDAO borrowDAO;

    public BorrowController(BorrowDAO borrowDAO) {
        this.borrowDAO = borrowDAO;
    }

    @PostMapping("/api/borrow")
    public BorrowResponse borrowBook(@RequestBody BorrowRequest request) {
        if (request.getUserId() == null || request.getBookId() == null) {
            return new BorrowResponse(false, "User ID and book ID are required");
        }

        return borrowDAO.borrowBook(request.getUserId(), request.getBookId());
    }

    @GetMapping("/api/users/{userId}/borrowed")
    public List<CurrentBorrowedBook> getCurrentBorrowedBooks(@PathVariable int userId) {
        return borrowDAO.findCurrentBorrowedBooks(userId);
    }

    @GetMapping("/api/users/{userId}/borrow-history")
    public List<BorrowHistoryItem> getBorrowHistory(@PathVariable int userId) {
        return borrowDAO.findBorrowHistory(userId);
    }

    @GetMapping("/api/users/{userId}/due-reminders")
    public List<DueReminderItem> getDueReminders(@PathVariable int userId) {
        return borrowDAO.findDueReminders(userId);
    }

    @PostMapping("/api/return")
    public ReturnResponse returnBook(@RequestBody ReturnRequest request) {
        if (request.getRecordId() == null || request.getBookId() == null) {
            return new ReturnResponse(false, "Record ID and book ID are required");
        }

        return borrowDAO.returnBook(request.getRecordId(), request.getBookId());
    }
}
