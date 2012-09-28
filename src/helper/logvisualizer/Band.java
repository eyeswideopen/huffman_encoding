package helper.logvisualizer;

import java.awt.*;

/**
 * author: Jacob Schlesinger <schlesinger.jacob@gmail.com>
 * creation date: 9/6/12
 * © Jacob Schlesinger 2012
 */
public class Band extends UIElement {
    private TimeLineRenderer _parent;

    public Band(TimeLineRenderer parent) {
        _parent = parent;
    }

    public TimeLineRenderer getParent() {
        return _parent;
    }

    @Override
    protected void drawInternal(Graphics2D g2d) {
        g2d.setPaint(Color.gray);
        g2d.drawLine(
                _parent.getHorizontalBandHeaderWidth(g2d),
                0,
                _parent.getHorizontalBandHeaderWidth(g2d),
                getRequiredHeight(g2d));
    }


    public int getRequiredHeaderWidth(Graphics2D g2d) {
        return 0;
    }
}
