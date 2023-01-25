package com.utilsrv.preader;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.io.File;
import java.nio.file.Paths;

@Configuration
@ConfigurationProperties
public class PReaderAppProperties {
    @Value("${HOME}")
    private String homeDir;
    public File getWorkDir() {
        File file = new File(homeDir + "/pupil_reader");
        if (!file.exists()) {
            file.mkdir();
        }
        return file;
    }

    public File getFile(String name) {
        File file = Paths.get(getWorkDir().getAbsolutePath(), name).toFile();
        return file;
    }
}
