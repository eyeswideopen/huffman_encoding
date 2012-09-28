package helper.logvisualizer;

//import org.apache.batik.gvt.PatternPaint;

import java.awt.*;
import java.awt.geom.Rectangle2D;

/**
 * author: Jacob Schlesinger <schlesinger.jacob@gmail.com>
 * creation date: 9/8/12
 * © Jacob Schlesinger 2012
 */
public class Task extends UIElement {
    private ThreadBand _parent;
    private String _taskType;
    private String _taskID;
    private long _waitingForDependenciesTS;
    private long _runningTS;
    private long _completedTS;
    private boolean _requiredDimensionsSet;
    private boolean _even;

    public Task(ThreadBand parent, String taskType, String taskID, long waitingForDependencies, long running, long completed) {
        _parent = parent;
        _taskID = taskID;
        _taskType = taskType;
        _waitingForDependenciesTS = waitingForDependencies;
        _runningTS = running;
        _completedTS = completed;
    }

    public Task(ThreadBand parent, String taskType, String taskID) {
        _parent = parent;
        _taskID = taskID;
        _taskType = taskType;
    }

    public void setWaitingForDependenciesTS(long waitingForDependenciesTS) {
        _waitingForDependenciesTS = waitingForDependenciesTS;
    }

    public void setRunningTS(long runningTS) {
        _runningTS = runningTS;
    }

    public void setCompletedTS(long completedTS) {
        _completedTS = completedTS;
    }

    public static Task fromLogEntry(String logEntry) {
        // TODO

        return null;
    }

    public long getWaitingForDependenciesTS() {
        return _waitingForDependenciesTS;
    }

    public boolean isEven() {
        return _even;
    }

    public void setEven(boolean even) {
        _even = even;
    }

    public long getRunningTS() {
        return _runningTS;
    }

    public long getCompletedTS() {
        return _completedTS;
    }

    @Override
    protected void drawInternal(Graphics2D g2d) {
        Color baseColor = _parent.getBaseColor();
        Color c1 = _even ? baseColor : Helper.darken(baseColor, 10);
        Color c2 = Helper.darken(c1, .5f);
        Color cWfd1 = Helper.lighten(c1, 35, -25, -25);
        Color cWfd2 = Helper.darken(cWfd1, .5f);
        Color cFont = Color.black;
        double horScale = _parent.getParent().getHorizontalScale();
        int wfdOffsetX = (int) ((_runningTS - _waitingForDependenciesTS) * horScale);

        g2d.setPaint(c2);
        g2d.drawRect(0, 0, getWidth() - 1, getHeight() - 1);

        if (wfdOffsetX > 0) {
            g2d.setPaint(cWfd1);
            g2d.fillRect(1, 1, wfdOffsetX, getHeight() - 2);
            g2d.translate(wfdOffsetX, 0);
        }
        //else{
        //    wfdOffsetX = 0;
        //}

        g2d.setPaint(c1);
        g2d.fillRect(1, 1, getWidth() - wfdOffsetX - 2, getHeight() - 2);
        //g2d.setPaint(c2);
        //g2d.drawRect(0, 0, getWidth() - wfdOffsetX, getHeight());

        g2d.setPaint(cFont);
        g2d.drawString(getTaskLabel(), 5, getHeight() - 6);
    }

    @Override
    public int getRequiredHeight(Graphics2D g2d) {
        if (!_requiredDimensionsSet) {
            _requiredDimensionsSet = true;
            Rectangle2D textBounds = g2d.getFontMetrics().getStringBounds(getTaskLabel(), g2d);
            setRequiredHeight((int) (textBounds.getHeight() + 10));
            setRequiredWidth((int) ((_completedTS - _waitingForDependenciesTS) * _parent.getParent().getHorizontalScale()));
        }
        return super.getRequiredHeight(g2d);
    }

    public String getTaskLabel() {
        return _taskID + "@" + _taskType;
    }

}
