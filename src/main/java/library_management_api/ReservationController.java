package library_management_api;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ReservationController {

    private final ReservationDAO reservationDAO;

    public ReservationController(ReservationDAO reservationDAO) {
        this.reservationDAO = reservationDAO;
    }

    @PostMapping("/api/reservations")
    public ReservationResponse reserveBook(@RequestBody ReservationRequest request) {
        if (request.getUserId() == null || request.getBookId() == null) {
            return new ReservationResponse(false, "User ID and book ID are required");
        }

        return reservationDAO.reserveBook(request.getUserId(), request.getBookId());
    }

    @GetMapping("/api/users/{userId}/reservations")
    public List<UserReservationItem> getUserReservations(@PathVariable int userId) {
        return reservationDAO.findReservationsByUserId(userId);
    }

    @PatchMapping("/api/users/{userId}/reservations/{reservationId}/cancel")
    public SimpleMessageResponse cancelReservation(
            @PathVariable int userId,
            @PathVariable int reservationId) {
        return reservationDAO.cancelReservation(userId, reservationId);
    }
}
