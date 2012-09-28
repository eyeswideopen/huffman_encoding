package emulator;

import java.io.DataOutputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.ArrayList;

public class DataSenderAsync extends Thread {
    private static int sent = 0;
    private static int processed = 0;
    private ArrayList<FilePacket> dataList = null;
    private ArrayList<String> fileList = new ArrayList<String>();
    private ArrayList<Long> startTimes = new ArrayList<Long>();
    private Socket tcpSocket;
    private String host;
    private Integer port;
    private int interval;
    private static boolean dbg = false;  // switch debugging on/off
    private static int waitToEnd = 30000;

    public DataSenderAsync(String inputDir, String host,
                           int port, int interval,
                           ArrayList<FilePacket> d,
                           boolean _dbg) {
        dataList = d;
        dbg = _dbg;
        try {
            this.host = host;
            this.port = port;
            this.interval = interval;

            FilePacket.readDir(inputDir, dataList, fileList);
            start();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void run() {
        try {
            //create socket
            tcpSocket = new Socket();

            // Create remote bind-point
            System.out.println("Connecting to " + host + ":" + port + ". Interval: " + interval);
            SocketAddress remoteBindPoint = new InetSocketAddress(host, port);
            tcpSocket.connect(remoteBindPoint);

            //wait for the connection to be established
            Thread.sleep(2000);

            OutputStream os = tcpSocket.getOutputStream();
            DataOutputStream oos = new DataOutputStream(os);
            if (dbg) System.out.println("OutputStream established");

            System.out.println("Sending...");

            //few performance counters
            long delaySum = 0;
            long time = 0, lastPackTime = 0;

            for (int i = 0; i < dataList.size(); i++) {
                if (dbg) System.out.println("try to send file " + i + " => " + fileList.get(i));

                FilePacket pack = dataList.get(i);
                // write data
                oos.writeInt(pack.size);  // write file size
                if (dbg) System.out.println("sent size of " + i);
                oos.writeInt(i);       // write counter
                pack.count = i;
                if (dbg) System.out.println("sent count => " + i);
                oos.write(pack.content);  // write all bytes of the file
                if (dbg) System.out.println("sent data of " + i + " => " + pack.content.length);

                time = System.currentTimeMillis();
                pack.startTime = time;

                //measure real delay between blocks
                if (lastPackTime != 0) {   //ignore first packet
                    delaySum += time - lastPackTime;
                }
                lastPackTime = time;

                System.out.println("Finished " + i);
                //System.out.println("Start time " + sent + ": " + time);
                Thread.sleep(interval);
            }
            // send last message to finish connection
            oos.writeInt(FilePacket.FINISH);  // write file size
            oos.close();
            os.close();
            tcpSocket.close();
            System.out.println("Transfered " + dataList.size() + " files");

            if (sent > 1) System.out.println("Average delay: " + (delaySum / (sent - 1)));
//      System.out.println("Waiting for results ...");
//      Thread.sleep(waitToEnd);

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }


}
