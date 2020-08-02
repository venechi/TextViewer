package textviewer;

import javafx.scene.paint.Color;
import javafx.scene.text.Font;

import java.io.Serializable;


public class Settings implements Serializable, Cloneable {
    private int padding;
    private int lineSpacing;
    private String fontFamily;
    private int fontSize;
    private SerializableColor charColor;
    private SerializableColor bgColor;

    Settings() {
        padding = 30;
        lineSpacing = 0;
        fontFamily = "맑은 고딕";
        fontSize = 12;
        charColor = new SerializableColor(Color.BLACK);
        bgColor = new SerializableColor(Color.WHITE);
    }

    public int getPadding() {
        return padding;
    }

    public void setPadding(int padding) {
        this.padding = padding;
    }

    public Font getFont() {
        return new Font(fontFamily, fontSize);
    }

    public void setFont(Font font) {
        fontFamily = font.getFamily();
        fontSize = (int) font.getSize();
    }

    public int getLineSpacing() {
        return lineSpacing;
    }

    public void setLineSpacing(int lineSpacing) {
        this.lineSpacing = lineSpacing;
    }

    public Color getCharColor() {
        return charColor.getFXColor();
    }

    public void setCharColor(Color charColor) {
        this.charColor = new SerializableColor(charColor);
    }

    public Color getBgColor() {
        return bgColor.getFXColor();
    }

    public void setBgColor(Color bgColor) {
        this.bgColor = new SerializableColor(bgColor);
    }

    public void applyNewSettings(Settings settings) {
        this.setCharColor(settings.getCharColor());
        this.setBgColor(settings.getBgColor());
        this.setFont(settings.getFont());
        this.setPadding(settings.getPadding());
        this.setLineSpacing(settings.getLineSpacing());
    }

    @Override
    protected Object clone() throws CloneNotSupportedException {
        Settings copy = (Settings) super.clone();
        copy.padding = this.padding;
        copy.lineSpacing = this.lineSpacing;
        copy.fontFamily = this.fontFamily;
        copy.fontSize = this.fontSize;
        copy.charColor = (SerializableColor) this.charColor.clone();
        copy.bgColor = (SerializableColor) this.bgColor.clone();
        return copy;
    }
}
