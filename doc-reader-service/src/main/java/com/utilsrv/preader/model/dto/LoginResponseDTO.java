package com.utilsrv.preader.model.dto;

import lombok.Data;

@Data
public class LoginResponseDTO {
	private String status;
	private String jwt;
}
