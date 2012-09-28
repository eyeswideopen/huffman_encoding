package emulator;


import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class ResultReceiverAsync extends Thread {
    private static int BUFSIZE = 1024;
    private ServerSocket serverSocket;
    private Socket resultSocket;
    private static String outdir = null;
    BufferedWriter fileWriter;
    private static ArrayList<Long> endTimes = new ArrayList<Long>();
    private static ArrayList<FilePacket> dataList = null;
    private static ArrayList<FilePacket> newDataList = new ArrayList<FilePacket>();
    private boolean isActive = true;
    private static boolean dbg = true;

    public ResultReceiverAsync(int port, String f, ArrayList<FilePacket> d, boolean _dbg, String _outdir) {
        dbg = _dbg;
        dataList = d;
        outdir = _outdir;
        try {
            serverSocket = new ServerSocket(port);
            if (dbg) System.out.println("Result receiver listening on port " + port);
            fileWriter = new BufferedWriter(new FileWriter(f));
            if (dbg) System.out.println("Writing results into file " + f);
            start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void run() {
        try {
            resultSocket = serverSocket.accept();
            if (dbg) System.out.println("Client connected!");
            InputStream is = resultSocket.getInputStream();
            DataInputStream ois = new DataInputStream(is);

            int count, lcounter = 0, size;
            long diff = 0;
            long lastTime = System.currentTimeMillis();

            while (true) {

                // receive next block
                size = ois.readInt();    // size
                if (size == FilePacket.FINISH) break; // no more files

                count = ois.readInt();    // counter
                byte[] buf = new byte[size];
                ois.readFully(buf);           // data
                FilePacket newPacket = new FilePacket();
                newPacket.size = size;
                newPacket.count = count;
                newPacket.content = buf;
                newDataList.add(newPacket);
                lastTime = System.currentTimeMillis();
                dataList.get(count).stopTime = lastTime;
                diff = lastTime - dataList.get(count).startTime;
                if (lcounter != dataList.get(lcounter).count)
                    System.out.println("Counter of " + lcounter + ". conflicts with received " + dataList.get(lcounter).count);
                if (buf.length != dataList.get(lcounter).size)
                    System.out.println("Length of " + lcounter + ". received file differs from" + dataList.get(lcounter).size);
                lcounter++;
                //write time to file
                fileWriter.write("File: " + count + ", size:" + size + ", " + diff + " ms\n");
                fileWriter.flush();
                if (!isActive) break;
            }
            fileWriter.close();
            ois.close();
            is.close();
            resultSocket.close();
            serverSocket.close();
            if (outdir != null) {
                FileOutputStream fs = null;
                for (int i = 0; i < newDataList.size(); i++) {
                    fs = new FileOutputStream(outdir + "/result_" + i + ".dat");
                    fs.write(newDataList.get(i).content);
                    fs.close();
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void cleanAndStop() {
        isActive = false;
    }
}
