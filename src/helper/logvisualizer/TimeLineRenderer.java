package helper.logvisualizer;

import java.awt.*;
import java.util.Collection;
import java.util.LinkedList;

/**
 * author: Jacob Schlesinger <schlesinger.jacob@gmail.com>
 * creation date: 9/6/12
 * © Jacob Schlesinger 2012
 */
public class TimeLineRenderer extends UIElement {
    private Collection<Band> _bands;
    private int _verticalBandGap = 5;
    private double _horizontalScale = 1.0;
    private boolean _requiredDimensionsSet;

    public int getHorizontalBandHeaderWidth(Graphics2D g2d) {
        int horizontalBandHeaderWidth = 0;
        for (Band band : _bands)
            if (horizontalBandHeaderWidth < band.getRequiredHeaderWidth(g2d))
                horizontalBandHeaderWidth = band.getRequiredHeaderWidth(g2d);
        return horizontalBandHeaderWidth;
    }

    public long getEarliestTS() {
        long earliestTS = Long.MAX_VALUE;
        for (Band band : _bands)
            if (band instanceof ThreadBand) {
                ThreadBand threadBand = (ThreadBand) band;
                if (threadBand.getEarliestTS() < earliestTS)
                    earliestTS = threadBand.getEarliestTS();
            }
        return earliestTS;
    }

    public long getLatestTS() {
        long latestTS = 0;
        for (Band band : _bands)
            if (band instanceof ThreadBand) {
                ThreadBand threadBand = (ThreadBand) band;
                if (threadBand.getLatestTS() > latestTS)
                    latestTS = threadBand.getLatestTS();
            }
        return latestTS;
    }

    public int getVerticalBandGap() {
        return _verticalBandGap;
    }

    public double getHorizontalScale() {
        return _horizontalScale;
    }

    public void setHotizintalScale(double scale) {
        _horizontalScale = scale;
    }

    public void addBand(Band band) {
        _bands.add(band);
    }

    public TimeLineRenderer() {
        _bands = new LinkedList<Band>();
        setOffsetX(5);
        setOffsetY(5);
    }

    @Override
    protected void drawInternal(Graphics2D g2d) {
        int offsetY = 0;
        for (Band band : _bands) {
            band.setHeight(band.getRequiredHeight(g2d));
            band.setWidth(getRequiredWidth(g2d) - 10);
            offsetY += _verticalBandGap;
            band.setOffsetY(offsetY);
            band.draw(g2d);
            offsetY += band.getHeight();

        }
    }

    @Override
    public int getRequiredHeight(Graphics2D g2d) {
        if (!_requiredDimensionsSet)
            setRequiredDimensions(g2d);
        return super.getRequiredHeight(g2d);
    }

    @Override
    public int getRequiredWidth(Graphics2D g2d) {
        if (!_requiredDimensionsSet)
            setRequiredDimensions(g2d);
        return super.getRequiredWidth(g2d);
    }

    private void setRequiredDimensions(Graphics2D g2d) {
        _requiredDimensionsSet = true;
        int height = 0;
        for (Band band : _bands) {
            height += band.getRequiredHeight(g2d);
        }
        height += (_bands.size() - 1) * _verticalBandGap;

        int width = (int) (getHorizontalBandHeaderWidth(g2d) + (getLatestTS() - getEarliestTS()) * _horizontalScale);

        setRequiredHeight(height + 10);
        setRequiredWidth(width + 10);
    }
}
