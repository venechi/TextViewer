package textviewer;

public class BookMark {
    private final String filePath;
    private final long line;

    BookMark(String filePath, long line) {
        this.filePath = filePath;
        this.line = line;
    }

    public String getFilePath() {
        return filePath;
    }

    public long getLine() {
        return line;
    }
}
