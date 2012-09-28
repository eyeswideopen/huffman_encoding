package emulator;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;

public class FilePacket {
    public static final int FINISH = -1;

    int size = -1;
    int count = -1;
    long startTime = -1;
    long stopTime = -1;
    byte[] content = null;

    public static void readDir(String dirname,
                               ArrayList<FilePacket> data,
                               ArrayList<String> fileList) {
        System.out.println("Start encoder.processing directory " + dirname + ".");

        try {
            if (fileList.size() == 0) {  // no file list given
                // read all files from the given directory path
                File dir = new File(dirname);
                System.out.println("Absolute File Directory: " + dir.getAbsolutePath());
                File[] files = dir.listFiles();
                for (int i = 0; i < files.length; i++) {
                    // readFile(files[i], data);
                    fileList.add(files[i].getName());
                }
            }
            // get raw data into dataList
            for (int i = 0; i < fileList.size(); i++) {
                System.out.println("READ " + fileList.get(i) + ".");

                readFile(new File(dirname + "/" + fileList.get(i)), data);
                data.get(i).count = i;
            }
            System.out.println("processed <" + dirname + ">");
        } catch (Exception ex) {
            System.err.println("Exception: " + ex.getMessage());
        }
    }

    private static void readFile(File file, ArrayList<FilePacket> data) {
        // System.out.println("Start reading " + file.getName() + ".");

        FilePacket fp = new FilePacket();
        fp.size = (int) file.length();
        try {
            BufferedInputStream bin = new BufferedInputStream(new FileInputStream(file));
            fp.content = new byte[(int) fp.size];
            bin.read(fp.content);
            //close streams
            bin.close();
            data.add(fp);  // put the new file package into list
            System.out.println("got <" + file.getName() + ">");
        } catch (Exception ex) {
            System.err.println("Exception: " + ex.getMessage());
        }
    }
}
