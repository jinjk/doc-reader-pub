package com.utilsrv.doc.batch.convert;

import com.utilsrv.doc.BaseDocFileStatusChangeHandler;
import com.utilsrv.doc.GlobalVarsCache;
import com.utilsrv.doc.model.PageCharsInfo;
import com.utilsrv.doc.model.SegResp;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.io.*;
import java.util.*;

@Component
public class WordSegBuilder {
    @Autowired
    RestTemplate template = null;
    @Autowired
    BaseDocFileStatusChangeHandler statusChangeHandler;
    Logger logger = LoggerFactory.getLogger(WordSegBuilder.class);

    public PDF2HtmlTaskDef buildSegments(String sha1) {
        PDF2HtmlTaskDef taskDef = GlobalVarsCache.getTaskDef(sha1);
        if (taskDef != null) {
            File dir = taskDef.getPagesDir();
            if (dir.exists()) {
                Iterator<File> files = FileUtils.iterateFiles(dir, new String[]{"html"}, false);
                Map<File, File> fileMap = new HashMap<>();
                while(files.hasNext()) {
                    File f = files.next();
                    File tmp = new File(f.getParent(), f.getName() + "_tmp");
                    if (!tmp.exists()) {
                        try {
                            tmp.createNewFile();
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    }
                    PageCharsInfo pageCharsInfo = new PageCharsInfo();
                    String gidPrefix = FilenameUtils.getBaseName(f.getName());
                    try (FileReader reader = new FileReader(f);
                         BufferedReader bufferedReader = new BufferedReader(reader);
                         FileWriter writer = new FileWriter(tmp)) {
                        String line;
                        int gidLevel1 = 0;
                        while((line = bufferedReader.readLine()) != null) {
                            line = line.trim();
                            if (!line.startsWith("<img")) {
                                pageCharsInfo.addLine(line);
                            }
                            if (line.startsWith("<img")) {
                                segAndWrite(pageCharsInfo, writer, gidPrefix);
                                pageCharsInfo = new PageCharsInfo();
                                gidPrefix = gidPrefix + (gidLevel1++);
                                writer.write(line);
                            }
                        }
                        segAndWrite(pageCharsInfo, writer, gidPrefix);
                    }
                    catch (IOException ex) {
                        throw new RuntimeException(ex);
                    }// end tmp write file
                    fileMap.put(f, tmp);
                }

                fileMap.keySet().forEach(f -> {
                    File tmp = fileMap.get(f);
                    try {
                        FileUtils.copyFile(tmp, f);
                        FileUtils.delete(tmp);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                });
                statusChangeHandler.segReady(taskDef.getDocFileInfo());
            }
        }
        return taskDef;
    }

    private void segAndWrite(PageCharsInfo pageCharsInfo, FileWriter writer, String pageGid) throws IOException {
        List<String> hanStrings = pageCharsInfo.hanStrings();
        Map<String, List<String>> reqBody = new HashMap<>();
        reqBody.put("lines", hanStrings);
        SegResp resp = template.postForEntity("http://localhost:5001", reqBody, SegResp.class).getBody();

        List<int[]> wordSegs = new ArrayList<>();
        for (int i = 0; i < resp.getRes().size(); i++) {
            int[] words = resp.getRes().get(i).stream().mapToInt(w -> w.length()).toArray();
            wordSegs.add(words);
        }
        pageCharsInfo.setWordSegsAndBuildGroups(wordSegs);

        writer.write(pageCharsInfo.toFormatted(pageGid));
    }
}
