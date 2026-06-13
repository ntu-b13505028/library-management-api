package library_management_api;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class BorrowResponse {

    private boolean ok;
    private String message;
    private Integer borrowDays;

    public BorrowResponse(boolean ok, String message) {
        this.ok = ok;
        this.message = message;
    }

    public BorrowResponse(boolean ok, String message, Integer borrowDays) {
        this.ok = ok;
        this.message = message;
        this.borrowDays = borrowDays;
    }

    public boolean isOk() {
        return ok;
    }

    public String getMessage() {
        return message;
    }

    public Integer getBorrowDays() {
        return borrowDays;
    }
}
