package library_management_api;

public class TopSubjectItem {

    private int rank;
    private String subjects;
    private int borrowCount;

    public TopSubjectItem(int rank, String subjects, int borrowCount) {
        this.rank = rank;
        this.subjects = subjects;
        this.borrowCount = borrowCount;
    }

    public int getRank() {
        return rank;
    }

    public String getSubjects() {
        return subjects;
    }

    public int getBorrowCount() {
        return borrowCount;
    }
}
