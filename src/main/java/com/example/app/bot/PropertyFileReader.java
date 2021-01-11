package com.example.app.bot;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class PropertyFileReader {

    public String[] getPropValues() throws IOException {
        FileInputStream fileInputStream;
        Properties properties = new Properties();
        String[] configList = new String[3];
        try{
            fileInputStream = new FileInputStream("src/main/resources/configuration.properties");
            properties.load(fileInputStream);

            String apiKey = properties.getProperty("translator.apiKey");
            String url = properties.getProperty("translator.url");
            String versionDate = properties.getProperty("translator.versionDate");

            configList[0] = apiKey;
            configList[1] =  versionDate;
            configList[2] = url;
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        return configList;
    }
}
