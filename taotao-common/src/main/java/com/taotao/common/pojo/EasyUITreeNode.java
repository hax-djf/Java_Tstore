package com.taotao.common.pojo;

import java.io.Serializable;
/**
 * 商品上传的目录的的返回 ，每一个目录都是以json格式数据返回
 * 并且返回的数据参数中包括着 id text文本 state 状态
 * 在js中 这个状态返回的是一个closed 和open,会根据传入的父节点查到所有的子叶节点
 * 如果是子叶节点的话，状态就是open , 否则不是叶子节点就是closed ,单点击closed的状态的节点的时候，会发送一个ajax请求
 * 这个请求回去查询下面的所有的子节点数据，然后进行返回 然后将节点的属性再次封装到这个类中
 * @author Administrator
 *
 */
public class EasyUITreeNode implements Serializable{
	private Long id; //节点id
	private String text; //节点id的数据
	private String state; //节点id的状态
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getText() {
		return text;
	}
	public void setText(String text) {
		this.text = text;
	}
	public String getState() {
		return state;
	}
	public void setState(String state) {
		this.state = state;
	}
	
	
}
