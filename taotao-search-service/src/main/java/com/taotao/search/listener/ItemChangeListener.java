package com.taotao.search.listener;

import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;

import org.springframework.beans.factory.annotation.Autowired;

import com.taotao.search.service.SearchItemService;

public class ItemChangeListener implements MessageListener {

	@Autowired
	private SearchItemService searchItemService; 
	@Override
	public void onMessage(Message message) {
		TextMessage textMessage=null;
		try {
		if(message instanceof TextMessage) {
			//有数据话 获取大数据
			textMessage=(TextMessage)message;
			long itemId=Long.parseLong(textMessage.getText());
			//向索引库中添加数据
			searchItemService.updateItemById(itemId);
		}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}
