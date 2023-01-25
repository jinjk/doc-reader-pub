package com.utilsrv.doc.batch.convert;

import com.utilsrv.doc.DocConstants;
import com.utilsrv.doc.model.DocFileInfo;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.File;

@Getter @Setter @NoArgsConstructor
public class PDF2HtmlTaskDef {
    public enum Status {INIT, WORKING, DONE, FAIL}
    private File pdfFile;
    private File htmlFile;
    private Status status = Status.INIT;
    private DocFileInfo docFileInfo;
    private File pagesDir;

    public PDF2HtmlTaskDef(DocFileInfo docFileInfo) {
        this.pdfFile = new File(docFileInfo.getDirPath(), DocConstants.PDF_FILE_NAME);
        this.htmlFile = new File(docFileInfo.getDirPath(), DocConstants.HTML_FILE_NAME);
        this.pagesDir = new File(docFileInfo.getDirPath(), DocConstants.PAGE_DIR);
        this.docFileInfo = docFileInfo;
    }
}
