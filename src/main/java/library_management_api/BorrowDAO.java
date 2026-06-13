package library_management_api;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public class BorrowDAO {

    private final JdbcTemplate jdbcTemplate;

    public BorrowDAO(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Transactional
    public BorrowResponse borrowBook(int userId, int bookId) {
        String roleLevel = findRoleLevelForActiveUser(userId);

        if (roleLevel == null) {
            return new BorrowResponse(false, "User not found");
        }

        int borrowLimit = getBorrowLimit(roleLevel);
        int borrowDays = getBorrowDays(roleLevel);

        if (borrowLimit == 0 || borrowDays == 0) {
            return new BorrowResponse(false, "Unsupported user role");
        }

        int currentBorrowedCount = countCurrentBorrowedBooks(userId);

        if (currentBorrowedCount >= borrowLimit) {
            return new BorrowResponse(false,
                    "Borrow limit reached. " + roleLevel + " users can borrow up to "
                            + borrowLimit + " books.");
        }

        int updatedRows = markBookAsBorrowed(bookId);

        if (updatedRows == 0) {
            return new BorrowResponse(false, "Book is not available");
        }

        insertBorrowRecord(userId, bookId, borrowDays);
        return new BorrowResponse(true, "Borrow successful", borrowDays);
    }

    @Transactional
    public ReturnResponse returnBook(int recordId, int bookId) {
        int updatedRows = markBorrowRecordAsReturned(recordId, bookId);

        if (updatedRows == 0) {
            return new ReturnResponse(false, "Return failed");
        }

        Integer reservationId = findOldestWaitingReservation(bookId);
        String bookStatus;

        if (reservationId == null) {
            bookStatus = "AVAILABLE";
        } else {
            fulfillReservation(reservationId);
            bookStatus = "RESERVED";
        }

        updateBookStatus(bookId, bookStatus);
        return new ReturnResponse(true, "Return successful", bookStatus);
    }

    public List<CurrentBorrowedBook> findCurrentBorrowedBooks(int userId) {
        String sql = """
                SELECT br.record_id, br.book_id, b.title, br.borrow_date, br.due_date,
                       br.borrow_days, br.record_status
                FROM borrow_records br
                JOIN books b ON br.book_id = b.book_id
                WHERE br.user_id = ?
                  AND br.return_date IS NULL
                ORDER BY br.borrow_date DESC
                """;

        return jdbcTemplate.query(sql, (resultSet, rowNumber) -> new CurrentBorrowedBook(
                resultSet.getInt("record_id"),
                resultSet.getInt("book_id"),
                resultSet.getString("title"),
                resultSet.getTimestamp("borrow_date").toLocalDateTime(),
                resultSet.getTimestamp("due_date").toLocalDateTime(),
                resultSet.getInt("borrow_days"),
                resultSet.getString("record_status")
        ), userId);
    }

    public List<BorrowHistoryItem> findBorrowHistory(int userId) {
        String sql = """
                SELECT br.record_id, br.book_id, b.title, br.borrow_date, br.due_date,
                       br.return_date, br.borrow_days, br.record_status
                FROM borrow_records br
                JOIN books b ON br.book_id = b.book_id
                WHERE br.user_id = ?
                ORDER BY br.borrow_date DESC
                """;

        return jdbcTemplate.query(sql, (resultSet, rowNumber) -> {
            LocalDateTime returnDate = null;

            if (resultSet.getTimestamp("return_date") != null) {
                returnDate = resultSet.getTimestamp("return_date").toLocalDateTime();
            }

            return new BorrowHistoryItem(
                    resultSet.getInt("record_id"),
                    resultSet.getInt("book_id"),
                    resultSet.getString("title"),
                    resultSet.getTimestamp("borrow_date").toLocalDateTime(),
                    resultSet.getTimestamp("due_date").toLocalDateTime(),
                    returnDate,
                    resultSet.getInt("borrow_days"),
                    resultSet.getString("record_status")
            );
        }, userId);
    }

    public List<DueReminderItem> findDueReminders(int userId) {
        String sql = """
                SELECT br.record_id, br.book_id, b.title, br.borrow_date, br.due_date,
                       br.borrow_days, br.record_status
                FROM borrow_records br
                JOIN books b ON br.book_id = b.book_id
                WHERE br.user_id = ?
                  AND br.return_date IS NULL
                  AND br.due_date <= DATE_ADD(CURRENT_TIMESTAMP, INTERVAL 3 DAY)
                ORDER BY br.due_date ASC
                """;

        return jdbcTemplate.query(sql, (resultSet, rowNumber) -> {
            LocalDateTime dueDate = resultSet.getTimestamp("due_date").toLocalDateTime();
            String reminder = createReminderText(dueDate);

            return new DueReminderItem(
                    resultSet.getInt("record_id"),
                    resultSet.getInt("book_id"),
                    resultSet.getString("title"),
                    resultSet.getTimestamp("borrow_date").toLocalDateTime(),
                    dueDate,
                    resultSet.getInt("borrow_days"),
                    resultSet.getString("record_status"),
                    reminder
            );
        }, userId);
    }

    private String createReminderText(LocalDateTime dueDate) {
        LocalDateTime now = LocalDateTime.now();

        if (dueDate.isBefore(now)) {
            return "Overdue";
        }

        long daysUntilDue = ChronoUnit.DAYS.between(now.toLocalDate(), dueDate.toLocalDate());

        if (daysUntilDue == 0) {
            return "Due today";
        }

        if (daysUntilDue == 1) {
            return "Due in 1 day";
        }

        return "Due in " + daysUntilDue + " days";
    }

    private String findRoleLevelForActiveUser(int userId) {
        String sql = """
                SELECT role_level
                FROM users
                WHERE user_id = ?
                  AND status = 'ACTIVE'
                FOR UPDATE
                """;

        try {
            return jdbcTemplate.queryForObject(sql, String.class, userId);
        } catch (EmptyResultDataAccessException exception) {
            return null;
        }
    }

    private int countCurrentBorrowedBooks(int userId) {
        String sql = """
                SELECT COUNT(*)
                FROM borrow_records
                WHERE user_id = ?
                  AND return_date IS NULL
                """;

        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, userId);
        return count == null ? 0 : count;
    }

    private int markBookAsBorrowed(int bookId) {
        String sql = """
                UPDATE books
                SET status = 'BORROWED'
                WHERE book_id = ?
                  AND status = 'AVAILABLE'
                """;

        return jdbcTemplate.update(sql, bookId);
    }

    private void insertBorrowRecord(int userId, int bookId, int borrowDays) {
        String sql = """
                INSERT INTO borrow_records
                    (user_id, book_id, borrow_date, due_date, return_date,
                     borrow_days, created_at, record_status)
                VALUES
                    (?, ?, CURRENT_TIMESTAMP, DATE_ADD(CURRENT_TIMESTAMP, INTERVAL ? DAY),
                     NULL, ?, CURRENT_TIMESTAMP, 'BORROWING')
                """;

        jdbcTemplate.update(sql, userId, bookId, borrowDays, borrowDays);
    }

    private int markBorrowRecordAsReturned(int recordId, int bookId) {
        String sql = """
                UPDATE borrow_records
                SET return_date = CURRENT_TIMESTAMP,
                    record_status = 'RETURNED'
                WHERE record_id = ?
                  AND book_id = ?
                  AND return_date IS NULL
                """;

        return jdbcTemplate.update(sql, recordId, bookId);
    }

    private Integer findOldestWaitingReservation(int bookId) {
        String sql = """
                SELECT reservation_id
                FROM reservations
                WHERE book_id = ?
                  AND status = 'WAITING'
                ORDER BY reservation_time, reservation_id
                LIMIT 1
                FOR UPDATE
                """;

        try {
            return jdbcTemplate.queryForObject(sql, Integer.class, bookId);
        } catch (EmptyResultDataAccessException exception) {
            return null;
        }
    }

    private void fulfillReservation(int reservationId) {
        String sql = """
                UPDATE reservations
                SET status = 'FULFILLED'
                WHERE reservation_id = ?
                """;

        jdbcTemplate.update(sql, reservationId);
    }

    private void updateBookStatus(int bookId, String status) {
        String sql = """
                UPDATE books
                SET status = ?
                WHERE book_id = ?
                """;

        jdbcTemplate.update(sql, status, bookId);
    }

    private int getBorrowLimit(String roleLevel) {
        if ("NORMAL".equals(roleLevel)) {
            return 3;
        }

        if ("VIP".equals(roleLevel)) {
            return 5;
        }

        return 0;
    }

    private int getBorrowDays(String roleLevel) {
        if ("NORMAL".equals(roleLevel)) {
            return 7;
        }

        if ("VIP".equals(roleLevel)) {
            return 14;
        }

        return 0;
    }
}
