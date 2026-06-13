package library_management_api;

public class SimpleMessageResponse {

    private boolean ok;
    private String message;

    public SimpleMessageResponse(boolean ok, String message) {
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
