package com.taotao.listener;

import java.io.File;
import java.io.FileWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;

import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfigurer;

import com.taotao.item.pojo.Item;
import com.taotao.pojo.TbItem;
import com.taotao.pojo.TbItemDesc;
import com.taotao.service.ItemListService;

import freemarker.template.Configuration;
import freemarker.template.Template;

/**
 * 这个地方是一个监听器 ,当有新的商品操作业务的时候,这个时候进行商品的静态化页面的生成
 * taotao_manage 进行消息的额发送的
 * @author Administrator
 *
 */
public class ItemChangeGenHtmMessageListener implements MessageListener {
	@Autowired  
	private ItemListService itemListService;
	
	@Autowired
	private FreeMarkerConfigurer config;
	@Override
	public void onMessage(Message message) {
		//进行消息的获取
		TextMessage textMessage =(TextMessage)message;
		//获取到数据 这个地方的数据传递的商品的id
		try {
			Long itemid =Long.valueOf(textMessage.getText());
			//进行服务的调用 可以调用mapper 从数据中查询到数据 进行静态化的生成
			TbItem tbItem = itemListService.getItemById(itemid);
			Item item =new Item(tbItem);
			//这样进行图片的封装
			TbItemDesc itemDesc = itemListService.getItemDescById(itemid);
			//准备到静态化的模板 将数据传递页面上面去
			genHtmlFreemarker(item,itemDesc);
			
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
	
	//静态化页面的 生成操作
	private void genHtmlFreemarker(Item item, TbItemDesc itemDesc) throws Exception {
		//1.获取到Configuration 通过FreeMarkerConfigurer对象
		Configuration configuration = config.getConfiguration();
		//2.创建模板并且获取到模板的对象
		Template template =configuration.getTemplate("item.ftl");
		//3.创建数据集
		Map model=new HashMap<>();
		model.put("item", item);
		model.put("itemDesc", itemDesc);
		//将数据进行输出
		Writer writer = new FileWriter(new File("d:\\Test"+item.getId()+".html"));
		//进行流的关闭
		writer.close();
		
	}

}
