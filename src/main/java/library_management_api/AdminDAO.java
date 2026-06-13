package library_management_api;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

@Repository
public class AdminDAO {

    private final JdbcTemplate jdbcTemplate;

    public AdminDAO(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Optional<Admin> findAdminForLogin(String username, String password) {
        String sql = """
                SELECT admin_id, username
                FROM admins
                WHERE username = ?
                  AND password = ?
                """;

        try {
            Admin admin = jdbcTemplate.queryForObject(sql, (resultSet, rowNumber) -> new Admin(
                    resultSet.getInt("admin_id"),
                    resultSet.getString("username")
            ), username, password);

            return Optional.of(admin);
        } catch (EmptyResultDataAccessException exception) {
            return Optional.empty();
        }
    }

    public List<AdminBorrowRecordItem> findBorrowRecords(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return findAllBorrowRecords();
        }

        String sql = """
                SELECT br.record_id, u.student_no, u.name, br.book_id, b.title,
                       br.borrow_date, br.due_date, br.return_date,
                       br.borrow_days, br.record_status
                FROM borrow_records br
                JOIN users u ON br.user_id = u.user_id
                JOIN books b ON br.book_id = b.book_id
                WHERE u.student_no LIKE ?
                   OR u.name LIKE ?
                ORDER BY br.borrow_date DESC
                """;

        String searchKeyword = "%" + keyword.trim() + "%";
        return jdbcTemplate.query(sql, borrowRecordRowMapper(), searchKeyword, searchKeyword);
    }

    public List<AdminReservationItem> findReservations(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return findAllReservations();
        }

        String sql = """
                SELECT r.reservation_id, u.student_no, u.name, r.book_id, b.title,
                       r.reservation_time, r.status
                FROM reservations r
                JOIN users u ON r.user_id = u.user_id
                JOIN books b ON r.book_id = b.book_id
                WHERE u.student_no LIKE ?
                   OR u.name LIKE ?
                ORDER BY r.reservation_time DESC
                """;

