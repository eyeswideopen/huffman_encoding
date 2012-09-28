package helper.logvisualizer;

/**
 * author: Jacob Schlesinger <schlesinger.jacob@gmail.com>
 * creation date: 9/11/12
 * © Jacob Schlesinger 2012
 */

public class Distributor {

    private long _valueRange;
    private long _displayRange;
    private long _minDisplayGap;
    private double _displayGap;
    private long _valueGap;

    public void setValueRange(long valueRange) {
        _valueRange = valueRange;
        if (_valueRange > 0 && _displayRange > 0)
            updateValues();
    }

    public void setDisplayRange(long displayRange) {
        _displayRange = displayRange;
        if (_valueRange > 0 && _displayRange > 0)
            updateValues();
    }

    public void setMinDisplayGap(long minDisplayGap) {
        _minDisplayGap = minDisplayGap;
        if (_valueRange > 0 && _displayRange > 0)
            updateValues();
    }

    private void updateValues() {
        long valueGap = 1;
        while (true) {
            if (_displayRange * valueGap / _valueRange > _minDisplayGap) {
                _valueGap = valueGap;
                _displayGap = _displayRange * _valueGap / _valueRange;
                break;
            }
            valueGap *= 2;
            int i = 0;
            if (valueGap >= 200)
                while (valueGap > 40) {
                    valueGap /= 10;
                    i++;
                }
            if (valueGap == 4)
                valueGap = 5;
            else if (valueGap == 40)
                valueGap = 25;

            for (int l = 0; l < i; l++)
                valueGap *= 10;
        }
    }

    public double getDisplayGap() {
        return _displayGap;
    }

    public long getValueGap() {
        return _valueGap;
    }

    public long getMinDisplayGap() {
        return _minDisplayGap;
    }
}

