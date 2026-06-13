package library_management_api;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class ReturnResponse {

    private boolean ok;
    private String message;
    private String bookStatus;

    public ReturnResponse(boolean ok, String message) {
        this.ok = ok;
        this.message = message;
    }

    public ReturnResponse(boolean ok, String message, String bookStatus) {
        this.ok = ok;
        this.message = message;
        this.bookStatus = bookStatus;
    }

    public boolean isOk() {
        return ok;
    }

    public String getMessage() {
        return message;
    }

    public String getBookStatus() {
        return bookStatus;
    }
}
