package dev.zeronelab.mybatis.web;

import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import dev.zeronelab.mybatis.dao.nBoardMapper;
import dev.zeronelab.mybatis.dto.ListResponse;
import dev.zeronelab.mybatis.vo.PageMaker;
import dev.zeronelab.mybatis.vo.SearchCriteria;
import dev.zeronelab.mybatis.vo.nBoardVO;

/**
 * Handles requests for the application home page.
 */

@RestController
@RequestMapping("/api/nBoard")
public class RestApiBoardController {

	private static final Logger logger = LoggerFactory.getLogger(RestApiBoardController.class);

	@Autowired
	private nBoardMapper mapper;

	// 게시글 목록
	@RequestMapping(value = "/list", method = RequestMethod.GET)
	public ResponseEntity<ListResponse<nBoardVO>> listPage(@ModelAttribute(value = "cri") SearchCriteria cri,
			Model model, HttpServletResponse response) throws Exception {

		logger.info(cri.toString());

		List<nBoardVO> list;

		
		if (cri != null) { // 만약 검색 조건이 null이 아니라면 검색 조건을 이용하여 목록을 가져옴
			list = mapper.listSearch(cri);

		} else { // 검색 조건이 null이면 전체 목록을 가져옴
			list = mapper.selectBoardList();
		} // 생성된 목록과 검색 조건을 이용하여 응답용 객체를 생성
		ListResponse<nBoardVO> responseList = createListResponse(list, cri);
		return ResponseEntity.ok(responseList);
	}

	private ListResponse<nBoardVO> createListResponse(List<nBoardVO> list, SearchCriteria cri) {
		PageMaker pageMaker = new PageMaker();

		
		if (cri != null) { // 만약 검색 조건이 있다면
			pageMaker.setCri(cri); // 페이징 정보에 검색 조건을 설정함
			pageMaker.setTotalCount(mapper.listSearchCount(cri)); // 검색조건을 만족하는 전체 항목 개수를 설정함
		} // 생성된 목록과 검색 조건을 이용하여 응답용 객체를 생성
		return new ListResponse<> (list, pageMaker);
	}

	
	// 게시글 작성
	@RequestMapping(value = "/write", method = RequestMethod.POST)
	public String writePOST(@RequestBody nBoardVO vo) throws Exception {

		logger.info(".............글 작성 post ...........");
		logger.info(vo.toString());

		mapper.write(vo);

		return "succ";
	}

	
	// 게시글 읽기
	@RequestMapping(value = "/read", method = RequestMethod.POST)
	public List<nBoardVO> read(@RequestParam("bNo") int bNo) throws Exception {
		logger.info("read post ...........");
		logger.info("bNo: " + bNo);
		List<nBoardVO> list = mapper.read(bNo);
		return list;
				 
	}
}