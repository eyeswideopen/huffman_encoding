package helper.logvisualizer;

import java.awt.*;

/**
 * author: Jacob Schlesinger <schlesinger.jacob@gmail.com>
 * creation date: 9/9/12
 * © Jacob Schlesinger 2012
 */
public class Helper {


    public static Color darken(Color c, int r, int g, int b) {
        int or = c.getRed(), og = c.getGreen(), ob = c.getBlue();
        or -= r;
        og -= g;
        ob -= b;
        or = or < 0 ? 0 : or;
        or = or > 255 ? 255 : or;
        og = og < 0 ? 0 : og;
        og = og > 255 ? 255 : og;
        ob = ob < 0 ? 0 : ob;
        ob = ob > 255 ? 255 : ob;
        return new Color(or, og, ob);
    }

    public static Color lighten(Color c, int r, int g, int b) {
        return darken(c, -r, -g, -b);
    }

    public static Color darken(Color c, float fr, float fg, float fb) {
        int r = c.getRed(), g = c.getGreen(), b = c.getBlue();
        return darken(c, (int) (r * (1f - fr)), (int) (g * (1f - fg)), (int) (b * (1f - fb)));
    }

    public static Color lighten(Color c, float fr, float fg, float fb) {
        return darken(c, -fr, -fg, -fb);
    }

    public static Color darken(Color c, int by) {
        return darken(c, by, by, by);
    }

    public static Color lighten(Color c, int by) {
        return darken(c, -by);
    }

    public static Color darken(Color c, float by) {
        return darken(c, by, by, by);
    }

    public static Color lighten(Color c, float by) {
        return darken(c, -by);
    }
}
