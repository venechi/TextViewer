package textviewer;

import javafx.scene.control.Alert;
import javafx.scene.text.Text;
import javafx.util.Pair;

import java.io.*;
import java.util.ArrayList;
import java.util.Scanner;

public class Model {
    private Settings settings = null;
    private static Model model = null;

    private ArrayList<BookMark> bookMarks = null;
    private ArrayList<Text> rawData = null;
    private ArrayList<Pair<Text, Integer>> lines = null;
    private ArrayList<Pair<Text, Integer>> leftPage = null;
    private ArrayList<Pair<Text, Integer>> rightPage = null;
    private File currentFile = null;
    private int textWindowWidth = 0;
    private int textWindowHeight = 0;
    private int linesPerPage = 0;
    private int currentPage = 0;
    private int totalPages = 0;

    public ArrayList<Pair<Text, Integer>> getLeftPage() {
        return leftPage;
    }

    public ArrayList<Pair<Text, Integer>> getRightPage() {
        return rightPage;
    }

    public int getCurrentPage() {
        return currentPage;
    }

    public void setCurrentPage(int currentPage) {
        if (currentPage >= 0 && currentPage <= totalPages) {
            this.currentPage = currentPage;
            updatePages();
        }
    }

    public Settings getSettings() {
        return settings;
    }

    public File getCurrentFile() {
        return currentFile;
    }

    private Model() {}

    public static Model getModelInstance() {
        if(model == null){
            model = new Model();
        }
        return model;
    }

    public void setCurrentFile(File currentFile) {
        if (currentFile != null) {
            this.currentFile = currentFile;
            if (!currentFile.canRead()) {
                showAlert("파일 읽기 에러", null, "파일 읽기 권한이 있는지 확인하십시오");
                return;
            }
            try {
                Scanner scanner = new Scanner(currentFile);
                rawData = new ArrayList<>();
                while (scanner.hasNextLine()) {
                    Text line = new Text(scanner.nextLine() + "\n");
                    line.setFont(settings.getFont());
                    line.setFill(settings.getCharColor());
                    line.setLineSpacing(settings.getLineSpacing());
                    rawData.add(line);
                }
                scanner.close();
            } catch (FileNotFoundException e) {
                showAlert("파일 읽기 에러", null, "파일이 해당 경로에 존재하는지 확인하십시오");
                return;
            }
            trimLines();
            updatePageMetadata();
            setCurrentPage(0);
            updatePages();
        }
    }

    public void setTextWindowWidth(int textWindowWidth) {
        this.textWindowWidth = textWindowWidth - (settings.getPadding() * 2);
        if (currentFile != null) {
            Integer currentLine = leftPage.get(0).getValue();
            trimLines();
            updatePageMetadata();
            int newLine = lines.indexOf(lines.stream().filter(line -> line.getValue().equals(currentLine)).findFirst().get());
            int page = (int) Math.floor(newLine / linesPerPage);
            if (page % 2 == 1)
                --page;
            setCurrentPage(page);
            updatePages();
        }
    }

    public void setTextWindowHeight(int textWindowHeight) {
        this.textWindowHeight = textWindowHeight - (getSettings().getPadding() * 2);
        if (getCurrentFile() != null) {
            Integer currentLine = leftPage.get(0).getValue();
            updatePageMetadata();
            int newLine = lines.indexOf(lines.stream().filter(line -> line.getValue().equals(currentLine)).findFirst().get());
            int page = (int) Math.floor((float)newLine / linesPerPage);
            if (page % 2 == 1)
                --page;
            setCurrentPage(page);
            updatePages();
        }
    }

    private void updatePageMetadata() {
        Text toMeasureSize = new Text();
        toMeasureSize.setFont(getSettings().getFont());
        linesPerPage = (int) Math.floor(
                (textWindowHeight - toMeasureSize.getLayoutBounds().getHeight())
                        / (toMeasureSize.getLayoutBounds().getHeight() + settings.getLineSpacing())
        ) + 1;
        totalPages = (int) Math.ceil((double) lines.size() / linesPerPage) - 1;
    }

    public int getTotalPages() {
        return totalPages;
    }

