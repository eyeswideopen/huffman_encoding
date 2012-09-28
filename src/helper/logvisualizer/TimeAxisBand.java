package helper.logvisualizer;

import java.awt.*;
import java.awt.geom.Rectangle2D;

/**
 * author: Jacob Schlesinger <schlesinger.jacob@gmail.com>
 * creation date: 9/11/12
 * © Jacob Schlesinger 2012
 */
public class TimeAxisBand extends Band {

    public TimeAxisBand(TimeLineRenderer parent) {
        super(parent);
    }

    @Override
    protected void drawInternal(Graphics2D g2d) {
        super.drawInternal(g2d);

        g2d.translate(getParent().getHorizontalBandHeaderWidth(g2d), 0);
        g2d.setPaint(Color.black);
        g2d.drawLine(0, 0, getWidth(), 0);
        double offsetX = 0;
        long value = 0;
        String stringValue;
        Distributor dist = new Distributor();
        long range = getParent().getLatestTS() - getParent().getEarliestTS();
        dist.setDisplayRange((long) (range * getParent().getHorizontalScale()));
        dist.setValueRange(range);
        Rectangle2D maxBounds = g2d.getFontMetrics().getStringBounds(Long.toString(range), g2d);
        dist.setMinDisplayGap((long) (maxBounds.getWidth() * 1.5));
        while (offsetX < getWidth()) {
            stringValue = Long.toString(value);
            g2d.drawLine((int) offsetX, 0, (int) offsetX, 5);
            Rectangle2D bounds = g2d.getFontMetrics().getStringBounds(stringValue, g2d);
            if (value > 0)
                g2d.drawString(stringValue, (int) (offsetX - bounds.getWidth() / 2.0), (int) (10 + bounds.getHeight()));
            value += dist.getValueGap();
            offsetX += dist.getDisplayGap();
        }
    }

    @Override
    public int getRequiredHeight(Graphics2D g2d) {
        if (super.getRequiredHeight(g2d) == 0) {
            Rectangle2D bounds = g2d.getFontMetrics().getStringBounds("0", g2d);
            setRequiredHeight((int) (bounds.getHeight() + 15));
        }
        return super.getRequiredHeight(g2d);
    }
}
