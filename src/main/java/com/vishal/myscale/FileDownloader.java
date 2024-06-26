package com.vishal.myscale;

import java.io.BufferedInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;

public class FileDownloader {
    public static void main(String[] args) {
        String fileURL = "https://huggingface.co/datasets/VishalMysore/newIndianCuisine/raw/main/train.csv";
        String saveDir = System.getProperty("user.dir"); ; // Replace with your directory
        String fileName = "train.csv";

        try {
            downloadFile(fileURL, saveDir, fileName);
            System.out.println("File downloaded successfully!");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void downloadFile(String fileURL, String saveDir, String fileName) throws IOException {
        URL url = new URL(fileURL);
        try (BufferedInputStream in = new BufferedInputStream(url.openStream());
             FileOutputStream fileOutputStream = new FileOutputStream(Paths.get(saveDir, fileName).toString())) {

            byte dataBuffer[] = new byte[1024];
            int bytesRead;
            while ((bytesRead = in.read(dataBuffer, 0, 1024)) != -1) {
                fileOutputStream.write(dataBuffer, 0, bytesRead);
            }
        }
    }
}

