package dev.zeronelab.mybatis.web;

import java.util.Date;
import java.util.List;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.util.WebUtils;

import dev.zeronelab.mybatis.dao.MemberMapper;
import dev.zeronelab.mybatis.dto.LoginDTO;
import dev.zeronelab.mybatis.vo.Member;
import util.JwtUtils;
import util.PasswordEncoder;

/**
 * Handles requests for the application home page.
 */

@RestController
@RequestMapping("/api/member")
public class RestApiMemberController {

	private static final Logger logger = LoggerFactory.getLogger(RestApiMemberController.class);

	private JwtUtils jwtUtils = new JwtUtils();

	private PasswordEncoder passwordEncoder;

	@Autowired
	private MemberMapper membermapper;

	// 회원리스트
	@RequestMapping(value = "/list", method = RequestMethod.GET)
	public List<Member> memberdList(Model model) throws Exception {
		logger.info("// /member/list");

		List<Member> list = membermapper.selectMemberList();

		logger.info("// list.toString()=" + list.toString());

		return list;
	}

	// mid로 회원정보 조회
	@RequestMapping(value = "/read", method = RequestMethod.POST)
	public Member read(@RequestBody Member mem) throws Exception {
		logger.info("read post ...........");
		logger.info(mem.toString());

		return membermapper.read(mem.getMemNo());

	}

	/// 회원가입
	@RequestMapping(value = "/register", method = RequestMethod.POST)
	public String registPOST(@RequestBody Member mem) throws Exception {

		logger.info("regist post ...........");
		logger.info(mem.toString());

		BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
		String hashedPassword = passwordEncoder.encode(mem.getMemPw());

		System.out.println("해시된 비밀번호:" + hashedPassword);

//		boolean isPasswordMatch = passwordEncoder.matches(mem.getPw(), hashedPassword);
//		System.out.println("비밀번호 일치 여부:" + isPasswordMatch);

		mem.setMemPw(hashedPassword);

		membermapper.register(mem);
		return "success";
	}

	// 로그인
	@RequestMapping(value = "/login", method = RequestMethod.GET)
	public void loginGET(@ModelAttribute("dto") LoginDTO dto) {
	}

	@RequestMapping(value = "/loginPost", method = RequestMethod.POST)
	public Member loginPOST(@RequestBody LoginDTO dto) throws Exception {

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
			logger.info("/*** dto.toString()="+dto.toString());
			return membermapper.login(dto);
		} else {
			// Passwords do not match, handle the error (e.g., return an error response)
			return null; // Adjust the return type accordingly
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

		//BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
		//boolean isPasswordMatch = passwordEncoder.matches(dto.getPw(), storedHashedPassword);
		boolean isPasswordMatch = false;
		if (storedHashedPassword != null && dto.getMemPw() != null) {
		    isPasswordMatch = storedHashedPassword.equals(dto.getMemPw());
		} else {
		    // storedHashedPassword 또는 dto.getPw()가 null인 경우 처리할 내용
		}

		logger.info("비밀번호 일치 여부: " + isPasswordMatch);

		
		if (isPasswordMatch) {
			//dto.setPw(storedHashedPassword);
			logger.info("/*** dto.toString()="+dto.toString());
			return membermapper.login(dto);
		} else {
			// Passwords do not match, handle the error (e.g., return an error response)
			return null; // Adjust the return type accordingly
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
	@RequestMapping(value = "/readMember", method = RequestMethod.POST)
	public Member readMember(@RequestBody Member mem) throws Exception {

		// model.addAttribute("mem", membermapper.readMember(email));
		logger.info("조회할 이메일 : " + mem.getMemId());

		return membermapper.readMember(mem.getMemId());
	}

	// 마이페이지 회원정보 수정
	@RequestMapping(value = "/modifyMember", method = RequestMethod.POST)
	public String modifyMemberPOST(@RequestBody Member mem, RedirectAttributes rttr) throws Exception {

		logger.info(mem.toString());
		BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
		String hashedPassword = passwordEncoder.encode(mem.getMemPw());

		System.out.println("해시된 비밀번호:" + hashedPassword);

//		boolean isPasswordMatch = passwordEncoder.matches(mem.getPw(), hashedPassword);
//		System.out.println("비밀번호 일치 여부:" + isPasswordMatch);

		mem.setMemPw(hashedPassword);
		
		membermapper.modifyMember(mem);

		rttr.addAttribute("name", mem.getMemName());
		rttr.addFlashAttribute("msg", "SUCCESS");

		logger.info(rttr.toString());

		return "SUCCESS";

	}
	@RequestMapping(value = "/modifyName", method = RequestMethod.POST)
	public String modifyNamePOST(@RequestBody Member mem, RedirectAttributes rttr) throws Exception {

		logger.info(mem.toString());

		membermapper.modifyName(mem);

		rttr.addAttribute("name", mem.getMemName());
		rttr.addFlashAttribute("msg", "SUCCESS");

		logger.info(rttr.toString());

		return "SUCCESS";

	}

	@RequestMapping(value = "/modifyNiname", method = RequestMethod.POST)
	public String modifyNinamePOST(@RequestBody Member mem, RedirectAttributes rttr) throws Exception {

		logger.info(mem.toString());

		membermapper.modifyNiname(mem);

		rttr.addAttribute("niname", mem.getMemNickName());
		rttr.addFlashAttribute("msg", "SUCCESS");

		logger.info(rttr.toString());

		return "SUCCESS";
	}

	@RequestMapping(value = "/modifyPw", method = RequestMethod.POST)
	public String modifyPwPOST(@RequestBody Member mem, RedirectAttributes rttr) throws Exception {

		logger.info(mem.toString());
		BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
		String hashedPassword = passwordEncoder.encode(mem.getMemPw());
		
		System.out.println("해시된 비밀번호:" + hashedPassword);
		
		BCryptPasswordEncoder passwordEncoder1 = new BCryptPasswordEncoder();
		String hashedPassword1 = passwordEncoder1.encode(mem.getMemPw());

		System.out.println("해시된 비밀번호:" + hashedPassword1);

//		boolean isPasswordMatch = passwordEncoder.matches(mem.getPw(), hashedPassword);
//		System.out.println("비밀번호 일치 여부:" + isPasswordMatch);

		mem.setMemPw(hashedPassword1);
		membermapper.modifyPw(mem);

		rttr.addAttribute("pw", mem.getMemPw());
		rttr.addFlashAttribute("msg", "SUCCESS");
		

		logger.info(rttr.toString());

		return "SUCCESS";
	}

//	@RequestMapping(value = "/modifyLocagree", method = RequestMethod.POST)
//	public String modifyLocagreePOST(@RequestBody Member mem, RedirectAttributes rttr) throws Exception {
//
//		logger.info(mem.toString());
//
//		membermapper.modifyLoca(mem);
//
//		rttr.addAttribute("locagree", mem.getLocagree());
//		rttr.addFlashAttribute("msg", "SUCCESS");
//
//		logger.info(rttr.toString());
//
//		return "SUCCESS";
//	}

	// 회원탈퇴
	@RequestMapping(value = "/deleteMember", method = RequestMethod.POST)
	public String delete(@RequestBody Member mem) throws Exception {

		logger.info("delete post ...........");
		logger.info(mem.toString());

		membermapper.delete(mem.getMemId());

		return "succ";

	}
}