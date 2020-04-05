package com.taotao.content.service.Imp;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.alibaba.dubbo.common.utils.StringUtils;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.taotao.common.EasyUIDataGridResult;
import com.taotao.common.pojo.TaotaoResult;
import com.taotao.common.util.JsonUtils;
import com.taotao.content.jedis.JedisClient;
import com.taotao.content.service.contentService;
import com.taotao.mapper.TbContentMapper;
import com.taotao.pojo.TbContent;
import com.taotao.pojo.TbContentExample;
import com.taotao.pojo.TbContentExample.Criteria;

@Service
public class contentServiceImp  implements contentService{
	@Autowired
	private TbContentMapper contentmapper;
	
	@Autowired
	private JedisClient JedisClient;
	
	@Value("${CONTENT_KEY}")
	private String CONTENT_KEY;
	@Override
	public EasyUIDataGridResult getcontentByCategory(long categoryId,int page, int rows) {
		//设置分页参数
		PageHelper.offsetPage(page, rows);
		//查询所有的内容信息 根据分类id
		TbContentExample example=new TbContentExample();
		Criteria createCriteria = example.createCriteria();
		createCriteria.andCategoryIdEqualTo(categoryId);
		//执行查询
		List<TbContent> listcontent = this.contentmapper.selectByExample(example);
		//返回数据
		PageInfo<TbContent> pageinfo=new PageInfo<TbContent>(listcontent);
		EasyUIDataGridResult dataGridResult=new  EasyUIDataGridResult();
		//获取分页中的信息返回到页面
		dataGridResult.setTotal((int)pageinfo.getTotal());
		dataGridResult.setRows(pageinfo.getList());
		System.out.println(pageinfo.getTotal());
		System.out.println(pageinfo.getList());
		return dataGridResult;
	}
	//进行分类类型内容添加操作
	@Override
	public TaotaoResult saveContent(TbContent content) {
		//添加商品
		//补全其他的信息
		content.setCreated(new Date());
		content.setUpdated(content.getCreated());
		this.contentmapper.insertSelective(content);
		//添加商品的时候进行数据的同步缓冲
		JedisClient.hdel(CONTENT_KEY, content.getCategoryId().toString());
		//响应客户端
		return TaotaoResult.ok();
	}
	
	//进行分类类型内容的编辑操作
	@Override
	public TaotaoResult updateContent(TbContent tbContent) {
		//编辑商品的信息是否需要进行商品的信息的更改
		tbContent.setUpdated(new Date());
		//创建时间不用更改
		this.contentmapper.updateByPrimaryKeySelective(tbContent);
		//修改信息的时候进行数据的同步
		JedisClient.hdel(CONTENT_KEY, tbContent.getCategoryId().toString());
		return TaotaoResult.ok();
	}
	
	
	//删除分类的内容信息
	@Override
	public TaotaoResult deleteContent(String ids) {
		//将ids,进行拆分操作 1,2,3,4String[]	split​(String regex)
		String[] strings = ids.split(",");
		for (String id : strings) {
			long contentid=Long.parseLong(id);
			//进行删除操作
			this.contentmapper.deleteByPrimaryKey(contentid);
		}
		//删除信息时候同步 未开发
		return new TaotaoResult();
	}
	
	
	//分类查询内容(轮播图)的显示
	@Override
	public List<TbContent> getContentList(long cid) {
		/*使用hash的数据格式存储数据来进行,这样的话,可以将数据进行分类的 新式存储起来方便与管理
		 * 1.查询分类的数据的时候,先在redis中进行数据的查找
		 * 2.如果redis中没有数据就到持久层里面进行数据的查找
		 */
		try {
			String strjson  = JedisClient.hget(CONTENT_KEY, cid +"");
			//判断是否存在数据
			if(!StringUtils.isEmpty(strjson)) {
				//存在数据将数据返回
				List<TbContent> list = JsonUtils.jsonToList(strjson, TbContent.class);
				System.out.println("jedis中存在缓存");
				return list;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		TbContentExample example=new TbContentExample();
		Criteria criteria = example.createCriteria();
		criteria.andCategoryIdEqualTo(cid);
		List<TbContent> listContent = this.contentmapper.selectByExample(example);
		//不存咋数据的话,向缓冲中添加数据
		System.out.println("jedis中不存咋缓冲");
		try {
			JedisClient.hset(CONTENT_KEY, cid+"", JsonUtils.objectToJson(listContent));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return listContent;
	}
	
	}
