package helper.logvisualizer;

import encoder.parallelization.EnRunningStates;
import org.apache.batik.dom.GenericDOMImplementation;
import org.apache.batik.svggen.SVGGraphics2D;
import org.apache.batik.svggen.SVGGraphics2DIOException;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;

import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.util.Hashtable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * author: Jacob Schlesinger <schlesinger.jacob@gmail.com>
 * creation date: 9/14/12
 * © Jacob Schlesinger 2012
 */
public class Visualizer {
    public static void visualize(String logFile, String outFile) {
        BufferedReader br;
        try {
            br = new BufferedReader(new FileReader(new File(logFile)));
        } catch (FileNotFoundException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            return;
        }

        Hashtable<String, ThreadBand> threadBands = new Hashtable<String, ThreadBand>();
        Hashtable<String, Task> tasks = new Hashtable<String, Task>();
        final TimeLineRenderer timeLineRenderer = new TimeLineRenderer();

        String patternString = "\\{(?<THREAD>.+)\\}\\[(?<TIMESTAMP>.+)\\](?<TYPE>.+)@(?<ID>.+):(?<STATE>.+)";
        Pattern pattern = Pattern.compile(patternString);
        String line;
        try {
            while ((line = br.readLine()) != null) {
                Matcher m = pattern.matcher(line);
                if (!m.matches())
                    throw new Exception("invalid line format");
                String threadName = m.group("THREAD");
                long time = Long.parseLong(m.group("TIMESTAMP"));
                String type = m.group("TYPE");
                String id = m.group("ID");
                EnRunningStates state = EnRunningStates.valueOf(m.group("STATE"));

                ThreadBand threadBand;
                if (!threadBands.containsKey(threadName)) {
                    threadBands.put(threadName, threadBand = new ThreadBand(timeLineRenderer));
                    threadBand.setThreadName(threadName);
                } else {
                    threadBand = threadBands.get(threadName);
                }

                Task task;
                if (!tasks.containsKey(id)) {
                    tasks.put(id, task = new Task(threadBand, type, id));
                    threadBand.addTask(task);
                } else {
                    task = tasks.get(id);
                }
                switch (state) {
                    case WaitingForDependencies:
                        task.setWaitingForDependenciesTS(time);
                        break;
                    case Running:
                        task.setRunningTS(time);
                        break;
                    case Completed:
                        task.setCompletedTS(time);
                        break;
                }
            }

            boolean flip = true;
            for (ThreadBand band : threadBands.values()) {
                if (flip = !flip)
                    band.setBaseColor(new Color(255, 255, 255));
                else
                    band.setBaseColor(new Color(230, 230, 230));
                timeLineRenderer.addBand(band);
            }

            timeLineRenderer.addBand(new TimeAxisBand(timeLineRenderer));

            timeLineRenderer.setHotizintalScale(.0001);

            /*JFrame frame = new JFrame() {
                @Override
                public void paint(Graphics g) {
                    Visualizer.paint((Graphics2D) g, timeLineRenderer);
                }
            };

            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(1000, 400);
            frame.setVisible(true);*/


            writeSVG(timeLineRenderer, outFile);
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (Exception e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }

    private static void writeSVG(TimeLineRenderer timeLineRenderer, String outFile) throws IOException, SVGGraphics2DIOException {
        // Get a DOMImplementation.
        DOMImplementation domImpl =
                GenericDOMImplementation.getDOMImplementation();

        // Create an instance of org.w3c.dom.Document.
        String svgNS = "http://www.w3.org/2000/svg";
        Document document = domImpl.createDocument(svgNS, "svg", null);

        // Create an instance of the SVG Generator.
        SVGGraphics2D svgGenerator = new SVGGraphics2D(document);

        // Ask the test to render into the SVG Graphics2D implementation.
        //timeLineRenderer.draw(svgGenerator);
        paint(svgGenerator, timeLineRenderer);

        svgGenerator.setSVGCanvasSize(new Dimension(timeLineRenderer.getWidth(), timeLineRenderer.getHeight()));

        // Finally, stream out SVG to the standard output using
        // UTF-8 encoding.
        boolean useCSS = true; // we want to use CSS style attributes
        //Writer out = new OutputStreamWriter(System.out, "UTF-8");
        FileWriter out = new FileWriter(outFile);
        svgGenerator.stream(out, useCSS);
        System.out.println("Stream written");
    }

    public static void main(String[] args) {
        if(args.length!=2){
            System.out.println("provide two files, input log file and output svg file, as first and second parameter");
            System.exit(-1);
        }
        if(!new File(args[0]).exists()){
            System.out.println("file '"+args[0]+"' does not exist");
            System.exit(-1);
        }
        try{
            visualize(args[0], args[1]);
        }catch (Throwable t){
            System.out.println("something went horribly wrong ...");
            t.printStackTrace();
            System.exit(-1);
        }
    }

    private static void paint(Graphics2D g2d, TimeLineRenderer renderer) {
        renderer.setHeight(renderer.getRequiredHeight(g2d));
        renderer.setWidth(renderer.getRequiredWidth(g2d));
        renderer.draw(g2d);
        System.out.println("finished drawing");
    }
}
