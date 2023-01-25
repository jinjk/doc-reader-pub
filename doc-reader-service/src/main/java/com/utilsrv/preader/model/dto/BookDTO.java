package com.utilsrv.preader.model.dto;

import lombok.Data;

@Data
public class BookDTO {
    private Long id;
    private String bookName;
    private String bookMark;
    private Integer pageCount;
    private Integer rating;
}
