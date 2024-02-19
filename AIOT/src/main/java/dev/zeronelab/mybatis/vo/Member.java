package dev.zeronelab.mybatis.vo;

import java.util.Date;

import lombok.Data;

@Data
public class Member {


	private int memNo;
	private String memId;
	private String memPw;
	private String memNickName;
	private String memName;
	private Date regdate;
	private String limittime;
	private String sessionid;
}
