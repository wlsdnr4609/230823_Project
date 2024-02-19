package dev.zeronelab.mybatis.dto;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class nBoardDTO {

	private int bNo;

	private String title;

	@Builder.Default
	private List<nBoardImageDTO> imageDTOList = new ArrayList<>();

	private String writer;

	private String content;

	private int viewCnt;

	private int replyCnt;

	private Date regidate;

}
