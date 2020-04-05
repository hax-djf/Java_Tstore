package com.taotao.service.Imp;

import java.util.Date;

import java.util.List;

import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;
import org.springframework.stereotype.Service;

import com.alibaba.druid.util.StringUtils;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.github.pagehelper.StringUtil;
import com.taotao.common.EasyUIDataGridResult;
import com.taotao.common.pojo.TaotaoResult;
import com.taotao.common.util.IDUtils;
import com.taotao.common.util.JsonUtils;
import com.taotao.mapper.TbItemDescMapper;
import com.taotao.mapper.TbItemMapper;
import com.taotao.mapper.TbItemParamMapper;
import com.taotao.pojo.TbItem;
import com.taotao.pojo.TbItemDesc;
import com.taotao.pojo.TbItemExample;
import com.taotao.pojo.TbItemExample.Criteria;
import com.taotao.pojo.TbItemParam;
import com.taotao.service.ItemListService;
import com.taotao.service.manager.jedis.JedisClient;

@Service
public class ItemListServiceImp implements ItemListService {
	@Autowired
	private TbItemMapper itemmapper;
	@Autowired
	private TbItemDescMapper itemDescmapper;
	
	@Autowired 
	private JedisClient jedisClient;
	@Value("${ITEM_INFO_KEY}")
	private String ITEM_INFO_KEY;
	
	@Value("${ITEM_INFO_KEY_EXPIRE}")
	private Integer ITEM_INFO_KEY_EXPIRE;
	@Autowired
	private JmsTemplate jmsTemplate; //发送消息模板
	
	@Autowired
	private Destination topicDestination;//订阅形式
	/**
	 * page 当前页 第一页 第二页
	 * rows 每一个查询的数据多少
	 */
	@Autowired
	private TbItemParamMapper tbItemParamMapper;
	
	@Override
	public EasyUIDataGridResult getItemListpage(int page, int rows) {
		//分页设置 只会影响下面的紧挨着的第一个sql操作
		PageHelper.startPage(page, rows);
		//进行数据查询
		TbItemExample example=new TbItemExample();
		//这个TbItemExample 可以设置条件 默认不设置的话表示的就是查询所有的数据
		List<TbItem> list = this.itemmapper.selectByExample(example);
		//将数据装成pageinfo 取分页信息
		/*PageInfo<TbItem> 不是一个list而是一个使用Page<E>,这个对象并不存在与web层 
		 所在在web层中反序列化时候会找不到这个类,会进行报错的操作当然可以不管
		 */
		PageInfo<TbItem> pageInfo=new PageInfo<TbItem>(list);
		//在去分页数据,将数据存储到EasyUIDataGridResult分页的包装类中
		EasyUIDataGridResult easyUIDataGridResult=new EasyUIDataGridResult();
		
		easyUIDataGridResult.setTotal((int)pageInfo.getTotal());
		easyUIDataGridResult.setRows(pageInfo.getList());
		//测试
		System.out.println(pageInfo.getTotal());
		System.out.println(easyUIDataGridResult.getTotal());
		
		return easyUIDataGridResult;
	}
	
	
	/**
	 * 进行商品数据的保存 以及商品的属性信息保存
	 * @param item 一个商品对象
	 * @param desc 商品的详情的信息 某一个字段
	 * @return
	 */
	
	public TaotaoResult saveItem(TbItem item,String desc) {
		//生成商品的id
		long itemId = IDUtils.genItemId();
		//1.补全item 的其他属性
		item.setId(itemId);
		item.setCreated(new Date());
		//1-正常，2-下架，3-删除',
		item.setStatus((byte) 1);
		item.setUpdated(item.getCreated());
		//2.插入到item表 商品的基本信息表
		itemmapper.insertSelective(item);
		//进行信息属性的补全操作（详情信息也是通过的一个类的包装）
		TbItemDesc desc2 = new TbItemDesc();
		desc2.setItemDesc(desc);
		desc2.setItemId(itemId);
		desc2.setCreated(item.getCreated());
		desc2.setUpdated(item.getCreated());
		//进行数据插入
		itemDescmapper.insert(desc2);
		
		//发送一个商品添加的消息
		jmsTemplate.send(topicDestination, new MessageCreator() {
			
			@Override
			public Message createMessage(Session session) throws JMSException {
				TextMessage textMessage = session.createTextMessage(itemId + "");
				return textMessage;
			}
		});
		//返回一个TaotaoResult的信息
		return TaotaoResult.ok();
	}


