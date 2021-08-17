package com.qthegamep.diploma.project.back;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Paths;
import java.util.Objects;
import java.util.Properties;

public class Config {
    private static final Logger LOG = LoggerFactory.getLogger(Config.class);

    private Config() {
    }

    public static void init() throws IOException, URISyntaxException {
        String propertiesFilePath = System.getProperty("config.properties");
        if (propertiesFilePath == null || propertiesFilePath.isEmpty()){
            URL res = Config.class.getResource("/config.properties");
            File file = Paths.get(Objects.requireNonNull(res).toURI()).toFile();
            propertiesFilePath = file.getAbsolutePath();
        }
        LOG.info("Loading properties... {}", propertiesFilePath);
        try (FileInputStream is = new FileInputStream(propertiesFilePath)) {
            Properties properties = new Properties();
            properties.load(is);
            properties.stringPropertyNames()
                    .forEach(key -> {
                        String value = properties.getProperty(key);
                        System.setProperty(key, value);
                        LOG.debug("Properties: {} : {}", key, value);
                    });
        } catch (Exception e) {
            LOG.error("Can't load config.properties. Exception: ", e);
        }
    }
}

