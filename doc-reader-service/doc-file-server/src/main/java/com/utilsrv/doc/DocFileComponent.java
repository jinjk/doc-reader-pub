package com.utilsrv.doc;

import com.google.common.io.Files;
import com.google.gson.Gson;
import com.utilsrv.doc.model.DocFileInfo;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.collections4.map.LRUMap;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.time.LocalDateTime;

public class DocFileComponent {
    private String componentDir;
    private DocFileInfo metaInfo;

    public DocFileComponent(String sha1) {
        this.componentDir = DocManager.dirPath(sha1);
        File file = FileUtils.getFile(componentDir, DocConstants.META_FILE_NAME);
        if (file.exists()) {
            Gson gson = new Gson();
            try {
                metaInfo = gson.fromJson(FileUtils.readFileToString(file, StandardCharsets.UTF_8),
                        DocFileInfo.class);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public DocFileInfo getMetaInfo() {
        if (metaInfo != null) {
            return metaInfo;
        }
        else {
            File file = FileUtils.getFile(componentDir, DocConstants.META_FILE_NAME);
            if (file.exists()) {
                Gson gson = new Gson();
                try {
                    DocFileInfo metaInfo = gson.fromJson(FileUtils.readFileToString(file, StandardCharsets.UTF_8),
                            DocFileInfo.class);
                    return metaInfo;
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
            return null;
        }
    }

    public static DocFileComponent save(String fileName, InputStream inputStream) throws IOException {
        File tempFile = File.createTempFile("book", fileName);
        String sha1;
        try {
            try (OutputStream output = new FileOutputStream(tempFile)) {
                IOUtils.copy(inputStream, output);
                output.flush();
            }
            try (InputStream fileInputStream = new FileInputStream(tempFile)) {
                sha1 = DigestUtils.sha1Hex(fileInputStream);
            }
            String absPath = DocManager.dirPath(sha1);
            File directories = new File(absPath);
            if (!directories.exists()) {
                directories.mkdirs();
            }
            FileUtils.copyFile(tempFile, new File(directories, DocConstants.PDF_FILE_NAME));
            DocFileInfo metaInfo = new DocFileInfo();
            metaInfo.setFileName(fileName);
            metaInfo.setFileType(Files.getFileExtension(fileName));
            metaInfo.setCreated(LocalDateTime.now());
            metaInfo.setDirPath(absPath);
            metaInfo.setSha1(sha1);
            Gson gson = new Gson();
            try (FileWriter writer = new FileWriter(new File(directories, DocConstants.META_FILE_NAME))) {
                gson.toJson(metaInfo, writer);
            }
        }
        finally {
            tempFile.delete();
        }
        return new DocFileComponent(sha1);
    }

    public InputStream getMainPage() throws IOException {
        File mainHtmlFile = FileUtils.getFile(this.componentDir, DocConstants.HTML_MAIN_PAGE);
        return FileUtils.openInputStream(mainHtmlFile);
    }

    public InputStream getPage(int page) throws IOException {
        Path path = Path.of(this.componentDir,
                DocConstants.PAGE_DIR,
                String.format("page_%s.html", page - 1));

        return readFromCache(path);
    }

    private static LRUMap<String, byte[]> pageCache = new LRUMap<>(20);
    private InputStream readFromCache(Path path) throws IOException {
        String pathStr = path.toString();
        if (pageCache.containsKey(pathStr)) {
            return new ByteArrayInputStream(pageCache.get(pathStr));
        }
        else {
            byte[] bytes = FileUtils.readFileToByteArray(path.toFile());
            pageCache.put(pathStr, bytes);
            return new ByteArrayInputStream(bytes);
        }
    }
}
