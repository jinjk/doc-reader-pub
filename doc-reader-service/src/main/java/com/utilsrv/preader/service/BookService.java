package com.utilsrv.preader.service;

import com.google.common.base.Charsets;
import com.google.gson.Gson;
import com.utilsrv.doc.DocFileComponent;
import com.utilsrv.doc.DocManager;
import com.utilsrv.doc.model.DocFileInfo;
import com.utilsrv.preader.PReaderAppProperties;
import com.utilsrv.preader.exception.ResourceNotFoundException;
import com.utilsrv.preader.jpa.entities.Book;
import com.utilsrv.preader.jpa.entities.Person;
import com.utilsrv.preader.jpa.entities.Status;
import com.utilsrv.preader.jpa.repository.BookPersonRepository;
import com.utilsrv.preader.jpa.repository.BookRepository;
import com.utilsrv.preader.jpa.repository.WordDictRepository;
import com.utilsrv.preader.model.dto.WordDictDTO;
import com.utilsrv.preader.model.dto.BookDTO;
import com.utilsrv.preader.model.dto.BookListDTO;
import com.utilsrv.preader.model.dto.BookTagDTO;
import com.utilsrv.preader.model.dto.UpdateResponseDTO;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.stream.Collectors;

@Service
public class BookService {
    @Autowired
    PReaderAppProperties config;
    @Autowired
    BookRepository bookRepository;
    @Autowired
    BookPersonRepository bookPersonRepository;
    @Autowired
    WordDictRepository wordDictRepository;
    @Autowired
    DocManager docManager;
    @Autowired
    private PlatformTransactionManager transactionManager;

    public Book saveBook(String fileName, MultipartFile file, Person person) {
        DocFileComponent docFileComponent;
        try(InputStream input = file.getInputStream()) {
            docFileComponent = docManager.newDoc(fileName, input);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        DocFileInfo docFileInfo = docFileComponent.getMetaInfo();
        TransactionTemplate transactionTemplate = new TransactionTemplate(transactionManager);
        Book bookRes = transactionTemplate.execute(status -> {
            Book book = bookRepository.findBySha1(docFileInfo.getSha1());
            boolean created = false;
            if (book == null) {
                created = true;
                book = new Book();
            }

            book.setPerson(person);
            book.setName(docFileInfo.getFileName());
            book.setPath(docFileInfo.getDirPath());
            book.setSha1(docFileInfo.getSha1());
            book.setStatus(Status.ACTIVE);
            if (created) {
                book.setCreatedDate(LocalDateTime.now());
            }
            book.setUpdatedDate(LocalDateTime.now());
            bookRepository.save(book);
            return book;
        });

        docManager.launchConvertJob(docFileComponent);

        return bookRes;
    }

    public UpdateResponseDTO saveBookTag(BookTagDTO bookTag) {
        Gson gson = new Gson();
        String str = gson.toJson(bookTag);
        try {
            config.getFile(bookTag.getBookId() + ".json").createNewFile();
            FileUtils.write(config.getFile(bookTag.getBookId() + ".json"), str, StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return new UpdateResponseDTO("done", "");
    }

    public BookTagDTO getTag(String bookId) {
        File file = config.getFile(bookId + ".json");
        if (!file.exists()) {
            throw new ResourceNotFoundException();
        }
        Gson gson = new Gson();
        try (Reader reader = new FileReader(file)) {
            BookTagDTO bookTag = gson.fromJson(reader, BookTagDTO.class);
            return bookTag;
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * End page is an excluded page
     * @param bookId
     * @param begin
     * @param end
     * @param outputStream
     * @throws IOException
     */
    public void consumeBook(Long bookId, int begin, int end, OutputStream outputStream) throws IOException {
        Book book = bookRepository.findById(bookId).orElseThrow(() -> new ResourceNotFoundException());
        if (begin <= 0) {
            DocFileComponent doc = docManager.getDoc(book.getSha1());
            try (InputStream input = doc.getMainPage()) {
                IOUtils.copy(input, outputStream);
            }
        }
        else if (begin >=  1 && begin <= book.getPageCount()) {
            int p = begin;
            int lastPage = begin;
            for (; (p < end && p <= book.getPageCount()); p++) {
                DocFileComponent doc = docManager.getDoc(book.getSha1());
                try (InputStream input = doc.getPage(p)) {
                    IOUtils.copy(input, outputStream);
                }
                lastPage = p;
            }
            String metaData = String.format("<!-- 35util-api-book-meta-info: {\"begin\": %s, \"end\": %s} -->",
                begin, lastPage);
            try(InputStream lastComment = new ByteArrayInputStream(metaData.getBytes(Charsets.UTF_8))) {
                IOUtils.copy(lastComment, outputStream);
            }
        }
        else {
            new ResourceNotFoundException();
        }
    }

    public BookListDTO getBooks(Long personId) {
        var bookPersons = bookPersonRepository.findByPersonId(personId);

        var my = bookPersons.stream().map(it -> {
            BookDTO dto = new BookDTO();
            dto.setId(it.getBook().getId());
            dto.setBookMark(it.getBookMark());
            dto.setRating(it.getRating());
            dto.setPageCount(it.getBook().getPageCount());
            dto.setBookName(it.getBook().getName());
            return dto;
        }).collect(Collectors.toList());

        Pageable page = PageRequest.of(0, 10);
        var recommended = bookRepository.findBooks(page).stream().map(it -> {
            BookDTO dto = new BookDTO();
            dto.setId(it.getId());
            dto.setBookName(it.getName());
            dto.setPageCount(it.getPageCount());
            return dto;
        }).collect(Collectors.toList());

        return new BookListDTO(my, recommended);
    }

    public WordDictDTO getWordDict(String word) {
        var dictEntity = wordDictRepository.findByWord(word);
        return dictEntity
                .map(it -> new WordDictDTO(it.getWord(), it.getPinYin(), it.getContent()))
                .orElseThrow(ResourceNotFoundException::new);
    }
}
