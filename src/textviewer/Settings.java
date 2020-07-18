package textviewer;

import javafx.scene.text.Font;

import java.io.Serializable;


public class Settings implements Serializable {
    private int padding;
    private int lineSpacing;
    private Font font;

    Settings() {
        padding = 30;
        lineSpacing = 0;
        font = new Font("맑은 고딕",12);
    }

    public int getPadding() {
        return padding;
    }

    public void setPadding(int padding) {
        this.padding = padding;
    }

    public Font getFont() {
        return font;
    }

    public void setFont(Font font) {
        this.font = font;
    }

    public int getLineSpacing() {
        return lineSpacing;
    }

    public void setLineSpacing(int lineSpacing) {
        this.lineSpacing = lineSpacing;
    }
}
