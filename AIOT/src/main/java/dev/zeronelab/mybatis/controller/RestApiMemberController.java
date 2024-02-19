package dev.zeronelab.mybatis.controller;

import java.util.Date;
import java.util.List;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.util.WebUtils;

import dev.zeronelab.mybatis.dto.LoginDTO;
import dev.zeronelab.mybatis.mapper.MemberMapper;
import dev.zeronelab.mybatis.vo.Member;
import util.JwtUtils;
import util.PasswordEncoder;

@RestController
@RequestMapping("/api/members")
public class RestApiMemberController {

	private static final Logger logger = LoggerFactory.getLogger(RestApiMemberController.class);

	private JwtUtils jwtUtils = new JwtUtils();

	private PasswordEncoder passwordEncoder;

	@Autowired
	private MemberMapper membermapper;

	// 회원리스트
	@RequestMapping(value = "/list", method = RequestMethod.GET)
	public List<Member> selectList(Model model) throws Exception {
		logger.info("// /member/list");

		List<Member> list = membermapper.selectMemberList();

		logger.info("// list.toString()=" + list.toString());

		return list;
	}

	// mNo로 회원정보 조회
	@RequestMapping(value = "/readMNo", method = RequestMethod.POST)
	public Member selectMNo(@RequestBody Member mem) throws Exception {
		logger.info("read post ...........");
		logger.info(mem.toString());

		return membermapper.selectMNo(mem.getMemNo());
	}

	/// 회원가입
	@RequestMapping(value = "/register", method = RequestMethod.POST)
	public String insertMemPOST(@RequestBody Member mem) throws Exception {

		logger.info("regist post ...........");
		logger.info(mem.toString());

		BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
		String hashedPassword = passwordEncoder.encode(mem.getMemPw());

		System.out.println("해시된 비밀번호:" + hashedPassword);

		mem.setMemPw(hashedPassword);

		membermapper.insertMem(mem);
		return "success";
	}

	// 로그인
	@RequestMapping(value = "/loginPost", method = RequestMethod.POST)
	public ResponseEntity<?> loginPOST(@RequestBody LoginDTO dto) throws Exception {

		logger.info("// /loginPost");
		logger.info(dto.toString());
		final String token = jwtUtils.generateToken(dto.getMemId());
		System.out.println("/*** encordingStr=" + token);

		String decordedStr = jwtUtils.getEmailFromToken(token);
		System.out.println("/*** decordingStr=" + decordedStr);

		boolean memId = jwtUtils.validateToken(token, dto.getMemId());
		System.out.println("email=" + memId);

		String storedHashedPassword = membermapper.getHashedPasswordByEmail(dto.getMemId());

		logger.info("Stored Hashed Password: " + storedHashedPassword);

		BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
		boolean isPasswordMatch = passwordEncoder.matches(dto.getMemPw(), storedHashedPassword);

		logger.info("비밀번호 일치 여부: " + isPasswordMatch);

		if (isPasswordMatch) {
			dto.setMemPw(storedHashedPassword);
			logger.info("/*** dto.toString()=" + dto.toString());
			Member loggedInMember = membermapper.login(dto);
			
			 // 토큰을 응답 헤더에 포함시켜 클라이언트로 전송
	        HttpHeaders responseHeaders = new HttpHeaders();
	        responseHeaders.set("Authorization", "Bearer " + token);
	        
	        return new ResponseEntity<>(loggedInMember, responseHeaders, HttpStatus.OK);
	    } else { // 불일치 시 오류 응답 반환
			return new ResponseEntity<>("로그인 실패 메시지", HttpStatus.UNAUTHORIZED);
		}
	}


