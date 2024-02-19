package dev.zeronelab.mybatis.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import dev.zeronelab.mybatis.mapper.nBoardMapper;
import dev.zeronelab.mybatis.mapper.nReplyMapper;
import dev.zeronelab.mybatis.vo.Criteria;
import dev.zeronelab.mybatis.vo.PageMaker;
import dev.zeronelab.mybatis.vo.nReplyVO;

@RestController
@RequestMapping("/api/nreplys")
public class RestApiReplyController {

	private static final Logger logger = LoggerFactory.getLogger(RestApiNBoardController.class);

	@Autowired
	private nReplyMapper mapper;

	@Autowired
	private nBoardMapper Mapper;

	// 댓글 등록
	@RequestMapping(value = "/add", method = RequestMethod.POST)
	public ResponseEntity<String> insert(@RequestBody Map<String, Object> requestBody) {
		logger.info("............ADD post ...........");
		String bNo = (String) requestBody.get("bNo");
		String replyText = (String) requestBody.get("replyText");
		String replyer = (String) requestBody.get("replyer");
		ResponseEntity<String> entity = null;
		try {
			mapper.insertReply(bNo, replyText, replyer);
			Mapper.updateReplyCnt(bNo, 1);
			entity = new ResponseEntity<String>("SUCCESS", HttpStatus.OK);
		} catch (Exception e) {
			e.printStackTrace();
			entity = new ResponseEntity<String>(e.getMessage(), HttpStatus.BAD_REQUEST);
		}
		return entity;
	}

	// 댓글 리스트
	@RequestMapping(value = "/list/{bNo}", method = RequestMethod.GET)
	public ResponseEntity<List<nReplyVO>> selectList(@PathVariable("bNo") Integer bNo) {

		ResponseEntity<List<nReplyVO>> entity = null;
		try {
			entity = new ResponseEntity<>(mapper.selectList(bNo), HttpStatus.OK);

		} catch (Exception e) {
			e.printStackTrace();
			entity = new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}

		return entity;
	}

	// 댓글 수정
	@RequestMapping(value = "/{rNo}", method = { RequestMethod.PUT, RequestMethod.PATCH })
	public ResponseEntity<String> update(@PathVariable("rNo") Integer rNo, @RequestBody nReplyVO vo) {

		ResponseEntity<String> entity = null;
		try {
			vo.setRNo(rNo);
			mapper.update(vo);

			entity = new ResponseEntity<String>("SUCCESS", HttpStatus.OK);
		} catch (Exception e) {
			e.printStackTrace();
			entity = new ResponseEntity<String>(e.getMessage(), HttpStatus.BAD_REQUEST);
		}
		return entity;
	}

	// 댓글 삭제
	@RequestMapping(value = "/{rNo}/{bNo}", method = RequestMethod.DELETE)
	public ResponseEntity<String> delete(@PathVariable("rNo") Integer rNo, @PathVariable("bNo") String bNo)
			throws Exception {
		ResponseEntity<String> entity = null;
		try {
			mapper.delete(rNo);
			Mapper.updateReplyCnt(bNo, -1);
			entity = new ResponseEntity<String>("SUCCESS", HttpStatus.OK);
		} catch (Exception e) {
			e.printStackTrace();
			entity = new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
		}
		return entity;
	}

	// 댓글 페이지
	@RequestMapping(value = "/{bNo}/{page}", method = RequestMethod.GET)
	public ResponseEntity<Map<String, Object>> listPage(@PathVariable("bNo") Integer bNo,
			@PathVariable("page") Integer page) {

		ResponseEntity<Map<String, Object>> entity = null;

		try {
			Criteria cri = new Criteria();
			cri.setPage(page);

			PageMaker pageMaker = new PageMaker();
			pageMaker.setCri(cri);

			Map<String, Object> map = new HashMap<String, Object>();
			List<nReplyVO> list = mapper.listPage(bNo, cri);

			map.put("list", list);

			int replyCount = mapper.count(bNo);
			pageMaker.setTotalCount(replyCount);

			map.put("pageMaker", pageMaker);

			entity = new ResponseEntity<Map<String, Object>>(map, HttpStatus.OK);

		} catch (Exception e) {
			e.printStackTrace();
			entity = new ResponseEntity<Map<String, Object>>(HttpStatus.BAD_REQUEST);
		}
		return entity;
	}
}
