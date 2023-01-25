package com.utilsrv.preader.controller;

import com.utilsrv.preader.exception.ResourceNotFoundException;
import com.utilsrv.preader.jpa.entities.Person;
import com.utilsrv.preader.model.dto.BookListDTO;
import com.utilsrv.preader.model.dto.BookTagDTO;
import com.utilsrv.preader.model.dto.UpdateResponseDTO;
import com.utilsrv.preader.model.dto.WordDictDTO;
import com.utilsrv.preader.service.AuthService;
import com.utilsrv.preader.service.BookService;
import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;
import net.sourceforge.pinyin4j.format.HanyuPinyinVCharType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.file.AccessDeniedException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@RestController
public class BookController {
    Logger logger = LoggerFactory.getLogger(this.getClass());
    @Autowired
    protected BookService bookService;
    @Autowired
    protected AuthService authService;

    @RequestMapping(value = "/", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public String home() {
        return "{\"msg\": \"home page\"}";
    }

    @RequestMapping(value = "/books/{name}/tag", method= RequestMethod.POST)
    public UpdateResponseDTO saveTag(@PathVariable("name") String bookName, @RequestBody BookTagDTO bookTag) {
        bookTag.setBookId(bookName);
        return bookService.saveBookTag(bookTag);
    }

    @RequestMapping(value = "/books/{name}/tag", method= RequestMethod.GET)
    public BookTagDTO getTag(@PathVariable("name") String bookName) {
        return bookService.getTag(bookName);
    }

    @RequestMapping(value = "/books/hello", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public Map<String, String> sayHello() {
        Map<String, String> map = new HashMap<>();
        map.put("msg", "hello");
        return map;
    }

    @RequestMapping(value = "/books", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public BookListDTO getBooks() {
        Person person = authService.getLoggedInPerson();
        return bookService.getBooks(person.getId());
    }

    @RequestMapping(value = {"/books/{id}", "/books/{id}/{pageAndLen}"}, method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public void readBook(HttpServletResponse response, @PathVariable("id") Long id,
                         @PathVariable(name = "pageAndLen", required = false) String pageAndLen) throws IOException {
//        Person person = authService.getLoggedInPerson();
        int begin = 0, end = 0;
        if (pageAndLen != null) {
            String[] args = pageAndLen.split(",");
            if (args.length == 2) {
                int page = Integer.parseInt(args[0]);
                int len = Integer.parseInt(args[1]);
                int[] range = new int[] {page, page + len};
                Arrays.sort(new int[]{page, page + len});
                begin = range[0]; end = range[1];
            }
        }

        bookService.consumeBook(id, begin, end, response.getOutputStream());
    }

    @RequestMapping(value = "/pinyin", produces = MediaType.APPLICATION_JSON_VALUE)
    public Map<String, String> getPinYin(@RequestParam("text") String text) {
        HanyuPinyinOutputFormat format = new HanyuPinyinOutputFormat();
        format.setCaseType(HanyuPinyinCaseType.LOWERCASE);
        format.setVCharType(HanyuPinyinVCharType.WITH_U_UNICODE);
        format.setToneType(HanyuPinyinToneType.WITH_TONE_MARK);

        Map<String, String> res = new HashMap<>();
//        if (StringUtils.isNotEmpty(text)) {
//            text.chars().forEach(ch -> {
//                String[] pinyinArray;
//                try {
//                    pinyinArray = PinyinHelper.toHanyuPinyinStringArray((char) ch, format);
//                } catch (Exception e) {
//                    throw new RuntimeException(e);
//                }
//                String pinyinStr = String.join(",", pinyinArray);
//                res.put(String.valueOf((char)ch), pinyinStr);
//            });
//        }

        return res;
    }

    @PostMapping(value = "/books", produces = MediaType.APPLICATION_JSON_VALUE)
    public UpdateResponseDTO savePDF(@RequestHeader("hash-key") String key, @RequestParam("file") MultipartFile file) throws IOException {
        if (!authService.verifyToken(key)) {
            throw new RuntimeException("Invalid access");
        }
        Person person = authService.getLoggedInPerson();
        String fileName = file.getOriginalFilename();
        bookService.saveBook(fileName, file, person);
        UpdateResponseDTO resp = new UpdateResponseDTO();
        resp.setStatus("OK");
        return resp;
    }

    @GetMapping("dict")
    public WordDictDTO getDictItem(@RequestParam("word") String word) {
        return bookService.getWordDict(word);
    }

    @ExceptionHandler({RuntimeException.class, IOException.class})
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Map<String, String> handleException(Exception ex) {
        HashMap<String, String> map = new HashMap<>();
        map.put("errorMsg", ex.getMessage());
        logger.error(ex.getMessage(), ex);
        return map;
    }

    @ExceptionHandler({ResourceNotFoundException.class})
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Map<String, String> handleNotFoundException(Exception ex) {
        HashMap<String, String> map = new HashMap<>();
        map.put("errorMsg", ex.getMessage());
        logger.error(ex.getMessage(), ex);
        return map;
    }
}
