package library_management_api;

public class TopBookItem {

    private int rank;
    private int bookId;
    private String title;
    private int borrowCount;

    public TopBookItem(int rank, int bookId, String title, int borrowCount) {
        this.rank = rank;
        this.bookId = bookId;
        this.title = title;
        this.borrowCount = borrowCount;
    }

    public int getRank() {
        return rank;
    }

    public int getBookId() {
        return bookId;
    }

    public String getTitle() {
        return title;
    }

    public int getBorrowCount() {
        return borrowCount;
    }
}
