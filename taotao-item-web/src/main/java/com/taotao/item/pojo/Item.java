package com.taotao.item.pojo;

import java.io.Serializable;

import org.springframework.beans.BeanUtils;

import com.taotao.pojo.TbItem;

public class Item extends TbItem implements Serializable{
	//将图片进行拆分处理
	public String[] getImages() {
		String image2 = this.getImage();
		if (image2 != null && !"".equals(image2)) {
			String[] strings = image2.split(",");
			return strings;
		}
		return null;
	}
	//将属性直接通过反射直接copy过来
	public Item(TbItem tbitem){
		BeanUtils.copyProperties(tbitem, this);//讲原来数据有的属性的值拷贝到item有的属性中
	}
	
	

	

}
