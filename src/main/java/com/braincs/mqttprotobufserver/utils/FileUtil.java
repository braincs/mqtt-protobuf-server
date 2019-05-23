package com.braincs.mqttprotobufserver.utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class FileUtil {
    public static String save(String path, String name, byte[] data) {
        File parent = new File(path);
        if (!parent.exists() ){//&& parent.isDirectory()
            parent.mkdirs();
        }
        FileOutputStream fos = null;
        File dstFile = new File(path, name);
        try{
            if (!dstFile.exists()){
                dstFile.createNewFile();
            }
            fos = new FileOutputStream(dstFile);
            fos.write(data);
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (fos != null) {
                    fos.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return dstFile.getAbsolutePath();
    }
}
