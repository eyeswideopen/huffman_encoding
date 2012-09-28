package helper.logvisualizer;

import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.util.LinkedList;

/**
 * author: Jacob Schlesinger <schlesinger.jacob@gmail.com>
 * creation date: 9/8/12
 * © Jacob Schlesinger 2012
 */
public class ThreadBand extends Band {
    private String _threadName;
    private Color _baseColor;
    private LinkedList<Task> _tasks;
    private long _earliestTS;
    private long _latestTS;
    private int _requiredHeaderWidth = -1;

    public ThreadBand(TimeLineRenderer parent) {
        super(parent);
        _tasks = new LinkedList<Task>();
        _earliestTS = Long.MAX_VALUE;
    }

    public void addTask(Task task) {
        _tasks.add(task);
        //if (task.getWaitingForDependenciesTS() < _earliestTS)
        //    _earliestTS = task.getWaitingForDependenciesTS();
        //if (task.getCompletedTS() > _latestTS)
        //    _latestTS = task.getCompletedTS();
    }

    @Override
    protected void drawInternal(Graphics2D g2d) {
        super.drawInternal(g2d);

        g2d.setPaint(Color.black);
        Rectangle2D textBounds = g2d.getFontMetrics().getStringBounds(getThreadName(), g2d);
        g2d.drawString(getThreadName(),
                (int) (getRequiredHeaderWidth(g2d) - textBounds.getWidth() - 5),
                getHeight() - (int) (textBounds.getHeight() - (getHeight() - textBounds.getHeight()) / 2.0f));

        g2d.translate(getParent().getHorizontalBandHeaderWidth(g2d), 0);
        boolean even = false;
        for (Task task : _tasks) {
            task.setEven(even = !even);
            task.setOffsetX((int)
                    ((task.getWaitingForDependenciesTS() - getParent().getEarliestTS()) *
                            getParent().getHorizontalScale()));
            task.setWidth((int)
                    ((task.getCompletedTS() - task.getWaitingForDependenciesTS()) *
                            getParent().getHorizontalScale()));
            task.setHeight(getHeight());
            task.draw(g2d);
        }
    }

    public long getEarliestTS() {
        if (_earliestTS == Long.MAX_VALUE) {
            for (Task task : _tasks) {
                if (task.getWaitingForDependenciesTS() < _earliestTS)
                    _earliestTS = task.getWaitingForDependenciesTS();
            }
        }
        return _earliestTS;
    }

    public long getLatestTS() {
        if (_latestTS == 0) {
            for (Task task : _tasks) {
                if (task.getCompletedTS() > _latestTS)
                    _latestTS = task.getCompletedTS();
            }
        }
        return _latestTS;
    }

    public void setBaseColor(Color baseColor) {
        _baseColor = baseColor;
    }

    public Color getBaseColor() {
        return _baseColor;
    }

    public String getThreadName() {
        if (_threadName == null)
            return "";
        return _threadName;
    }

    protected void setThreadName(String threadName) {
        _threadName = threadName;
    }

    @Override
    public int getRequiredHeight(Graphics2D g2d) {
        if (super.getRequiredHeight(g2d) == 0) {
            for (Task task : _tasks)
                if (task.getRequiredHeight(g2d) > super.getRequiredHeight(g2d))
                    setRequiredHeight(task.getRequiredHeight(g2d));
        }
        return super.getRequiredHeight(g2d);
    }

    @Override
    public int getRequiredWidth(Graphics2D g2d) {
        if (super.getRequiredWidth(g2d) == 0) {
            setRequiredWidth((int) (getParent().getHorizontalBandHeaderWidth(g2d) +
                    (_latestTS - _earliestTS) * getParent().getHorizontalScale()));
        }
        return super.getRequiredHeight(g2d);
    }

    @Override
    public int getRequiredHeaderWidth(Graphics2D g2d) {
        if (_requiredHeaderWidth < 0) {
            Rectangle2D textBounds = g2d.getFontMetrics().getStringBounds(getThreadName(), g2d);
            _requiredHeaderWidth = (int) (textBounds.getWidth() + 10);
        }
        return _requiredHeaderWidth;
    }
}
