package com.utilsrv.doc.batch.convert;

import com.utilsrv.doc.BaseDocFileStatusChangeHandler;
import com.utilsrv.doc.GlobalVarsCache;
import com.utilsrv.doc.model.DocFileInfo;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.fit.pdfdom.PDFDomTree;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;

@Component
public class PDF2HmlConverter implements ItemProcessor<PDF2HtmlTaskDef, PDF2HtmlTaskDef> {
    Logger logger = LoggerFactory.getLogger(this.getClass());
    @Autowired
    BaseDocFileStatusChangeHandler persistenceHandler;

    @Override
    public PDF2HtmlTaskDef process(PDF2HtmlTaskDef t) throws Exception {
        return convertFileToHtml(t);
    }

    public PDF2HtmlTaskDef convertFileToHtml(PDF2HtmlTaskDef t) {
        try(var output = new PrintWriter(t.getHtmlFile(), StandardCharsets.UTF_8)) {
            PDDocument pdf = PDDocument.load(t.getPdfFile());
            (new PDFDomTree()).writeText(pdf, output);
            DocFileInfo docFileInfo = t.getDocFileInfo();
            docFileInfo.setHasHtmlFile(true);
            docFileInfo.save();
            persistenceHandler.htmlFileReady(docFileInfo);
            GlobalVarsCache.add(t.getDocFileInfo().getSha1(), t);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            t.setStatus(PDF2HtmlTaskDef.Status.FAIL);
            throw new RuntimeException(e);
        }
        t.setStatus(PDF2HtmlTaskDef.Status.DONE);
        return t;
    }
}
