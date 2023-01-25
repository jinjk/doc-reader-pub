package com.utilsrv.doc.model;

import com.google.gson.Gson;
import com.utilsrv.doc.DocConstants;
import lombok.Getter;
import lombok.Setter;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;

@Getter @Setter
public class DocFileInfo {
    private String fileName;
    private String fileType;
    private String sha1;
    private String dirPath;
    private boolean hasHtmlFile = false;
    private LocalDateTime created;
    private ProcessStatus processStatus;
    private int pageCount;

    public DocFileInfo() {}

    public static DocFileInfo load(String dirPath) {
        Gson gson = new Gson();
        try (FileReader reader = new FileReader(new File(dirPath, DocConstants.META_FILE_NAME))) {
            DocFileInfo info = gson.fromJson(reader, DocFileInfo.class);
            return info;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void save() {
        Gson gson = new Gson();
        try (FileWriter writer = new FileWriter(new File(dirPath, DocConstants.META_FILE_NAME))) {
            gson.toJson(this, writer);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
