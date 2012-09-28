package helper.logvisualizer;

import org.apache.batik.dom.GenericDOMImplementation;
import org.apache.batik.svggen.SVGGraphics2D;
import org.apache.batik.svggen.SVGGraphics2DIOException;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;

import javax.swing.*;
import java.awt.*;
import java.io.FileWriter;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

/**
 * author: Jacob Schlesinger <schlesinger.jacob@gmail.com>
 * creation date: 9/6/12
 * © Jacob Schlesinger 2012
 */
public class Test {
    private static TimeLineRenderer _renderer;

    public static void main(String[] args) {
        _renderer = new TimeLineRenderer();
        _renderer.setHotizintalScale(0.001);
        ThreadBand b1 = new ThreadBand(_renderer);
        ThreadBand b2 = new ThreadBand(_renderer);
        b1.setThreadName("thread 1");
        b2.setThreadName("thread 2");
        b1.setBaseColor(Helper.lighten(Color.lightGray, 50));
        b2.setBaseColor(Helper.lighten(Color.green, 50));
        _renderer.addBand(b1);
        _renderer.addBand(b2);
        _renderer.addBand(new TimeAxisBand(_renderer));
        // ThreadBand parent, String taskType, String taskID, long waitingForDependencies, long running, long completed
        b1.addTask(new Task(b1, "OverLordTask", "1", 10, 10, 1000));
        b1.addTask(new Task(b1, "OverLordTask", "2", 1005, 1050, 2100));
        b2.addTask(new Task(b2, "SlaveTask", "3", 25, 1015, 1700));


        JFrame frame = new JFrame() {
            @Override
            public void paint(Graphics g) {
                Test.paint((Graphics2D) g);
            }
        };

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1000, 400);
        frame.setVisible(true);

        try {
            writeSVG();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (SVGGraphics2DIOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }

    public static void writeSVG() throws IOException, SVGGraphics2DIOException {
        // Get a DOMImplementation.
        DOMImplementation domImpl =
                GenericDOMImplementation.getDOMImplementation();

        // Create an instance of org.w3c.dom.Document.
        String svgNS = "http://www.w3.org/2000/svg";
        Document document = domImpl.createDocument(svgNS, "svg", null);

        // Create an instance of the SVG Generator.
        SVGGraphics2D svgGenerator = new SVGGraphics2D(document);

        // Ask the test to render into the SVG Graphics2D implementation.
        paint(svgGenerator);

        // Finally, stream out SVG to the standard output using
        // UTF-8 encoding.
        boolean useCSS = true; // we want to use CSS style attributes
        //Writer out = new OutputStreamWriter(System.out, "UTF-8");
        FileWriter out = new FileWriter("/home/jlaw/temp/test.svg");
        svgGenerator.stream(out, useCSS);
    }


    public static void paint(Graphics2D g2d) {
        _renderer.setHeight(_renderer.getRequiredHeight(g2d));
        _renderer.setWidth(_renderer.getRequiredWidth(g2d));
        _renderer.draw(g2d);
    }
}
