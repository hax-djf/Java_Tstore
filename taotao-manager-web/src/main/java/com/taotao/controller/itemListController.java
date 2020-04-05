package com.taotao.controller;

import java.util.HashMap;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.taotao.common.EasyUIDataGridResult;
import com.taotao.common.pojo.TaotaoResult;
import com.taotao.common.util.JsonUtils;
import com.taotao.pojo.TbItem;
import com.taotao.service.ItemListService;
import com.taotao.utils.FastDFSClient;

@Controller
public class itemListController {
	
	@Value("${TAOTAO_IMAGE_SERVER_URL}")
	private String TAOTAO_IMAGE_SERVER_URL;//url数据的注入
	
	@Autowired
	private ItemListService itemListService;
	//限定get请求
	@RequestMapping(value="/item/list",method = RequestMethod.GET)
	@ResponseBody
	public EasyUIDataGridResult getItemListpage(int page,int rows) {
		System.out.println(page+rows);
		//json数据格式返回
		EasyUIDataGridResult itemListpage = this.itemListService.getItemListpage(page, rows);
		System.out.println(itemListpage.getTotal());
		return itemListpage;
	}
	
	
	//进行图片的上传操作
	@RequestMapping(value = "/pic/upload",produces=MediaType.TEXT_PLAIN_VALUE+";charset=utf-8")
	@ResponseBody
	public String uploadImage(MultipartFile uploadFile) {
		try {
			//获取图片上传的名字
			String oldfileName=uploadFile.getOriginalFilename();
			//截取后缀名
			String extName=oldfileName.substring(oldfileName.lastIndexOf(".")+1);
			//获取文件字节数组
			byte[] bytes = uploadFile.getBytes();
			// 3.通过fastdfsclient的方法上传图片（参数要求有 字节数组 和扩展名 不包含"."）
			FastDFSClient client = new FastDFSClient("classpath:resource/fastdfs.conf");
			
			// 返回值：group1/M00/00/00/wKgZhVk4vDqAaJ9jAA1rIuRd3Es177.jpg
			String string = client.uploadFile(bytes, extName);
			//进行数据的拼接
			//拼接成完整的URL 返回给客户端进行数据的访问
			//"http://192.168.1.101/"
			String path = 	TAOTAO_IMAGE_SERVER_URL+string;
			// 4.成功时，设置map
			Map<String, Object> map = new HashMap<>();
			//0表示成功 url 图片的访问路径 
			//将数据返会到页面上面 页面上面提交数据库的是是一个图片存储在一个访问的id地址
			map.put("error", 0);
			map.put("url", path);
			// 6.返回map
			return JsonUtils.objectToJson(map);
		} catch (Exception e) {
			// 5.失败时，设置map
			Map<String, Object> map = new HashMap<>();
			//1表示的是失败
			map.put("error", 1);
			map.put("message", "上传失败");
			return JsonUtils.objectToJson(map);
		}
	}
	
		/*
		 * 商品数据的保存 (涵盖详情)
		 *url:：/item/save
		 * @param item
		 * @param desc
		 * @return json TaotaoResult响应给客户端一个状态
		 * method:post 提交方式
		 */
		@RequestMapping(value="/item/save",method=RequestMethod.POST)
		@ResponseBody
		public TaotaoResult saveItem(TbItem item,String desc){
			//1.引入服务
			//2.注入服务
			//3.调用
			return itemListService.saveItem(item, desc);
		} 
		
		
		//@RequestMapping("/rest/page/item-edit")
		//@ResponseBody //返回json数据
//		public TaotaoResult  showPage_deit(Long itemId) {
//			String json = JsonUtils.objectToJson(itemListService.getItemById(itemId));
//			//根据这个商品id进行查询商品的数据
//			return TaotaoResult.ok(json);
//		} 
		
		@RequestMapping("/rest/item/query/item/desc/")
		@ResponseBody
		public TaotaoResult showItemDesc(Long itemId) {
			//根据商品id进行数据的查询
			String json = JsonUtils.objectToJson(itemListService.getItemDescById(itemId));
			
			return TaotaoResult.ok(json);
		}
		
		@RequestMapping("/rest/item/delete")
		@ResponseBody
		public TaotaoResult deleteById(Long itemId) {
			return itemListService.deleteItemAndDescById(itemId);
		}
		///rest/item/instock 下架
		///rest/item/reshelf 上架
		@RequestMapping("/rest/item/instock")
		@ResponseBody
		public TaotaoResult instock(Long itemId) {
			return itemListService.instock(itemId);
		}
		
		@RequestMapping("/rest/item/reshelf")
		@ResponseBody
		public TaotaoResult reshelf(Long itemId) {
			return itemListService.reshelf(itemId);
		}
		
		//提交商品
		@RequestMapping("/rest/item/update")
		@ResponseBody
		public TaotaoResult update_saveItemById(TbItem item,String desc) {
			return itemListService.saveItem(item, desc);
		}
		
		@RequestMapping("rest/item/param/item/query/")
		@ResponseBody
		public TaotaoResult querycangshu(Long itemId) {
			return itemListService.queryItem(itemId);
		}
		
}
