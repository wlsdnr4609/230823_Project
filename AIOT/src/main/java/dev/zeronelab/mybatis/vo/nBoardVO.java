package dev.zeronelab.mybatis.vo;

import java.util.Date;
import lombok.Data;

@Data
public class nBoardVO {
  private int bNo;
  private String writer;
  private String title;
  private String content;
  private int viewCnt;
  private int replyCnt;
  private Date regidate;
  
  private String[] Files;
}
