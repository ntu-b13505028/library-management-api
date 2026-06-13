package library_management_api;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class BookController {

    private final BookDAO bookDAO;

    public BookController(BookDAO bookDAO) {
        this.bookDAO = bookDAO;
    }

    @GetMapping("/api/books")
    public List<Book> getBooks(@RequestParam(required = false) String keyword) {
        return bookDAO.findBooks(keyword);
    }

    @GetMapping("/api/books/{id}")
    public ResponseEntity<Book> getBookById(@PathVariable int id) {
        return bookDAO.findBookById(id)
                .map(book -> ResponseEntity.ok(book))
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/api/books/{bookId}/borrow-history")
    public List<BookBorrowHistoryItem> getBookBorrowHistory(@PathVariable int bookId) {
        return bookDAO.findBorrowHistoryByBookId(bookId);
    }
}
