package jeuDesFourmis.IHM;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import javax.swing.Icon;

public class ButtonIcon implements Icon {
    public static final ButtonIcon.IconShape START;
    public static final ButtonIcon.IconShape PAUSE;
    private static final Color DEFAULT_START_COLOR;
    private static final Color DEFAULT_PAUSE_COLOR;
    private static final Color DEFAULT_BACKGROUND_COLOR;
    private int width;
    private int height;
    private int xrect1;
    private int yrect1;
    private int xrect2;
    private int yrect2;
    private int wrect;
    private int hrect;
    private int xtriangle1;
    private int xtriangle2;
    private int xtriangle3;
    private int ytriangle1;
    private int ytriangle2;
    private int ytriangle3;
    private int[] xtriangle;
    private int[] ytriangle;
    private ButtonIcon.IconShape shape;
    private Color startColor;
    private Color pauseColor;
    private Color backgroundColor;

    public ButtonIcon(ButtonIcon.IconShape var1, int var2, int var3) {
        this.shape = var1;
        this.width = var2;
        this.height = var3;
        this.setStartColor(DEFAULT_START_COLOR);
        this.setPauseColor(DEFAULT_PAUSE_COLOR);
        this.setBackgroundColor(DEFAULT_BACKGROUND_COLOR);
        this.computeDimensions();
    }

    public void setStartColor(Color var1) {
        this.startColor = var1;
    }

    public void setPauseColor(Color var1) {
        this.pauseColor = var1;
    }

    public void setBackgroundColor(Color var1) {
        this.backgroundColor = var1;
    }

    private void computeDimensions() {
        this.xtriangle = new int[3];
        this.ytriangle = new int[3];
        this.xtriangle1 = this.width / 5;
        this.ytriangle1 = this.height / 5;
        this.xtriangle2 = 4 * this.width / 5;
        this.ytriangle2 = this.height / 2;
        this.xtriangle3 = this.xtriangle1;
        this.ytriangle3 = 4 * this.height / 5;
        this.xrect1 = this.xtriangle1;
        this.xrect2 = 3 * this.width / 5;
        this.yrect1 = this.ytriangle1;
        this.yrect2 = this.yrect1;
        this.wrect = this.width / 5;
        this.hrect = 3 * this.height / 5;
    }

    public void paintIcon(Component var1, Graphics var2, int var3, int var4) {
        Graphics2D var5 = (Graphics2D)var2.create();
        var5.setColor(this.backgroundColor);
        var5.fillRect(var3, var4, this.width, this.height);
        switch(this.shape) {
            case Start:
                this.xtriangle[0] = var3 + this.xtriangle1;
                this.ytriangle[0] = var4 + this.ytriangle1;
                this.xtriangle[1] = var3 + this.xtriangle2;
                this.ytriangle[1] = var4 + this.ytriangle2;
                this.xtriangle[2] = var3 + this.xtriangle1;
                this.ytriangle[2] = var4 + this.ytriangle3;
                var5.setColor(this.startColor);
                var5.fillPolygon(this.xtriangle, this.ytriangle, 3);
                break;
            case Pause:
                var5.setColor(this.pauseColor);
                var5.fillRect(var3 + this.xrect1, var4 + this.yrect1, this.wrect, this.hrect);
                var5.fillRect(var3 + this.xrect2, var4 + this.yrect2, this.wrect, this.hrect);
        }

        var5.dispose();
    }

    public int getIconWidth() {
        return this.width;
    }

    public int getIconHeight() {
        return this.height;
    }

    static {
        START = ButtonIcon.IconShape.Start;
        PAUSE = ButtonIcon.IconShape.Pause;
        DEFAULT_START_COLOR = new Color(0, 255, 0);
        DEFAULT_PAUSE_COLOR = new Color(255, 145, 55);
        DEFAULT_BACKGROUND_COLOR = new Color(200, 200, 200);
    }

    public static enum IconShape {
        Start,
        Pause;

        private IconShape() {
        }
    }
}