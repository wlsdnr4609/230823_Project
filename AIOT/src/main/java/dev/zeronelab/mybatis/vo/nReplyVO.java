package dev.zeronelab.mybatis.vo;

import java.util.Date;

import lombok.Data;

@Data
public class nReplyVO {

  private Integer rNo;
  private Integer bNo;
  private String replyText;
  private String replyer;
  private Date regdate;
  private Date modidate;
}