	@Override
	public TbItem getItemById(Long itemId) {
		// 添加缓存的原则是，不能够影响现在有的业务逻辑
		//查询是否缓存
		try {
			if(itemId!=null) {
			// 从缓存中查询
			String json = jedisClient.get(ITEM_INFO_KEY + ":" + itemId + ":BASE");
			if(!StringUtils.isEmpty(json)) {
				//不为空话
				jedisClient.expire(ITEM_INFO_KEY + ":" + itemId + ":BASE", ITEM_INFO_KEY_EXPIRE);
				return JsonUtils.jsonToPojo(json, TbItem.class);
			}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		//数据库查数据
		TbItem tbItem = itemmapper.selectByPrimaryKey(itemId);
		//不存数据的话,从数据库查数据然后将数据存到redis中
		try {
			//设置数据
			jedisClient.set(ITEM_INFO_KEY + ":" + itemId + ":BASE", JsonUtils.objectToJson(tbItem));
			jedisClient.expire(ITEM_INFO_KEY + ":" + itemId + ":BASE", ITEM_INFO_KEY_EXPIRE);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return tbItem;
	}


	@Override
	public TbItemDesc getItemDescById(Long itemId) {
		//和上面的查询商品技术是一样的
		// 查询缓存
		try {
			if (itemId != null) {
				// 从缓存中查询
				String jsonstring = jedisClient.get(ITEM_INFO_KEY + ":" + itemId + ":DESC");
				if (!StringUtils.isEmpty(jsonstring)) {// 不为空则直接返回
					jedisClient.expire(ITEM_INFO_KEY + ":" + itemId + ":DESC", ITEM_INFO_KEY_EXPIRE);
					return JsonUtils.jsonToPojo(jsonstring, TbItemDesc.class);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		//数据库查数据
		TbItemDesc itemDesc = itemDescmapper.selectByPrimaryKey(itemId);
		// 添加缓存
		try {
			// 注入redisclient
			if (itemDesc != null){
				jedisClient.set(ITEM_INFO_KEY + ":" + itemId + ":DESC", JsonUtils.objectToJson(itemDesc));
				jedisClient.expire(ITEM_INFO_KEY + ":" + itemId + ":DESC", ITEM_INFO_KEY_EXPIRE);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return itemDesc;
	}


	@Override
	public TaotaoResult deleteItemAndDescById(Long itemId) {
		//1.根据商品id删除商品的信息
		//注入服务
		//2.删除商品的信息
		//itemmapper.deleteByPrimaryKey(itemID);
		//3.删除商品的详情信息
		//itemDescmapper.deleteByPrimaryKey(itemID);
		
		//进行商品数据的修改下架
		TbItem tbItem=new TbItem();
		tbItem.setId(itemId);
		tbItem.setStatus((byte) 3);
		itemmapper.updateByPrimaryKeySelective(tbItem);
		//执行操作
		
		return TaotaoResult.ok();
	}


	@Override
	public TaotaoResult instock(Long itemid) {
		//进行商品数据的修改下架
		TbItem tbItem=new TbItem();
		tbItem.setId(itemid);
		tbItem.setStatus((byte) 2);
		itemmapper.updateByPrimaryKeySelective(tbItem);
		//执行操作
		
		return TaotaoResult.ok();
	}


	@Override
	public TaotaoResult reshelf(Long itemId) {
		//进行商品数据的修改上架
		TbItem tbItem=new TbItem();
		tbItem.setId(itemId);
		tbItem.setStatus((byte) 3);
		itemmapper.updateByPrimaryKeySelective(tbItem);
		//执行操作
				
		return TaotaoResult.ok();
	}

	//查询商品的参数
	@Override
	public TaotaoResult queryItem(Long itemId) {
		TbItemParam tbItemParam = tbItemParamMapper.selectByPrimaryKey(itemId);
		String json = JsonUtils.objectToJson(tbItemParam);
		return TaotaoResult.ok(json);
	}

	
	
	//第一种：在service中另外定义一个方法，先执行 插入商品的service的方法，然后在执行成功后，再发送消息。
	
	///rest/item/delete 删除商品
	///rest/item/update 修改商品
	///rest/item/instock 下架商品
	///rest/item/reshelf //上架商品
	//rest/item/query/item/desc/ //查看商品的详情
	///rest/item/param/item/query/ //查看商品的参数
	///rest/page/item-edit //进入商品的编辑的页面

	
}
