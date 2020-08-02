package textviewer;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.text.Font;
import javafx.stage.Stage;

public class SettingsController {
    private Model model;
    private Settings tempSettings;

    @FXML
    private ColorPicker charColor;

    @FXML
    private ColorPicker bgColor;

    @FXML
    private ChoiceBox<String> fontFamily;

    @FXML
    private Spinner<Integer> fontSize;

    @FXML
    private Spinner<Integer> padding;

    @FXML
    private Spinner<Integer> lineSpacing;

    @FXML
    private Button applyButton;

    @FXML
    private Button cancelButton;

    @FXML
    public void initialize() throws CloneNotSupportedException {
        //ToDo: 설정이 바뀐 경우, 바뀐 설정을 반영하여 사용자 화면 업데이트
        model = Model.getModelInstance();
        tempSettings = (Settings)model.getSettings().clone();
        charColor.setValue(tempSettings.getCharColor());
        bgColor.setValue(tempSettings.getBgColor());
        fontFamily.getItems().addAll(javafx.scene.text.Font.getFamilies());
        fontFamily.setValue(tempSettings.getFont().getFamily());
        fontSize.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 4096));
        padding.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 4096));
        lineSpacing.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 4096));

        charColor.setOnAction(event -> tempSettings.setCharColor(charColor.getValue()));
        bgColor.setOnAction(event -> tempSettings.setBgColor(bgColor.getValue()));
        fontFamily.setOnAction(event -> {
            System.out.println(fontFamily.getValue());
            tempSettings.setFont(new Font(fontFamily.getValue(), tempSettings.getFont().getSize()));
        });
        fontSize.getEditor().textProperty().addListener((observable, oldValue, newValue) -> {
            try {
                tempSettings.setFont(new Font(tempSettings.getFont().getFamily(), Integer.parseInt(newValue)));
            } catch (NumberFormatException e) {
                fontSize.getEditor().textProperty().setValue(String.valueOf(tempSettings.getFont().getSize()));
            }
        });
        fontSize.getValueFactory().setValue((int)tempSettings.getFont().getSize());
        padding.getEditor().textProperty().addListener((observable, oldValue, newValue) -> {

        });
        padding.getValueFactory().setValue(tempSettings.getPadding());
        lineSpacing.getEditor().textProperty().addListener((observable, oldValue, newValue) -> {

        });
        lineSpacing.getValueFactory().setValue(tempSettings.getLineSpacing());
        applyButton.setOnMouseClicked(event -> {
            model.getSettings().applyNewSettings(tempSettings);
            Stage stage = (Stage) applyButton.getScene().getWindow();
            stage.close();
        });
        cancelButton.setOnMouseClicked(event -> {
            Stage stage = (Stage) cancelButton.getScene().getWindow();
            stage.close();
        });
    }
}
