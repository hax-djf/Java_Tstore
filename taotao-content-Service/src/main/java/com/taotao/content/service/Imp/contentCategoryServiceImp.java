package com.taotao.content.service.Imp;

import java.util.ArrayList;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.taotao.common.pojo.EasyUITreeNode;
import com.taotao.common.pojo.TaotaoResult;
import com.taotao.content.service.contentCategoryService;
import com.taotao.mapper.TbContentCategoryMapper;
import com.taotao.pojo.TbContentCategory;
import com.taotao.pojo.TbContentCategoryExample;
import com.taotao.pojo.TbContentCategoryExample.Criteria;

@Service(value = "contentCategoryServiceImp")
public class contentCategoryServiceImp implements contentCategoryService {
	@Autowired
	private TbContentCategoryMapper contentCategorymapper;
	/**
	 * 根据父节点内容分类id查询所有的子分类内容
	 */
	@Override
	public List<EasyUITreeNode> getContentCategoryList(long parentId) {
		//获取到所有的分类信息默认id第一次为0
		TbContentCategoryExample example=new TbContentCategoryExample();
		Criteria createCriteria = example.createCriteria();
		createCriteria.andParentIdEqualTo(parentId);
		//执行sql
		List<TbContentCategory> selectByExample = this.contentCategorymapper.selectByExample(example);
		//将获取到子节点信息封装到EasyUITreeNode类中
		List<EasyUITreeNode> listnode=new ArrayList<EasyUITreeNode>();
		for (TbContentCategory tbContentCategory : selectByExample) {
			//得到每一个节点
			//创建EasyUITreeNode对象
			EasyUITreeNode node=new EasyUITreeNode();
			node.setId(tbContentCategory.getId());
			node.setText(tbContentCategory.getName());
			//判断是否是叶子节点是的话 状态为open 不是的话就是clsed 
			node.setState(tbContentCategory.getIsParent()?"closed":"open");
			listnode.add(node);
		}
		System.out.println(listnode);
		System.out.println(parentId);
		return listnode;
	}
	
	/**
	 * 进行节点数据的增加 这个时候的parentId就是当前点击节点的id 
	 * 查询的时候操作是根据的是当前节点id在category表中是parentId来进行查询数据的
	 */
	@Override
	public TaotaoResult addContentCategory(long parentId, String name) {
		//进行数据补全操作
		TbContentCategory category=new TbContentCategory();
		category.setParentId(parentId);
		category.setName(name);
		category.setIsParent(false);
		//排列序号，表示同级类目的展现次序，如数值相等则按名称次序排列。取值范围:大于零的整数
		category.setSortOrder(1);
		//状态。可选值:1(正常),2(删除)
		category.setStatus(1);
		category.setCreated(new Date());
		category.setUpdated(new Date());
		// c)向tb_content_category表中插入数据
		this.contentCategorymapper.insert(category);
		//判断当前节点是否父节点 如果不是话
		TbContentCategory parentPrimaryKey = this.contentCategorymapper.selectByPrimaryKey(parentId);
		if(!parentPrimaryKey.getIsParent()) {
			//如果不是的话就设置为是父节点
			parentPrimaryKey.setIsParent(true);
			//插入操作
			this.contentCategorymapper.updateByPrimaryKeySelective(parentPrimaryKey);
		}
		
		//返回taotaoresultr
		//需要进行主键信息的返回 ,将主键信息返回到页面上面
		// 5、返回TaotaoResult，其中包装TbContentCategory对象
		return TaotaoResult.ok(category);
	}
	
	/**
	 * 重命名分类信息
	 */
	@Override
	public TaotaoResult updateContebtCategory(long id, String name) {
		//update tb_category set name='name' where id ='id'
		TbContentCategory category=new TbContentCategory();
		category.setName(name);
		category.setId(id);
		this.contentCategorymapper.updateByPrimaryKeySelective(category);
		return TaotaoResult.ok();
	}
	/**
	 * 删除分类信息
	 * 需要分析删除的节点是不是子节点 
	 * 1、根据id删除记录。
		2、判断父节点下是否还有子节点，如果没有需要把父节点的isparent改为false
		3、如果删除的是父节点，子节点要级联删除。
		两种解决方案：
		1）如果判断是父节点不允许删除。
		2）递归删除。（不会推荐使用）

	 */
	@Override
	public TaotaoResult deleteContentCategory(long id) {
		//先查询删除的节点不是父节点
		TbContentCategory selectByPrimaryKey = this.contentCategorymapper.selectByPrimaryKey(id);
		//如果为true 是父节点不能删除    fasle 删除
		if(!selectByPrimaryKey.getIsParent()) {
			//删除操作
			this.contentCategorymapper.deleteByPrimaryKey(id);
			//在判断这个节点的父节点还存子节点吗?
			TbContentCategoryExample example=new TbContentCategoryExample();
			Criteria createCriteria = example.createCriteria();
			createCriteria.andParentIdEqualTo(selectByPrimaryKey.getParentId());
			if(this.contentCategorymapper.selectByExample(example)==null) {
				//表示没有节点
				//修改其为不是父节点
				TbContentCategory category=new TbContentCategory();
				//得到这个节点的父节点id
				category.setId(selectByPrimaryKey.getParentId());
				category.setIsParent(false);
				this.contentCategorymapper.updateByPrimaryKeySelective(category);
			}
			return TaotaoResult.ok();
		}
		//删除父节点
		return TaotaoResult.mesResult("删除失败");
	}

}
