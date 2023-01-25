package com.utilsrv.preader.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data @AllArgsConstructor
public class WordDictDTO {
    private String word;
    private String pinYin;
    private String content;
}
