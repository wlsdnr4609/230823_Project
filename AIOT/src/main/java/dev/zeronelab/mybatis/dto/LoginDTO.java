package dev.zeronelab.mybatis.dto;

import lombok.Data;

@Data
public class LoginDTO {
	
	private String memId;
	private String memPw;
	private boolean useCookie;


}
