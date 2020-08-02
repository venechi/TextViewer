package textviewer;

import javafx.scene.paint.Color;

import java.io.Serializable;

public class SerializableColor implements Serializable, Cloneable {
    private double red;
    private double green;
    private double blue;
    private double alpha;

    public SerializableColor(Color color) {
        this.red = color.getRed();
        this.green = color.getGreen();
        this.blue = color.getBlue();
        this.alpha = color.getOpacity();
    }

    public Color getFXColor() {
        return new Color(red, green, blue, alpha);
    }

    @Override
    protected Object clone() throws CloneNotSupportedException {
        SerializableColor copy = (SerializableColor)super.clone();
        copy.red = this.red;
        copy.green = this.green;
        copy.blue = this.blue;
        copy.alpha = this.alpha;
        return copy;
    }
}
