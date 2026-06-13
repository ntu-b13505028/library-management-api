package library_management_api;

public class ReservationResponse {

    private boolean ok;
    private String message;

    public ReservationResponse(boolean ok, String message) {
        this.ok = ok;
        this.message = message;
    }

    public boolean isOk() {
        return ok;
    }

    public String getMessage() {
        return message;
    }
}
