package library_management_api;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class Book {

    private int bookId;
    private String title;
    private String authors;
    private String subjects;
    private String publisher;
    private Integer publishYear;
    private String edition;
    private String formatDesc;
    private String source;
    private String isbn;
    private String note;
    private String status;

    public Book(int bookId, String title, String authors, String subjects, String status) {
        this.bookId = bookId;
        this.title = title;
        this.authors = authors;
        this.subjects = subjects;
        this.status = status;
    }

    public Book(int bookId, String title, String authors, String subjects, String publisher,
            Integer publishYear, String edition, String formatDesc, String source, String isbn,
            String note, String status) {
        this.bookId = bookId;
        this.title = title;
        this.authors = authors;
        this.subjects = subjects;
        this.publisher = publisher;
        this.publishYear = publishYear;
        this.edition = edition;
        this.formatDesc = formatDesc;
        this.source = source;
        this.isbn = isbn;
        this.note = note;
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

    public String getFormatDesc() {
        return formatDesc;
    }

    public String getSource() {
        return source;
    }

    public String getIsbn() {
        return isbn;
    }

    public String getNote() {
        return note;
    }

    public String getStatus() {
        return status;
    }
}
