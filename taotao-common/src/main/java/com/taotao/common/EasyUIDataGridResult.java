package com.taotao.common;

import java.io.Serializable;
import java.util.List;

/**
 * 返回一个pojo的包装类在common中,因为在service层和web层中国都需要使用的
 * @author Administrator
 * 需要实现implements Serializable 因为在service数据返回的过程
 */
public class EasyUIDataGridResult implements Serializable {
	//返回的数据格式 EasyUI
	private Integer total;
	private List rows;
	
	public Integer getTotal() {
		return total;
	}

	public void setTotal(Integer total) {
		this.total = total;
	}

	public List getRows() {
		return rows;
	}

	public void setRows(List rows) {
		this.rows = rows;
	}

	@Override
	public String toString() {
		return "EasyUIDataGridResult [total=" + total + ", rows=" + rows + "]";
	}
	

}
