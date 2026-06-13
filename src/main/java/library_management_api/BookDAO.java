package library_management_api;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class BookDAO {

    private final JdbcTemplate jdbcTemplate;

    public BookDAO(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<Book> findBooks(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            String sql = """
                    SELECT book_id, title, authors, subjects, status
                    FROM books
                    WHERE status <> 'REMOVED'
                    ORDER BY book_id
                    """;

            return jdbcTemplate.query(sql, (resultSet, rowNumber) -> new Book(
                    resultSet.getInt("book_id"),
                    resultSet.getString("title"),
                    resultSet.getString("authors"),
                    resultSet.getString("subjects"),
                    resultSet.getString("status")
            ));
        }

        String sql = """
                SELECT book_id, title, authors, subjects, status
                FROM books
                WHERE status <> 'REMOVED'
                  AND (
                      title LIKE ?
                      OR authors LIKE ?
                      OR subjects LIKE ?
                      OR publisher LIKE ?
                      OR isbn LIKE ?
                  )
                ORDER BY book_id
                """;

        String searchKeyword = "%" + keyword.trim() + "%";

        return jdbcTemplate.query(sql, (resultSet, rowNumber) -> new Book(
                resultSet.getInt("book_id"),
                resultSet.getString("title"),
                resultSet.getString("authors"),
                resultSet.getString("subjects"),
                resultSet.getString("status")
        ), searchKeyword, searchKeyword, searchKeyword, searchKeyword, searchKeyword);
    }

    public Optional<Book> findBookById(int id) {
        String sql = """
                SELECT book_id, title, authors, subjects, publisher, publish_year,
                       edition, format_desc, source, isbn, note, status
                FROM books
                WHERE book_id = ?
                """;

        try {
            Book book = jdbcTemplate.queryForObject(sql, (resultSet, rowNumber) -> new Book(
                    resultSet.getInt("book_id"),
                    resultSet.getString("title"),
                    resultSet.getString("authors"),
                    resultSet.getString("subjects"),
                    resultSet.getString("publisher"),
                    resultSet.getObject("publish_year", Integer.class),
                    resultSet.getString("edition"),
                    resultSet.getString("format_desc"),
                    resultSet.getString("source"),
                    resultSet.getString("isbn"),
                    resultSet.getString("note"),
                    resultSet.getString("status")
            ), id);

            return Optional.of(book);
        } catch (EmptyResultDataAccessException exception) {
            return Optional.empty();
        }
    }

    public List<BookBorrowHistoryItem> findBorrowHistoryByBookId(int bookId) {
        String sql = """
                SELECT br.record_id, u.student_no, u.name, br.borrow_date,
                       br.due_date, br.return_date, br.borrow_days, br.record_status
                FROM borrow_records br
                JOIN users u ON br.user_id = u.user_id
                WHERE br.book_id = ?
                ORDER BY br.borrow_date DESC
                """;

        return jdbcTemplate.query(sql, (resultSet, rowNumber) -> {
            LocalDateTime returnDate = null;

            if (resultSet.getTimestamp("return_date") != null) {
                returnDate = resultSet.getTimestamp("return_date").toLocalDateTime();
            }

            return new BookBorrowHistoryItem(
                    resultSet.getInt("record_id"),
                    resultSet.getString("student_no"),
                    resultSet.getString("name"),
                    resultSet.getTimestamp("borrow_date").toLocalDateTime(),
                    resultSet.getTimestamp("due_date").toLocalDateTime(),
                    returnDate,
                    resultSet.getInt("borrow_days"),
                    resultSet.getString("record_status")
            );
        }, bookId);
    }
}