        String searchKeyword = "%" + keyword.trim() + "%";
        return jdbcTemplate.query(sql, adminReservationRowMapper(), searchKeyword, searchKeyword);
    }

    private List<AdminReservationItem> findAllReservations() {
        String sql = """
                SELECT r.reservation_id, u.student_no, u.name, r.book_id, b.title,
                       r.reservation_time, r.status
                FROM reservations r
                JOIN users u ON r.user_id = u.user_id
                JOIN books b ON r.book_id = b.book_id
                ORDER BY r.reservation_time DESC
                """;

        return jdbcTemplate.query(sql, adminReservationRowMapper());
    }

    private RowMapper<AdminReservationItem> adminReservationRowMapper() {
        return (resultSet, rowNumber) -> new AdminReservationItem(
                resultSet.getInt("reservation_id"),
                resultSet.getString("student_no"),
                resultSet.getString("name"),
                resultSet.getInt("book_id"),
                resultSet.getString("title"),
                resultSet.getTimestamp("reservation_time").toLocalDateTime(),
                resultSet.getString("status")
        );
    }

    public List<TopBookItem> findTopBooks() {
        String sql = """
                SELECT br.book_id, b.title, COUNT(*) AS borrow_count
                FROM borrow_records br
                JOIN books b ON br.book_id = b.book_id
                GROUP BY br.book_id, b.title
                ORDER BY borrow_count DESC, br.book_id ASC
                """;

        return jdbcTemplate.query(sql, (resultSet, rowNumber) -> new TopBookItem(
                rowNumber + 1,
                resultSet.getInt("book_id"),
                resultSet.getString("title"),
                resultSet.getInt("borrow_count")
        ));
    }

    public List<TopSubjectItem> findTopSubjects() {
        String sql = """
                SELECT b.subjects, COUNT(*) AS borrow_count
                FROM borrow_records br
                JOIN books b ON br.book_id = b.book_id
                GROUP BY b.subjects
                ORDER BY borrow_count DESC, b.subjects ASC
                """;

        return jdbcTemplate.query(sql, (resultSet, rowNumber) -> new TopSubjectItem(
                rowNumber + 1,
                resultSet.getString("subjects"),
                resultSet.getInt("borrow_count")
        ));
    }

    public List<AdminUserItem> findUsers(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return findAllUsers();
        }

        String sql = """
                SELECT user_id, student_no, name, role_level, status, created_at
                FROM users
                WHERE student_no LIKE ?
                   OR name LIKE ?
                ORDER BY user_id ASC
                """;

        String searchKeyword = "%" + keyword.trim() + "%";
        return jdbcTemplate.query(sql, adminUserRowMapper(), searchKeyword, searchKeyword);
    }

    private List<AdminUserItem> findAllUsers() {
        String sql = """
                SELECT user_id, student_no, name, role_level, status, created_at
                FROM users
                ORDER BY user_id ASC
                """;

        return jdbcTemplate.query(sql, adminUserRowMapper());
    }

    private RowMapper<AdminUserItem> adminUserRowMapper() {
        return (resultSet, rowNumber) -> new AdminUserItem(
                resultSet.getInt("user_id"),
                resultSet.getString("student_no"),
                resultSet.getString("name"),
                resultSet.getString("role_level"),
                resultSet.getString("status"),
                resultSet.getTimestamp("created_at").toLocalDateTime()
        );
    }

    public List<AdminBookItem> findBooks(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return findAllBooks();
        }

        String sql = """
                SELECT book_id, title, authors, subjects, publisher,
                       publish_year, edition, isbn, status
                FROM books
                WHERE title LIKE ?
                   OR authors LIKE ?
                   OR subjects LIKE ?
                   OR publisher LIKE ?
                   OR isbn LIKE ?
                ORDER BY book_id ASC
                """;

        String searchKeyword = "%" + keyword.trim() + "%";
        return jdbcTemplate.query(sql, adminBookRowMapper(),
                searchKeyword, searchKeyword, searchKeyword, searchKeyword, searchKeyword);
    }

    private List<AdminBookItem> findAllBooks() {
        String sql = """
                SELECT book_id, title, authors, subjects, publisher,
                       publish_year, edition, isbn, status
                FROM books
                ORDER BY book_id ASC
                """;

        return jdbcTemplate.query(sql, adminBookRowMapper());
    }

    private RowMapper<AdminBookItem> adminBookRowMapper() {
        return (resultSet, rowNumber) -> new AdminBookItem(
                resultSet.getInt("book_id"),
                resultSet.getString("title"),
                resultSet.getString("authors"),
                resultSet.getString("subjects"),
                resultSet.getString("publisher"),
                resultSet.getObject("publish_year", Integer.class),
                resultSet.getString("edition"),
                resultSet.getString("isbn"),
                resultSet.getString("status")
        );
    }

    public int updateUserStatus(int userId, String status) {
        String sql = """
                UPDATE users
                SET status = ?
                WHERE user_id = ?
                """;

        return jdbcTemplate.update(sql, status, userId);
    }

    public void addBook(AddBookRequest request) {
        String sql = """
                INSERT INTO books
                    (title, authors, subjects, publisher, publish_year, edition,
                     format_desc, source, isbn, note, status)
                VALUES
                    (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, 'AVAILABLE')
                """;

        jdbcTemplate.update(sql,
                request.getTitle(),
                request.getAuthors(),
                request.getSubjects(),
                request.getPublisher(),
                request.getPublishYear(),
                request.getEdition(),
                request.getFormatDesc(),
                request.getSource(),
                request.getIsbn(),
                request.getNote());
    }

    public String findBookStatus(int bookId) {
        String sql = """
                SELECT status
                FROM books
                WHERE book_id = ?
                """;

        try {
            return jdbcTemplate.queryForObject(sql, String.class, bookId);
        } catch (EmptyResultDataAccessException exception) {
            return null;
        }
    }

    public void removeBook(int bookId) {
        String sql = """
                UPDATE books
                SET status = 'REMOVED'
                WHERE book_id = ?
                """;

        jdbcTemplate.update(sql, bookId);
    }

    public void restoreBook(int bookId) {
        String sql = """
                UPDATE books
                SET status = 'AVAILABLE'
                WHERE book_id = ?
                """;

        jdbcTemplate.update(sql, bookId);
    }

    private List<AdminBorrowRecordItem> findAllBorrowRecords() {
        String sql = """
                SELECT br.record_id, u.student_no, u.name, br.book_id, b.title,
                       br.borrow_date, br.due_date, br.return_date,
                       br.borrow_days, br.record_status
                FROM borrow_records br
                JOIN users u ON br.user_id = u.user_id
                JOIN books b ON br.book_id = b.book_id
                ORDER BY br.borrow_date DESC
                """;

        return jdbcTemplate.query(sql, borrowRecordRowMapper());
    }

    private RowMapper<AdminBorrowRecordItem> borrowRecordRowMapper() {
        return (resultSet, rowNumber) -> {
            LocalDateTime returnDate = null;

            if (resultSet.getTimestamp("return_date") != null) {
                returnDate = resultSet.getTimestamp("return_date").toLocalDateTime();
            }

            return new AdminBorrowRecordItem(
                    resultSet.getInt("record_id"),
                    resultSet.getString("student_no"),
                    resultSet.getString("name"),
                    resultSet.getInt("book_id"),
                    resultSet.getString("title"),
                    resultSet.getTimestamp("borrow_date").toLocalDateTime(),
                    resultSet.getTimestamp("due_date").toLocalDateTime(),
                    returnDate,
                    resultSet.getInt("borrow_days"),
                    resultSet.getString("record_status")
            );
        };
    }
}
