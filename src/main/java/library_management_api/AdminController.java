package library_management_api;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AdminController {

    private final AdminDAO adminDAO;

    public AdminController(AdminDAO adminDAO) {
        this.adminDAO = adminDAO;
    }

    @GetMapping("/api/admin/borrow-records")
    public List<AdminBorrowRecordItem> getAllBorrowRecords(
            @RequestParam(required = false) String keyword) {
        return adminDAO.findBorrowRecords(keyword);
    }

    @GetMapping("/api/admin/reservations")
    public List<AdminReservationItem> getAllReservations(
            @RequestParam(required = false) String keyword) {
        return adminDAO.findReservations(keyword);
    }

    @GetMapping("/api/admin/statistics/top-books")
    public List<TopBookItem> getTopBooks() {
        return adminDAO.findTopBooks();
    }

    @GetMapping("/api/admin/statistics/top-subjects")
    public List<TopSubjectItem> getTopSubjects() {
        return adminDAO.findTopSubjects();
    }

    @GetMapping("/api/admin/users")
    public List<AdminUserItem> getAllUsers(@RequestParam(required = false) String keyword) {
        return adminDAO.findUsers(keyword);
    }

    @GetMapping("/api/admin/books")
    public List<AdminBookItem> getAllBooks(@RequestParam(required = false) String keyword) {
        return adminDAO.findBooks(keyword);
    }

    @PostMapping("/api/admin/books")
    public SimpleMessageResponse addBook(@RequestBody AddBookRequest request) {
        if (request.getTitle() == null || request.getTitle().trim().isEmpty()) {
            return new SimpleMessageResponse(false, "Title is required");
        }

        request.setTitle(request.getTitle().trim());
        request.setAuthors(trimText(request.getAuthors()));
        request.setSubjects(trimText(request.getSubjects()));
        request.setPublisher(trimText(request.getPublisher()));
        request.setEdition(trimText(request.getEdition()));
        request.setFormatDesc(trimText(request.getFormatDesc()));
        request.setSource(trimText(request.getSource()));
        request.setIsbn(trimText(request.getIsbn()));
        request.setNote(trimText(request.getNote()));

        adminDAO.addBook(request);
        return new SimpleMessageResponse(true, "Book added successfully");
    }

    @PatchMapping("/api/admin/books/{bookId}/remove")
    public SimpleMessageResponse removeBook(@PathVariable int bookId) {
        String status = adminDAO.findBookStatus(bookId);

        if (status == null) {
            return new SimpleMessageResponse(false, "Book not found");
        }

        if ("BORROWED".equals(status)) {
            return new SimpleMessageResponse(false, "Borrowed books cannot be removed");
        }

        adminDAO.removeBook(bookId);
        return new SimpleMessageResponse(true, "Book removed successfully");
    }

    @PatchMapping("/api/admin/books/{bookId}/restore")
    public SimpleMessageResponse restoreBook(@PathVariable int bookId) {
        String status = adminDAO.findBookStatus(bookId);

        if (status == null) {
            return new SimpleMessageResponse(false, "Book not found");
        }

        if (!"REMOVED".equals(status)) {
            return new SimpleMessageResponse(false, "Only removed books can be restored");
        }

        adminDAO.restoreBook(bookId);
        return new SimpleMessageResponse(true, "Book restored successfully");
    }

    @PatchMapping("/api/admin/users/{userId}/status")
    public SimpleMessageResponse updateUserStatus(
            @PathVariable int userId,
            @RequestBody UpdateUserStatusRequest request) {
        if (request.getStatus() == null || request.getStatus().trim().isEmpty()) {
            return new SimpleMessageResponse(false, "Invalid status");
        }

        String status = request.getStatus().trim().toUpperCase();

        if (!"ACTIVE".equals(status) && !"SUSPENDED".equals(status)) {
            return new SimpleMessageResponse(false, "Invalid status");
        }

        int updatedRows = adminDAO.updateUserStatus(userId, status);

        if (updatedRows == 0) {
            return new SimpleMessageResponse(false, "User not found");
        }

        return new SimpleMessageResponse(true, "User status updated successfully");
    }

    private String trimText(String text) {
        if (text == null) {
            return null;
        }

        return text.trim();
    }
}
