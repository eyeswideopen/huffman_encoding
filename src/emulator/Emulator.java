package emulator;

import java.net.DatagramSocket;
import java.net.Socket;
import java.util.ArrayList;


public class Emulator {
    public static String host = "127.0.0.1", resultdir = null;
    public static int port = 0;
    public static String inputDir = ".";
    public static int listenToPort = 0;
    public static DatagramSocket socket = null;
    public static Socket tcpSocket = null;
    private static boolean dbg = false;  // switch debugguing mode off / on

    // dataList contains information about every transferred file
    public static ArrayList<FilePacket> dataList = new ArrayList<FilePacket>();

    public static void main(String argv[]) {
        try {
            //check params
            if (argv.length < 6) printHelp();

            //read parameters
            inputDir = argv[0];
            host = argv[1];
            port = Integer.valueOf(argv[2]);
            int interval = Integer.valueOf(argv[3]);
            listenToPort = Integer.valueOf(argv[4]);
            String resultfile = argv[5];
            if (argv.length > 6) {
                if (argv[6].charAt(0) == 'd') dbg = true;
                if (argv[6].charAt(0) == 'w') resultdir = argv[7];
            }
            // start receiver and sender thread
            ResultReceiverAsync receiver = new ResultReceiverAsync(listenToPort, resultfile, dataList, dbg, resultdir);
            DataSenderAsync sender = new DataSenderAsync(inputDir, host, port, interval, dataList, dbg);

            sender.join();
            receiver.join();
            receiver.cleanAndStop();

            //calculate avarage encoder.processing time
            System.out.println("Processed packages: " + dataList.size());
            int packProcessed = dataList.size();
            long totalTime = 0;
            long maxTime = 0;
            for (int i = 0; i < packProcessed; i++) {
                long time = dataList.get(i).stopTime - dataList.get(i).startTime;
                totalTime += time;
                if (time > maxTime) maxTime = time;
                System.out.println("Time " + (i + 1) + ": " + time + " ms");
            }

            if (packProcessed > 0)
                System.out.println("Avg. encoder.processing time: " + (totalTime / packProcessed) + " ms");
            System.out.println("Max. encoder.processing time: " + maxTime + " ms");
            System.exit(0);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public static void printHelp() {
        System.err.println("usage: inputDir host port interval listenPort resultfile [d | w <dir>]");
        System.err.println("\t d .. switch debug mode on");
        System.err.println("\t w .. write processed files into folder <dir> \n\t\tafter end of all receiving operations");
        System.exit(1);
    }

}
