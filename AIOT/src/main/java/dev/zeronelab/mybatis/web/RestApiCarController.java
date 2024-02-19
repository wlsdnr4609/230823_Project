package dev.zeronelab.mybatis.web;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import dev.zeronelab.mybatis.dao.CarMapper;
import dev.zeronelab.mybatis.vo.CarVO;

/**
 * Handles requests for the application home page.
 */

@RestController
@RequestMapping("/api/car")
public class RestApiCarController {

	private static final Logger logger = LoggerFactory.getLogger(RestApiCarController.class);

	@Autowired
	private CarMapper carmapper;

	// 차량리스트
	@RequestMapping(value = "/list", method = RequestMethod.GET)
	public List<CarVO> carList(Model model) throws Exception {
		logger.info(". . . . . . . ./car/list");

		List<CarVO> list = carmapper.selectCarList();

		logger.info(". . . . . . . . .// list.toString()=" + list.toString());

		return list;
	}

	
	// memId로 차량정보 조회
	@RequestMapping(value = "/read", method = RequestMethod.POST)
	public  List<CarVO> read(@RequestBody CarVO vo) throws Exception {
		logger.info("read post ...........");
		logger.info("MemId: " + vo.getMemId());

		List<CarVO> list = carmapper.read(vo.getMemId());
		
		 return list;
	}

	// 차량등록
	@RequestMapping(value = "/regi", method = RequestMethod.POST)
	public String carRegi(@RequestBody CarVO vo) throws Exception {

		logger.info("regi post ...........");
		logger.info(vo.toString());

		carmapper.carRegi(vo);

		return "succ";

		
	}
	
	// 차량번호 중복 체크
	@RequestMapping(value = "/carNumCK", method = RequestMethod.POST)
	public CarVO carNumCk(@RequestBody CarVO vo) throws Exception {
		logger.info("cNumCk post ...........");
		logger.info("carNum: " + vo.getCarNum());

		return carmapper.carNumCK(vo.getCarNum());

	}

	// 차량정보삭제
		@RequestMapping(value = "/remove", method = RequestMethod.POST)
		public String delete(@RequestBody CarVO vo) throws Exception {

			logger.info("remove post ...........");
			logger.info(vo.toString());

			carmapper.delete(vo);

			return "succ";

		}
}
