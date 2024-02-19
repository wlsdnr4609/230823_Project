package dev.zeronelab.mybatis.dao;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import dev.zeronelab.mybatis.vo.CarVO;

@Mapper
public interface CarMapper {
	List<CarVO> selectCarList() throws Exception;

	public List<CarVO> read(String memId);	
	
	public void carRegi(CarVO vo);
	
	CarVO carNumCK(String carNum);

	public void delete(CarVO vo);


}
