package dev.zeronelab.mybatis.dao;

import java.util.Date;
import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import dev.zeronelab.mybatis.dto.LoginDTO;
import dev.zeronelab.mybatis.vo.Member;

@Mapper
public interface MemberMapper {
	List<Member> selectMemberList() throws Exception;
	
	public void register(Member mem);
	
	public Member login(LoginDTO dto);

	public String getHashedPasswordByEmail(String memId);

	public void keepLogin(String memId, String id, Date sessionLimit);

	public Member readMember(String memId);
	
	public void modifyMember(Member mem);
	
	public void modifyName(Member mem);
	
	public void modifyNiname(Member mem);

	public void modifyPw(Member mem);

    public void modifyLoca(Member mem);

	public Member read(int memNo);

	public void delete(String memId);

	public Member emailCk(String memId);

	public Member ninameCk(String memNickName);

	public Member midCk(String memId);

}
