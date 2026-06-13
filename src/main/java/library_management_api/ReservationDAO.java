package library_management_api;

import java.util.List;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public class ReservationDAO {

    private final JdbcTemplate jdbcTemplate;

    public ReservationDAO(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Transactional
    public ReservationResponse reserveBook(int userId, int bookId) {
        String bookStatus = findBookStatus(bookId);

        if (bookStatus == null) {
            return new ReservationResponse(false, "Book not found");
        }

        if ("AVAILABLE".equals(bookStatus)) {
            return new ReservationResponse(false, "Available books do not need reservation");
        }

        if ("REMOVED".equals(bookStatus)) {
            return new ReservationResponse(false, "Removed books cannot be reserved");
        }

        if ("RESERVED".equals(bookStatus)) {
            return new ReservationResponse(false, "This book is already reserved");
        }

        if (!"BORROWED".equals(bookStatus)) {
            return new ReservationResponse(false, "Only borrowed books can be reserved");
        }

        if (hasWaitingReservation(userId, bookId)) {
            return new ReservationResponse(false, "You already reserved this book");
        }

        insertWaitingReservation(userId, bookId);
        return new ReservationResponse(true, "Reservation successful");
    }

    public List<UserReservationItem> findReservationsByUserId(int userId) {
        String sql = """
                SELECT r.reservation_id, r.book_id, b.title, r.reservation_time, r.status
                FROM reservations r
                JOIN books b ON r.book_id = b.book_id
                WHERE r.user_id = ?
                ORDER BY r.reservation_time DESC
                """;

        return jdbcTemplate.query(sql, (resultSet, rowNumber) -> new UserReservationItem(
                resultSet.getInt("reservation_id"),
                resultSet.getInt("book_id"),
                resultSet.getString("title"),
                resultSet.getTimestamp("reservation_time").toLocalDateTime(),
                resultSet.getString("status")
        ), userId);
    }

    @Transactional
    public SimpleMessageResponse cancelReservation(int userId, int reservationId) {
        ReservationInfo reservation = findReservationForUpdate(reservationId);

        if (reservation == null) {
            return new SimpleMessageResponse(false, "Reservation not found");
        }

        if (reservation.getUserId() != userId) {
            return new SimpleMessageResponse(false, "You can only cancel your own reservation");
        }

        if (!"WAITING".equals(reservation.getStatus())) {
            return new SimpleMessageResponse(false, "Only waiting reservations can be cancelled");
        }

        updateReservationStatus(reservationId, "CANCELLED");

        if (countWaitingReservationsForBook(reservation.getBookId()) == 0) {
            if (hasActiveBorrowRecord(reservation.getBookId())) {
                updateBookStatus(reservation.getBookId(), "BORROWED");
            } else {
                updateBookStatus(reservation.getBookId(), "AVAILABLE");
            }
        }

        return new SimpleMessageResponse(true, "Reservation cancelled successfully");
    }

    private String findBookStatus(int bookId) {
        String sql = """
                SELECT status
                FROM books
                WHERE book_id = ?
                FOR UPDATE
                """;

        try {
            return jdbcTemplate.queryForObject(sql, String.class, bookId);
        } catch (EmptyResultDataAccessException exception) {
            return null;
        }
    }

    private boolean hasWaitingReservation(int userId, int bookId) {
        String sql = """
                SELECT COUNT(*)
                FROM reservations
                WHERE user_id = ?
                  AND book_id = ?
                  AND status = 'WAITING'
                """;

        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, userId, bookId);
        return count != null && count > 0;
    }

    private void insertWaitingReservation(int userId, int bookId) {
        String sql = """
                INSERT INTO reservations (user_id, book_id, reservation_time, status)
                VALUES (?, ?, CURRENT_TIMESTAMP, 'WAITING')
                """;

        jdbcTemplate.update(sql, userId, bookId);
    }

    private ReservationInfo findReservationForUpdate(int reservationId) {
        String sql = """
                SELECT reservation_id, user_id, book_id, status
                FROM reservations
                WHERE reservation_id = ?
                FOR UPDATE
                """;

        try {
            return jdbcTemplate.queryForObject(sql, (resultSet, rowNumber) -> new ReservationInfo(
                    resultSet.getInt("user_id"),
                    resultSet.getInt("book_id"),
                    resultSet.getString("status")
            ), reservationId);
        } catch (EmptyResultDataAccessException exception) {
            return null;
        }
    }

    private void updateReservationStatus(int reservationId, String status) {
        String sql = """
                UPDATE reservations
                SET status = ?
                WHERE reservation_id = ?
                """;

        jdbcTemplate.update(sql, status, reservationId);
    }

    private int countWaitingReservationsForBook(int bookId) {
        String sql = """
                SELECT COUNT(*)
                FROM reservations
                WHERE book_id = ?
                  AND status = 'WAITING'
                """;

        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, bookId);
        return count == null ? 0 : count;
    }

    private boolean hasActiveBorrowRecord(int bookId) {
        String sql = """
                SELECT COUNT(*)
                FROM borrow_records
                WHERE book_id = ?
                  AND record_status IN ('BORROWING', 'OVERDUE')
                """;

        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, bookId);
        return count != null && count > 0;
    }

    private void updateBookStatus(int bookId, String status) {
        String sql = """
                UPDATE books
                SET status = ?
                WHERE book_id = ?
                """;

        jdbcTemplate.update(sql, status, bookId);
    }

    private static class ReservationInfo {

        private final int userId;
        private final int bookId;
        private final String status;

        ReservationInfo(int userId, int bookId, String status) {
            this.userId = userId;
            this.bookId = bookId;
            this.status = status;
        }

        public int getUserId() {
            return userId;
        }

        public int getBookId() {
            return bookId;
        }

        public String getStatus() {
            return status;
        }
    }
}
