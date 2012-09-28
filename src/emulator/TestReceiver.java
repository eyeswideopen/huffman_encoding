package emulator;//package test;

import encoder.configuration.DefaultConfiguration;
import encoder.io.FileReceiver;
import encoder.io.FileSender;
import encoder.processing.Processor;
import encoder.processing.interfaces.IHuffmanProcessor;
import helper.StopWatch;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.ArrayList;

public class TestReceiver {

    private static ServerSocket serverSocket;
    private static Socket outSocket;
    private static Socket getDataSocket;
    private static FileOutputStream fileWriter;
    private static ArrayList<Long> endTimes = new ArrayList<Long>();
    private static String basename = "file_", basedir = "";
    private static String resultHost = "127.0.0.1";
    private static int resultPort = -1;
    private static ArrayList<String> fileList = new ArrayList<String>();
    public static boolean dbg = false;  // switch debugging on/off
    private static String _seperator = java.io.File.separator;


    public static void main(String argv[]) {
        if (argv.length < 4) {
            System.out.println("Usage: receivePort outputDir resultHost resultPort");
            System.exit(1);
        }
        try {

            // establish server on given port number
            int port = Integer.valueOf(argv[0]);
            basedir = argv[1];
            System.out.println("waiting on port " + port + ", send results to " + argv[2] + ":" + argv[3]);
            resultHost = argv[2];
            resultPort = Integer.valueOf(argv[3]);
            serverSocket = new ServerSocket(port);

            // ############# RECEIVING DATA PART  #########
            // init connection to sending part of emulator

            //wait for the connection to be established
            getDataSocket = serverSocket.accept();
            System.out.println("Client connected");
            InputStream is = getDataSocket.getInputStream();

            DataInputStream ois = new DataInputStream(is);
            if (dbg) System.out.println("input streams established");

            while (true) {
                FilePacket fp = new FilePacket();
                fp.size = ois.readInt();  // size
                if (fp.size == FilePacket.FINISH) break;
                if (dbg) System.out.println("next size " + fp.size);
                fp.count = ois.readInt(); // count
                if (dbg) System.out.println("next count " + fp.count);
                fp.content = new byte[fp.size];
                ois.readFully(fp.content);
                if (dbg) System.out.println("received item " + fp.count + ", size=" + fp.content.length);

                // save data to file
                String fname = basename + fp.count + ".dat";
                if (dbg) System.out.println("wrote data to " + fname);
                fileWriter = new FileOutputStream(basedir + _seperator + "recievedData" + _seperator + fname);
                fileWriter.write(fp.content);
                fileWriter.close();
                fileList.add(fname);  // store local name for further encoder.processing
            } // while
            // close streams
            ois.close();
            is.close();
            getDataSocket.close();
            serverSocket.close();

            // ######### PROCESSING PART #######

            for (String fileName : fileList) {
                // TODO: get proper config object
                IHuffmanProcessor pc = new Processor(new DefaultConfiguration());
                ((Processor) pc).setReceiver(new FileReceiver(fileName));
                ((Processor) pc).setSender(new FileSender(fileName));
                pc.process();
            }

            StopWatch.printTable();
            // re-read data from local files
            ArrayList<FilePacket> dataList = new ArrayList<FilePacket>(); // empty list for result data
            // fill data list reading local files based on local names


            FilePacket.readDir(basedir + _seperator + "processedData", dataList, fileList);

            // ######### SEND DATA BACK ########
            sendBack(dataList);

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private static void sendBack(ArrayList<FilePacket> dataList) {
        try {
            System.out.println("Sending back " + dataList.size() + " files");

            //connect to receiving part of emulator
            outSocket = new Socket();
            SocketAddress remoteBindPoint = new InetSocketAddress(resultHost, resultPort);
            System.out.println("Try to connect to " + resultHost + ":" + resultPort);

            outSocket.connect(remoteBindPoint);
            OutputStream os = outSocket.getOutputStream();
            DataOutputStream oos = new DataOutputStream(os);

            System.out.println("Result connection established!");
            for (int i = 0; i < dataList.size(); i++) {
                FilePacket fp = dataList.get(i);
                oos.writeInt(fp.size);
                oos.writeInt(i);
                oos.write(fp.content);
                System.out.println("Sent back item " + i + " size " + fp.content.length);
            }
            oos.writeInt(FilePacket.FINISH);
            // close streams
            oos.close();
            os.close();
            outSocket.close();

            System.out.println("Finished encoder.processing!");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
