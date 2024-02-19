package dev.zeronelab.mybatis.dto;

import java.util.List;

import dev.zeronelab.mybatis.vo.PageMaker;

import lombok.Data;

@Data
public class ListResponse<T> {

	private List<T> list;
	private PageMaker pageMaker;
	
	public ListResponse(List<T> list, PageMaker pageMaker) {
        this.list = list;
        this.pageMaker = pageMaker;
    }
}
