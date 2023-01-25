package com.utilsrv.preader.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data @AllArgsConstructor
public class BookListDTO {
    private List<BookDTO> myBooks;
    private List<BookDTO> recommended;
}
