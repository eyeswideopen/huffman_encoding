package helper.logvisualizer;

import java.awt.*;
import java.awt.geom.AffineTransform;

/**
 * author: Jacob Schlesinger <schlesinger.jacob@gmail.com>
 * creation date: 9/8/12
 * © Jacob Schlesinger 2012
 */
public abstract class UIElement {

    private int _width;
    private int _height;
    private int _requiredWidth;
    private int _requiredHeight;
    private int _offsetX;
    private int _offsetY;

    public int getWidth() {
        return _width;
    }

    public int getHeight() {
        return _height;
    }

    public void setWidth(int width) {
        _width = width;
    }

    public void setHeight(int height) {
        _height = height;
    }

    public int getRequiredHeight(Graphics2D g2d) {
        return _requiredHeight;
    }

    public int getRequiredWidth(Graphics2D g2d) {
        return _requiredWidth;
    }

    protected void setRequiredWidth(int requiredWidth) {
        _requiredWidth = requiredWidth;
    }

    protected void setRequiredHeight(int requiredHeight) {
        _requiredHeight = requiredHeight;
    }

    public int getOffsetX() {
        return _offsetX;
    }

    public int getOffsetY() {
        return _offsetY;
    }

    public void setOffsetX(int offsetX) {
        _offsetX = offsetX;
    }

    public void setOffsetY(int offsetY) {
        _offsetY = offsetY;
    }

    public void draw(Graphics2D g2d) {
        AffineTransform transform = g2d.getTransform();
        g2d.translate(_offsetX, _offsetY);
        Shape clip = g2d.getClip();
        g2d.setClip(0, 0, _width, _height);
        try {
            drawInternal(g2d);
        } finally {
            g2d.setClip(clip);
            g2d.setTransform(transform);
        }
    }

    protected abstract void drawInternal(Graphics2D g2d);
}
