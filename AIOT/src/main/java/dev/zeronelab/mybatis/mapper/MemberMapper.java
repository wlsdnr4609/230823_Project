package dev.zeronelab.mybatis.mapper;

import java.util.Date;
import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import dev.zeronelab.mybatis.dto.LoginDTO;
import dev.zeronelab.mybatis.vo.Member;

@Mapper
public interface MemberMapper {
	List<Member> selectMemberList() throws Exception;
	
	public Member selectMNo(int memNo);	
	
	public void insertMem(Member mem);

	public String getHashedPasswordByEmail(String memId);
	
	public Member login(LoginDTO dto);

	public void keepLogin(String memId, String id, Date sessionLimit);

	public Member emailCk(String memId);

	public Member ninameCk(String memNickName);

	public Member midCk(String memId);

	public Member selectMemId(String memId);
	
	public void update(Member mem);

	public void delete(String memId);

}
