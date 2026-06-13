package library_management_api;

public class AdminBookItem {

    private int bookId;
    private String title;
    private String authors;
    private String subjects;
    private String publisher;
    private Integer publishYear;
    private String edition;
    private String isbn;
    private String status;

    public AdminBookItem(int bookId, String title, String authors, String subjects,
            String publisher, Integer publishYear, String edition, String isbn, String status) {
        this.bookId = bookId;
        this.title = title;
        this.authors = authors;
        this.subjects = subjects;
        this.publisher = publisher;
        this.publishYear = publishYear;
        this.edition = edition;
        this.isbn = isbn;
        this.status = status;
    }

    public int getBookId() {
        return bookId;
    }

    public String getTitle() {
        return title;
    }

    public String getAuthors() {
        return authors;
    }

    public String getSubjects() {
        return subjects;
    }

    public String getPublisher() {
        return publisher;
    }

    public Integer getPublishYear() {
        return publishYear;
    }

    public String getEdition() {
        return edition;
    }

    public String getIsbn() {
        return isbn;
    }

    public String getStatus() {
        return status;
    }
}
