package com.utilsrv.doc.batch.convert;

import com.utilsrv.doc.BaseDocFileStatusChangeHandler;
import com.utilsrv.doc.GlobalVarsCache;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;

@Component
public class DocPageBuilder {
    @Autowired
    BaseDocFileStatusChangeHandler statusChangeHandler;

    public PDF2HtmlTaskDef buildPages(String sha1) {
        PDF2HtmlTaskDef taskDef = GlobalVarsCache.getTaskDef(sha1);
        if (taskDef != null) {
            File htmlFile = taskDef.getHtmlFile();
            try {
                int pageCount = HtmlDocParserHandler.parseXml(htmlFile);
                taskDef.getDocFileInfo().setPageCount(pageCount);
                statusChangeHandler.pagesReady(taskDef.getDocFileInfo());
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        return taskDef;
    }
}
