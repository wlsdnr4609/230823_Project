package dev.zeronelab.mybatis.dao;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import dev.zeronelab.mybatis.vo.SearchCriteria;
import dev.zeronelab.mybatis.vo.nBoardVO;

@Mapper
public interface nBoardMapper {
	List<nBoardVO> selectBoardList() throws Exception;

	List<nBoardVO> listSearch(SearchCriteria cri);

	int listSearchCount(SearchCriteria cri);

	void write(nBoardVO vo);

	List<nBoardVO> read(int bNo);

	
	
}
