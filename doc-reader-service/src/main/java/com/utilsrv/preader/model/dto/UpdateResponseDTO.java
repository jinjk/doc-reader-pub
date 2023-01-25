package com.utilsrv.preader.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @AllArgsConstructor @NoArgsConstructor
public class UpdateResponseDTO {
    private String status;
    private String message;
}
