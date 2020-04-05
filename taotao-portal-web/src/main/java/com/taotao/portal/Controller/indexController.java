package com.taotao.portal.Controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import com.taotao.common.util.JsonUtils;
import com.taotao.content.service.contentService;
import com.taotao.pojo.TbContent;
import com.taotao.portal.pojo.Ad1Node;

@Controller
public class indexController {
	
	@Autowired
	private contentService contentservice;
	
	@Value("${AD1_CATEGORY_ID}")
	private Long AD1_CATEGORY_ID;
	
	@Value("${AD1_HEIGHT}")
	private String AD1_HEIGHT;
	@Value("${AD1_HEIGHT_B}")
	private String AD1_HEIGHT_B;
	@Value("${AD1_WIDTH}")
	private String AD1_WIDTH;
	@Value("${AD1_WIDTH_B}")
	private String AD1_WIDTH_B;

	/**
	 *	 首页返回 
	 *1.显示轮播图操作
	 * @return
	 */
	@RequestMapping("/index")
	public String indexController(Model model) {
		List<Ad1Node> lsitnodes=new ArrayList<Ad1Node>();
		List<TbContent> contentList = this.contentservice.getContentList(this.AD1_CATEGORY_ID);
		//将查询的内容封装到一个自定义的pojo中
		for (TbContent tbContent : contentList) {
			Ad1Node node=new Ad1Node();
			node.setHref(tbContent.getUrl());
			node.setSrc(tbContent.getPic());
			node.setSrcB(tbContent.getPic2());
			node.setHeightB(AD1_HEIGHT_B);
			node.setHeight(AD1_HEIGHT);
			node.setWidth(AD1_WIDTH);
			node.setWidthB(AD1_WIDTH_B);
			//添加到集合中
			lsitnodes.add(node);
		}
		//将数据存在以以json格式返回 将数据存到model中request域中返回
		//页面需要的json数据为content_ad1
		model.addAttribute("ad1", JsonUtils.objectToJson(lsitnodes));
		return "index";
	}
}
