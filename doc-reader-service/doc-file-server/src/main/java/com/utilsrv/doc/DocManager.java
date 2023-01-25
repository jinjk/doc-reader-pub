package com.utilsrv.doc;

import com.utilsrv.doc.batch.convert.PDF2HtmlTaskDef;
import com.utilsrv.doc.model.DocFileInfo;
import org.apache.commons.collections4.map.LRUMap;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;

@Component
public class DocManager {
    @Autowired
    DocConfig docConfig;
    @Autowired
    Job convertDocumentJob;
    @Autowired
    JobLauncher jobLauncher;
    private static LRUMap<String, String> pathCache = new LRUMap(250);

    private static DocManager instance;

    public static DocManager getInstance() {
        return instance;
    }

    @PostConstruct
    private void init() {
        DocManager.instance = this;
    }

    public void launchConvertJob(PDF2HtmlTaskDef task) {
        try {
            GlobalVarsCache.add(task.getDocFileInfo().getSha1(), task);
            JobParameters jobParameters = new JobParametersBuilder().addString(DocConstants.JOB_PARAM_SHA1,
                    task.getDocFileInfo().getSha1())
                    .addDate(DocConstants.JOB_PARAM_START_DATE, new Date())
                    .toJobParameters();
            jobLauncher.run(convertDocumentJob, jobParameters);
        } catch (JobExecutionAlreadyRunningException e) {
            throw new RuntimeException(e);
        } catch (JobRestartException e) {
            throw new RuntimeException(e);
        } catch (JobInstanceAlreadyCompleteException e) {
            throw new RuntimeException(e);
        } catch (JobParametersInvalidException e) {
            throw new RuntimeException(e);
        }
    }

    public DocFileComponent newDoc(String fileName, InputStream inputStream) throws IOException {
        DocFileComponent doc = DocFileComponent.save(fileName, inputStream);
        return doc;
    }

    public void launchConvertJob(DocFileComponent doc) {
        PDF2HtmlTaskDef task = new PDF2HtmlTaskDef(doc.getMetaInfo());
        launchConvertJob(task);
    }

    public static String dirPath(String sha1) {
        String res = pathCache.get(sha1);
        if (res == null) {
            String dirPath = String.format("%s/%s/%s/%s",
                    instance.docConfig.getRootDir(), sha1.substring(0, 2), sha1.substring(2, 4), sha1.substring(4));
            pathCache.put(sha1, dirPath);
            res = dirPath;
        }
        return res;
    }

    public static DocFileInfo docFileInfo(String sha1) {
        return DocFileInfo.load(dirPath(sha1));
    }

    @Cacheable(cacheNames = "doc-service-doc", key = "#sha1")
    public DocFileComponent getDoc(String sha1) {
        return new DocFileComponent(sha1);
    }
}