	@RequestMapping(value = "/loginCookie", method = RequestMethod.POST)
	public Member loginCookie(@RequestBody LoginDTO dto) throws Exception {

		logger.info("/*** /loginCookie 시작...");
		logger.info(dto.toString());
		final String token = jwtUtils.generateToken(dto.getMemId());
		System.out.println("/*** encordingStr=" + token);

		String decordedStr = jwtUtils.getEmailFromToken(token);
		System.out.println("/*** decordingStr=" + decordedStr);

		boolean email = jwtUtils.validateToken(token, dto.getMemId());
		System.out.println("email=" + email);

		String storedHashedPassword = membermapper.getHashedPasswordByEmail(dto.getMemId());

		logger.info("Stored Hashed Password: " + storedHashedPassword);

		boolean isPasswordMatch = false;
		if (storedHashedPassword != null && dto.getMemPw() != null) {
			isPasswordMatch = storedHashedPassword.equals(dto.getMemPw());
		} else {
			return null;
		}

		logger.info("비밀번호 일치 여부: " + isPasswordMatch);

		if (isPasswordMatch) {
			// dto.setPw(storedHashedPassword);
			logger.info("/*** dto.toString()=" + dto.toString());
			return membermapper.login(dto);
		} else {
			return null;
		}
	}

	// 로그아웃
	@RequestMapping(value = "/logout", method = RequestMethod.GET)
	public String logout(HttpServletRequest request, HttpServletResponse response, HttpSession session)
			throws Exception {

		Object obj = session.getAttribute("login");

		if (obj != null) {
			Member mem = (Member) obj;

			session.removeAttribute("login");
			session.invalidate();

			Cookie loginCookie = WebUtils.getCookie(request, "loginCookie");

			if (loginCookie != null) {
				loginCookie.setPath("/");
				loginCookie.setMaxAge(0);
				response.addCookie(loginCookie);
				membermapper.keepLogin(mem.getMemId(), session.getId(), new Date());
			}
		}
		return "success";
	}

	// 이메일 중복체크
	@RequestMapping(value = "/emailCk", method = RequestMethod.POST)
	public Member eamilCk(@RequestBody Member mem) throws Exception {
		logger.info("emailCk post ...........");
		logger.info(mem.toString());

		return membermapper.emailCk(mem.getMemId());
	}

	// 이메일로 mid 체크
	@RequestMapping(value = "/midCk", method = RequestMethod.POST)
	public Member midCk(@RequestBody Member mem) throws Exception {
		logger.info("midCk post ...........");
		logger.info(mem.toString());

		return membermapper.midCk(mem.getMemId());

	}

	// 닉네임 중복체크
	@RequestMapping(value = "/ninameCk", method = RequestMethod.POST)
	public Member ninameCk(@RequestBody Member mem) throws Exception {
		logger.info("ninameCk post ...........");
		logger.info(mem.toString());

		return membermapper.ninameCk(mem.getMemNickName());
	}

	// 마이페이지 회원정보 조회
	@RequestMapping(value = "/read", method = RequestMethod.POST)
	public Member selectMemId(@RequestBody Member mem) throws Exception {

		// model.addAttribute("mem", membermapper.readMember(email));
		logger.info("조회할 이메일 : " + mem.getMemId());

		return membermapper.selectMemId(mem.getMemId());
	}

	// 마이페이지 회원정보 수정
	@RequestMapping(value = "/modify", method = RequestMethod.POST)
	public String updatePOST(@RequestBody Member mem, RedirectAttributes rttr) throws Exception {

		logger.info(mem.toString());
		BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
		String hashedPassword = passwordEncoder.encode(mem.getMemPw());

		System.out.println("해시된 비밀번호:" + hashedPassword);

		mem.setMemPw(hashedPassword);

		membermapper.update(mem);

		rttr.addAttribute("name", mem.getMemName());
		rttr.addFlashAttribute("msg", "SUCCESS");

		logger.info(rttr.toString());

		return "SUCCESS";

	}

	// 회원탈퇴
	@RequestMapping(value = "/remove", method = RequestMethod.POST)
	public String delete(@RequestBody Member mem) throws Exception {

		logger.info("delete post ...........");
		logger.info(mem.toString());

		membermapper.delete(mem.getMemId());

		return "succ";
	}
}