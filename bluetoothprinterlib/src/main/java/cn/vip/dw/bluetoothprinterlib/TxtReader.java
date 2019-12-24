package cn.vip.dw.bluetoothprinterlib;

import android.content.Context;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;

public class TxtReader {

    /**
     * 读取文件内容 /assets/XXX/*
     *
     * @param context
     * @param fileName /assets/XXX/*
     * @return
     */
    public static String getStringFromAssetsByFullPath(Context context, String fileName) {
        try {

            InputStreamReader inputReader = new InputStreamReader(context.getClass().getResourceAsStream(fileName));

            BufferedReader bufReader = new BufferedReader(inputReader);

            String line;

            String Result = "";

            while ((line = bufReader.readLine()) != null) {
                Result += (line + "\n");
            }

            return Result;

        } catch (Exception e) {

            e.printStackTrace();

        }
        return null;
    }

    /**
     * 通过一个InputStream获取内容
     *
     * @param inputStream
     * @return
     */
    public static String getStringFromRaw(InputStream inputStream) {
        InputStreamReader inputStreamReader = null;
        try {
            inputStreamReader = new InputStreamReader(inputStream, "gbk");
        } catch (UnsupportedEncodingException e1) {
            e1.printStackTrace();
        }
        BufferedReader reader = new BufferedReader(inputStreamReader);
        StringBuffer sb = new StringBuffer("");
        String line;
        try {
            while ((line = reader.readLine()) != null) {
                sb.append(line);
                sb.append("\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return sb.toString();
    }

    /**
     * 通过txt文件的路径获取其内容
     *
     * @param filepath
     * @return
     */
    public static String getStringFromFile(String filepath) {
        File file = new File(filepath);
        FileInputStream fileInputStream = null;
        try {
            fileInputStream = new FileInputStream(file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return getStringFromRaw(fileInputStream);
    }
}
