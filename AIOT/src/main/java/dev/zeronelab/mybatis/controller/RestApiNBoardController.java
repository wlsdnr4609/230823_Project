package dev.zeronelab.mybatis.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import dev.zeronelab.mybatis.dto.nBoardDTO;
import dev.zeronelab.mybatis.dto.nBoardImageDTO;
import dev.zeronelab.mybatis.mapper.nBoardMapper;
import dev.zeronelab.mybatis.vo.PageMaker;
import dev.zeronelab.mybatis.vo.SearchCriteria;
import dev.zeronelab.mybatis.vo.nBoardVO;

@RestController
@RequestMapping("/api/nBoards")
public class RestApiNBoardController {

	private static final Logger logger = LoggerFactory.getLogger(RestApiNBoardController.class);

	@Autowired
	private nBoardMapper mapper;

	// 게시글 페이지 리스트
	@RequestMapping(value = "/list/{page}", method = RequestMethod.GET)
	public ResponseEntity<Map<String, Object>> selectNBoardListPage(@PathVariable("page") Integer page,
			@ModelAttribute(value = "cri") SearchCriteria cri) {
		ResponseEntity<Map<String, Object>> entity = null;

		try {// 검색 조건이 없으면 새로운 SearchCriteria 객체를 생성하여 사용
			if (cri == null) {
				cri = new SearchCriteria();
			}
			cri.setPage(page);

			PageMaker pageMaker = new PageMaker();
			pageMaker.setCri(cri);

			Map<String, Object> map = new HashMap<>();
			List<nBoardVO> list;

			// 검색 조건이 있는 경우와 없는 경우를 구분하여 데이터를 가져옴
			int boardCount;
			if (cri.hasSearchCondition()) {
				// 검색 조건이 있는 경우
				list = mapper.listSearch(cri);
				boardCount = mapper.listSearchCount(cri);
			} else {
				// 검색 조건이 없는 경우
				list = mapper.selectBoardList(cri);
				boardCount = mapper.selectBoardListCount(cri);

			}
			pageMaker.setTotalCount(boardCount);

			map.put("list", list);
			map.put("pageMaker", pageMaker);

			entity = new ResponseEntity<>(map, HttpStatus.OK);

		} catch (Exception e) {
			e.printStackTrace();
			entity = new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}
		return entity;
	}

	// 게시글 읽기
	@RequestMapping(value = "/read", method = RequestMethod.POST)
	public nBoardDTO read(@RequestBody Map<String, Integer> request) throws Exception {
		int bNo = request.get("bNo");

		// 조회수
		mapper.updateCounts(bNo);

		// 게시글 정보 가져오기
		nBoardDTO nb = mapper.read(bNo);

		// 이미지 정보 가져오기
		List<nBoardImageDTO> imageDTOList = getImageDTOList(bNo);
		System.out.println("/*** imageDTOList=" + imageDTOList.toString());

		nb.setImageDTOList(imageDTOList);

		return nb;
	}

	private List<nBoardImageDTO> getImageDTOList(int bNo) {
		return mapper.getImageDTOList(bNo); // 실제로 이미지 정보를 조회하는 메서드를 호출하도록 변경
	}

	// 게시글 작성
	@RequestMapping(value = "/write", method = RequestMethod.POST)
	public String writePOST(@RequestBody nBoardDTO nBoardDTO) throws Exception {
		System.out.println("nBoardDTO: " + nBoardDTO);

		// nBoardDTO 생성
		mapper.write(nBoardDTO);

		List<nBoardImageDTO> imageDTOList = nBoardDTO.getImageDTOList();

		if (imageDTOList != null && !imageDTOList.isEmpty()) {
			for (nBoardImageDTO imageDTO : imageDTOList) {
				String imgName = imageDTO.getImgName();
				String uuid = imageDTO.getUuid();
				String path = imageDTO.getPath();

				// mapper에 fileName, uuid, path 등을 활용한 로직 추가
				mapper.addAttach(imgName, uuid, path);
			}
		}
		return "succ";
	}

	// 게시글 수정
	@RequestMapping(value = "/modify", method = RequestMethod.POST)
	public String updatePOST(@RequestBody Map<String, Object> requestBody) throws Exception {
		logger.info("modifyPagingpost...........");

		// 클라이언트가 보낸 요청 본문에서 게시물 번호, 제목, 내용을 추출
		String bNo = (String) requestBody.get("bNo");
		String title = (String) requestBody.get("title");
		String content = (String) requestBody.get("content");

		logger.info("게시물 번호를 사용: " + bNo);

		// MyBatis의 update 메서드를 호출하여 추출한 값들로 게시물 업데이트
		mapper.update(title, content, bNo);

		// 첨부 파일 삭제
		mapper.deleteAttach(bNo);

		// 요청 본문에서 imageDTOList 추출
		Object rawImageDTOList = requestBody.get("imageDTOList");

		if (rawImageDTOList instanceof List) {
			List<?> imageDTOList = (List<?>) rawImageDTOList;

			// 여기서 imageDTOList를 사용할 수 있음
			for (Object imageDTO : imageDTOList) {
				if (imageDTO instanceof Map) {
					Map<?, ?> imageMap = (Map<?, ?>) imageDTO;
					String imgName = (String) imageMap.get("imgName");
					String uuid = (String) imageMap.get("uuid");
					String path = (String) imageMap.get("path");

					// mapper에 fileName, uuid, path 등을 활용한 로직 추가
					mapper.replaceAttach(imgName, uuid, path, bNo);
				}
			}
		} else {
			// 적절한 타입이 아닌 경우에 대한 처리
		}

		return "succ";
	}

	// 게시글 삭제
	@RequestMapping(value = "/remove", method = RequestMethod.POST)
	public String deletePOST(@RequestBody Map<String, Integer> request) throws Exception {
		logger.info("delete post ...........");
		int bNo = request.get("bNo");
		// bNo를 사용하여 필요한 작업 수행

		mapper.delete(bNo);

		return "succ";
	}

}