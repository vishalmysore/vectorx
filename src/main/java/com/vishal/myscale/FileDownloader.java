package com.vishal.myscale;

import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import java.io.BufferedInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;

@Log
@Configuration
public class FileDownloader {
    @Value("${data.url}")
    private String fileURL;

    public void downloadFile()  {
        String saveDir = System.getProperty("user.dir"); ; // Replace with your directory
        String fileName = "train.csv";
        URL url = null;
        try {
            url = new URL(fileURL);
        } catch (MalformedURLException e) {
            log.severe(e.getMessage());

        }
        try (BufferedInputStream in = new BufferedInputStream(url.openStream());
             FileOutputStream fileOutputStream = new FileOutputStream(Paths.get(saveDir, fileName).toString())) {

            byte dataBuffer[] = new byte[1024];
            int bytesRead;
            while ((bytesRead = in.read(dataBuffer, 0, 1024)) != -1) {
                fileOutputStream.write(dataBuffer, 0, bytesRead);
            }
        } catch (FileNotFoundException e) {
            log.severe(e.getMessage());
        } catch (IOException e) {
            log.severe(e.getMessage());
        }
    }
}

