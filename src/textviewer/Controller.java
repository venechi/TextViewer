package textviewer;

import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.Alert;
import javafx.scene.control.MenuBar;
import javafx.scene.layout.BorderPane;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.FileChooser;
import javafx.util.Pair;

import java.io.*;
import java.util.ArrayList;
import java.util.Optional;
import java.util.Scanner;
import java.util.stream.Collectors;

public class Controller {
    private Model model;

    @FXML
    public BorderPane mainWindow;

    @FXML
    public MenuBar menuBar;

    @FXML
    public TextFlow textLeft;

    @FXML
    public TextFlow textRight;

    @FXML
    public void initialize() {
        model = new Model();
        mainWindow.heightProperty().addListener((observable, oldValue, newValue) -> {
            model.setTextWindowHeight(newValue.intValue() - 27);
            printTextOnScreen();
        });
        textRight.widthProperty().addListener((observable, oldValue, newValue) -> {
            model.setTextWindowWidth(newValue.intValue());
            printTextOnScreen();
        });
        model.restoreSettingsAndBookmarks();
        textLeft.setPadding(new Insets(model.getSettings().getPadding()));
        textLeft.setLineSpacing(model.getSettings().getLineSpacing());
        textRight.setPadding(new Insets(model.getSettings().getPadding()));
        textRight.setLineSpacing(model.getSettings().getLineSpacing());
    }

    private void printTextOnScreen() {
        if (model.getLeftPage() != null) {
            textLeft.getChildren().clear();
            textLeft.getChildren().addAll(model.getLeftPage().stream().map(t -> t.getKey()).collect(Collectors.toList()));
            textRight.getChildren().clear();
            textRight.getChildren().addAll(model.getRightPage().stream().map(t -> t.getKey()).collect(Collectors.toList()));
        }
    }

    public void openFile() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("텍스트 파일", "*.txt")
        );
        model.setCurrentFile(fileChooser.showOpenDialog(mainWindow.getScene().getWindow()));
        printTextOnScreen();
    }

    public void nextPage() {
        model.nextPage();
        printTextOnScreen();
    }

    public void prevPage() {
        model.prevPage();
        printTextOnScreen();
    }
}
