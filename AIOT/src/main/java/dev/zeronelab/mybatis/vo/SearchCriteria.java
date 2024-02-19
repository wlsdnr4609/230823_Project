package dev.zeronelab.mybatis.vo;

import lombok.Data;

@Data
public class SearchCriteria extends Criteria {
 
  private String searchType;
  private String keyword;
  
  public boolean hasSearchCondition() {
	  return this.searchType != null && this.keyword != null;
  }
}


