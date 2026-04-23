package model;

public class EBook extends DigitalResource {
    private int totalPages;
    private int currentPage;
    private String format;
    private boolean isDownloaded;
    private String downloadDate;

    public EBook(String isbn, String title, String author, String publisher, 
                 int year, int totalCopies, String genre, int totalPages, String format) {
        super(isbn, title, author, publisher, year, totalCopies, genre);
        this.totalPages = totalPages;
        this.currentPage = 0;
        this.format = format;
        this.isDownloaded = false;
        this.downloadDate = null;
    }

    public int getTotalPages() { return totalPages; }
    public int getCurrentPage() { return currentPage; }
    public String getFormat() { return format; }
    public boolean isDownloaded() { return isDownloaded; }
    public String getDownloadDate() { return downloadDate; }

    public void download(String userId) {
        if (!isDownloaded) {
            this.isDownloaded = true;
            this.downloadDate = java.time.LocalDate.now().toString();
            System.out.println("📖 E-book downloaded by user: " + userId);
        } else {
            System.out.println("⚠️ E-book already downloaded.");
        }
    }

    public void read(int pageNumber) {
        if (pageNumber >= 1 && pageNumber <= totalPages) {
            this.currentPage = pageNumber;
            System.out.println("📖 Reading page " + pageNumber + " of " + totalPages);
        } else {
            System.out.println("❌ Invalid page number. Total pages: " + totalPages);
        }
    }

    public void addBookmark() {
        System.out.println("🔖 Bookmark added at page " + currentPage);
    }

    public void showReadingProgress() {
        double progress = (double) currentPage / totalPages * 100;
        System.out.printf("📊 Reading progress: %.1f%% (%d/%d pages)%n", progress, currentPage, totalPages);
    }

    @Override
    public String getItemType() { return "EBook"; }

    @Override
    public String toString() {
        return getItemType() + "," + isbn + "," + title + "," + author + "," + publisher + "," 
               + year + "," + totalCopies + "," + isDamaged + "," + genre + "," + totalPages + "," + format;
    }
}