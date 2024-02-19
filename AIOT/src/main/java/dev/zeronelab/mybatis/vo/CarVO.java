package dev.zeronelab.mybatis.vo;

import java.util.Date;

import lombok.Data;

@Data
public class CarVO {

	private int carNo;
	private String carNum;
	private String carBrand;
	private String carModel;
	private String charType;
	private Date regidate;
	private int evcapacity;
	private String memId;
}
