package com.taotao.service.Imp;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.taotao.common.pojo.EasyUITreeNode;
import com.taotao.mapper.TbItemCatMapper;
import com.taotao.mapper.TbItemMapper;
import com.taotao.pojo.TbItemCat;
import com.taotao.pojo.TbItemCatExample;
import com.taotao.pojo.TbItemCatExample.Criteria;
import com.taotao.service.ItemCatService;
@Service
public class ItemCatServiceImp implements ItemCatService {
	@Autowired
	private TbItemCatMapper tbItemMapper;
	/**
	 * 根据页面传递过来的parentId id进行子节点的数据查询 
	 * 如果是叶子节点表示的状态为open 不是叶子节点的话就是close ,并且点击状态为close的点击以后会查询子节点
	 */
	@Override
	public List<EasyUITreeNode> getItemCatListByParentId(Long parentId) {
		//注入mapper
		//创建exaplme
		TbItemCatExample catExample=new TbItemCatExample();
		Criteria criteria = catExample.createCriteria();
		criteria.andParentIdEqualTo(parentId);
		//执行查询条件
		List<TbItemCat> list = tbItemMapper.selectByExample(catExample);
		//5.转成需要的数据类型List<EasyUITreeNode> 返回给页面所有的节点
		List<EasyUITreeNode> nodes = new ArrayList<>();
		//对数据进行封装传递给页面
		for (TbItemCat tbItemCat : list) {
			EasyUITreeNode node=new EasyUITreeNode();
			node.setId(tbItemCat.getId());
			node.setText(tbItemCat.getName());
			//表示是叶子节点就是open 不是的话就是closed
			node.setState(tbItemCat.getIsParent()?"closed":"open");
			nodes.add(node);
		}
		return nodes;
	}

}
