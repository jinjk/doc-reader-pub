package com.utilsrv.preader.service;

import com.utilsrv.doc.BaseDocFileStatusChangeHandler;
import com.utilsrv.doc.model.DocFileInfo;
import com.utilsrv.preader.jpa.entities.Book;
import com.utilsrv.preader.jpa.repository.BookRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;
import java.time.LocalDateTime;

@Component
public class JPADocFileEventHandler extends BaseDocFileStatusChangeHandler {
    @Autowired
    BookRepository repository;

    @Override
    @Transactional(Transactional.TxType.REQUIRES_NEW)
    public void htmlFileReady(DocFileInfo docFileInfo) {
        Book book = repository.findBySha1(docFileInfo.getSha1());
        if (book == null) {
            throw new RuntimeException("Book record not found");
        }
        book.setHasHtml(true);
        book.setUpdatedDate(LocalDateTime.now());
        repository.save(book);
    }

    @Override
    @Transactional(Transactional.TxType.REQUIRES_NEW)
    public void pagesReady(DocFileInfo docFileInfo) {
        Book book = repository.findBySha1(docFileInfo.getSha1());
        book.setHasWordSeg(true);
        book.setUpdatedDate(LocalDateTime.now());
        book.setPageCount(docFileInfo.getPageCount());
        repository.save(book);
    }
}
