package textviewer;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.scene.text.TextFlow;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Pair;

import java.util.stream.Collectors;

public class Controller {
    private Model model;

    @FXML
    private BorderPane mainWindow;

    @FXML
    private MenuBar menuBar;

    @FXML
    private TextFlow textLeft;

    @FXML
    private TextFlow textRight;

    @FXML
    private Spinner<Integer> currentPage;

    @FXML
    private Label totalPage;

    @FXML
    private Button changePage;

    @FXML
    public void initialize() {
        model = Model.getModelInstance();
        mainWindow.heightProperty().addListener((observable, oldValue, newValue) -> {
            model.setTextWindowHeight(newValue.intValue() - 27);
            currentPage.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, model.getTotalPages()));
            currentPage.getValueFactory().setValue(model.getCurrentPage());
            totalPage.setText(" / " + model.getTotalPages());
            printTextOnScreen();
        });
        textRight.widthProperty().addListener((observable, oldValue, newValue) -> {
            model.setTextWindowWidth(newValue.intValue());
            currentPage.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, model.getTotalPages()));
            currentPage.getValueFactory().setValue(model.getCurrentPage());
            totalPage.setText(" / " + model.getTotalPages());
            printTextOnScreen();
        });
        model.restoreSettingsAndBookmarks();
        textLeft.setPadding(new Insets(model.getSettings().getPadding()));
        textLeft.setLineSpacing(model.getSettings().getLineSpacing());
        textRight.setPadding(new Insets(model.getSettings().getPadding()));
        textRight.setLineSpacing(model.getSettings().getLineSpacing());
        currentPage.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 0));
        //currentPage.getEditor().setAlignment(Pos.CENTER_RIGHT);
        currentPage.getEditor().textProperty().addListener((observable, oldValue, newValue) -> {
            try {
                int newOne = newValue.equals("") ? 0 : Integer.parseInt(newValue);
                if (newOne > model.getTotalPages())
                    currentPage.getEditor().textProperty().setValue(String.valueOf(model.getTotalPages()));
            } catch (NumberFormatException e) {
                currentPage.getEditor().textProperty().setValue(String.valueOf(model.getCurrentPage()));
            }
        });

        currentPage.valueProperty().addListener((observable, oldValue, newValue) -> {
            //ToDO: 첫 페이지와 마지막 페이지에서 스피너를 이용해 없는 페이지로 이동을 시도하는경우 무한루프 증상 해결
            int newOne = newValue;
            if(newOne != model.getCurrentPage()) {
                if (newOne % 2 == 0) {
                    if (newOne - model.getCurrentPage() == 2)
                        model.nextPage();
                    else if (model.getCurrentPage() - newOne == 2)
                        model.prevPage();
                    else
                        model.setCurrentPage(newOne);
                } else {
                    model.setCurrentPage(newOne - 1);
                }
                System.out.println("newOne: " + newOne + " currentPage: " + model.getCurrentPage());
                currentPage.getValueFactory().setValue(model.getCurrentPage());
                printTextOnScreen();
            }
        });

        changePage.setOnAction(event -> currentPage.getValueFactory().setValue(Integer.parseInt(currentPage.getEditor().textProperty().getValue())));
    }

    public void openSettingsMenu() throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("../res/layout/SettingsWindow.fxml"));
        Stage stage = new Stage();
        stage.setTitle("설정");
        stage.getIcons().add(new Image(getClass().getResource("../res/image/settings.png").toString()));
        stage.setScene(new Scene(root));
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.show();
    }

    private void printTextOnScreen() {
        if (model.getLeftPage() != null) {
            textLeft.getChildren().clear();
            textLeft.getChildren().addAll(model.getLeftPage().stream().map(Pair::getKey).collect(Collectors.toList()));
            textRight.getChildren().clear();
            textRight.getChildren().addAll(model.getRightPage().stream().map(Pair::getKey).collect(Collectors.toList()));
        }
    }

    public void openFile() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("텍스트 파일", "*.txt")
        );
        model.setCurrentFile(fileChooser.showOpenDialog(mainWindow.getScene().getWindow()));
        if (model.getCurrentFile() != null) {
            Stage primaryStage = (Stage) mainWindow.getScene().getWindow();
            primaryStage.setTitle("Text Viewer [" + model.getCurrentFile().getPath() + "]");
            currentPage.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(-2, model.getTotalPages() % 2 == 0 ? model.getTotalPages() + 2 : model.getTotalPages() + 1, 0, 2));
            totalPage.setText(" / " + model.getTotalPages());
            printTextOnScreen();
        }
    }

    public void nextPage() {
        if (!model.nextPage())
            openFile();
        else {
            currentPage.getValueFactory().setValue(model.getCurrentPage());
            printTextOnScreen();
        }
    }

    public void prevPage() {
        if (!model.prevPage())
            openFile();
        else {
            currentPage.getValueFactory().setValue(model.getCurrentPage());
            printTextOnScreen();
        }
    }
}
