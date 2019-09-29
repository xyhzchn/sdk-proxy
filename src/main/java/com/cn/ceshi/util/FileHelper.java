package com.cn.ceshi.util;

import java.io.*;

public class FileHelper {
    public static Object readFromFile(String path) {
        FileInputStream fis = null;
        ObjectInputStream ois = null;
        try {
            fis = new FileInputStream(path);
            ois = new ObjectInputStream(fis);
            return ois.readObject();
        } catch (Throwable t) {
            t.printStackTrace();
        } finally {
            closeIO(ois);
            closeIO(fis);
        }
        return null;
    }

    public static boolean saveToFile(String path, Object obj) {
        FileOutputStream fos = null;
        ObjectOutputStream oos = null;
        try {
            fos = new FileOutputStream(path);
            oos = new ObjectOutputStream(fos);
            oos.writeObject(obj);
            return true;
        } catch (Throwable t) {
            t.printStackTrace();
        } finally {
            closeIO(oos);
            closeIO(fos);
        }
        return false;
    }

    private static void closeIO(Closeable io) {
        if (io != null) {
            try {
                io.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