    private <T> void serializeObjectAndSaveToFile(String filePath, T object) throws IOException {
        File file = new File(filePath);
        if (file.exists())
            file.delete();

        file.createNewFile();
        FileOutputStream fileOutputStream = new FileOutputStream(file);
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream);
        objectOutputStream.writeObject(object);
        objectOutputStream.close();
        fileOutputStream.close();
    }

    private Object deSerializeObjectFromFile(String filePath) throws IOException, ClassNotFoundException {
        File file = new File(filePath);
        FileInputStream fileInputStream = new FileInputStream(file);
        ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream);
        Object result = objectInputStream.readObject();
        objectInputStream.close();
        fileInputStream.close();
        return result;
    }

    public void saveSettingsAndBookmarks() {
        File folder = new File(Constants.FOLDER_PATH);
        if (!folder.exists()) {
            folder.mkdir();
        }

        try {
            serializeObjectAndSaveToFile(Constants.SETTING_FILE_PATH, getSettings());
        } catch (IOException e) {
            showAlert("설정 저장 에러", null, "설정을 저장할 수 없습니다");
        }

        try {
            serializeObjectAndSaveToFile(Constants.BOOKMARKS_FILE_PATH, bookMarks);
        } catch (IOException e) {
            showAlert("책갈피 저장 에러", null, "책갈피를 저장할 수 없습니다");
        }
    }

    public void restoreSettingsAndBookmarks() {
        File rootDir = new File(Constants.FOLDER_PATH);
        if (rootDir.exists()) {
            try {
                settings = (Settings) deSerializeObjectFromFile(Constants.SETTING_FILE_PATH);
            } catch (IOException e) {
                showAlert("설정 불러오기 에러", "기존 설정값을 읽어올 수 없습니다", "프로그램 설정이 기본값으로 변경됩니다");
                settings = new Settings();
            } catch (ClassNotFoundException e) {
            }

            try {
                bookMarks = (ArrayList<BookMark>) deSerializeObjectFromFile(Constants.BOOKMARKS_FILE_PATH);
            } catch (IOException e) {
                showAlert("북마크 불러오기 에러", "북마크 불러오기 에러", "저장된 북마크를 불러올 수 없습니다");
                settings = new Settings();
            } catch (ClassNotFoundException e) {
            }
        } else {
            settings = new Settings();
        }
    }

    private ArrayList<Text> splitLine(Text original) {
        int pivot;
        double dividend;
        ArrayList<Text> result = new ArrayList<>();
        String string = original.getText();
        Text t = new Text(string);
        t.setFont(settings.getFont());
        do {
            dividend = original.getLayoutBounds().getWidth() / textWindowWidth;
            pivot = (int) Math.ceil(string.length() / dividend);
            while (true) {
                t.setText(string.substring(0, pivot));
                if (t.getLayoutBounds().getWidth() > textWindowWidth)
                    --pivot;
                else {
                    Text r = new Text(string.substring(0, pivot) + "\n");
                    r.setFont(settings.getFont());
                    result.add(r);
                    string = string.substring(pivot);
                    t.setText(string);
                    break;
                }
            }
        } while (t.getLayoutBounds().getWidth() > textWindowWidth);
        result.add(t);
        return result;
    }

    private void trimLines() {
        if (rawData == null)
            return;
        lines = new ArrayList<>();
        for (int i = 0; i < rawData.size(); ++i) {
            Text text = rawData.get(i);
            if (text.getLayoutBounds().getWidth() <= textWindowWidth) {
                lines.add(new Pair<>(text, i));
            } else {
                for (Text t : splitLine(text))
                    lines.add(new Pair<>(t, i));
            }
        }
    }

    private ArrayList<Pair<Text, Integer>> getPage(int pageNum) {
        ArrayList<Pair<Text, Integer>> page;
        if (lines != null && pageNum >= 0 && pageNum <= totalPages) {
            if (linesPerPage * (pageNum + 1) - 1 < lines.size()) {
                page = new ArrayList<>(lines.subList(
                        linesPerPage * pageNum, linesPerPage * (pageNum + 1)
                ));
            } else {
                page = new ArrayList<>(lines.subList(
                        linesPerPage * pageNum, lines.size()));
            }
        } else
            page = new ArrayList<>();
        return page;
    }

    public void updatePages() {
        leftPage = getPage(getCurrentPage());
        rightPage = getPage(getCurrentPage() + 1);
    }

    public boolean nextPage() {
        if (getCurrentFile() == null) {
            return false;
        } else {
            if (getCurrentPage() + 1 < getTotalPages()) {
                setCurrentPage(getCurrentPage() + 2);
                updatePages();
            } else {
                showAlert("마지막 페이지입니다", null, "마지막 페이지입니다");
            }
            return true;
        }
    }

    public boolean prevPage() {
        if (getCurrentFile() == null) {
            return false;
        } else {
            if (currentPage > 0) {
                setCurrentPage(getCurrentPage() - 2);
                updatePages();
            } else {
                showAlert("첫번째 페이지입니다", null, "첫번째 페이지입니다");
            }
            return true;
        }
    }

    private void showAlert(String title, String header, String content) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.show();
    }
}
