package com.pinyougou.solrutil;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.data.solr.core.query.Query;
import org.springframework.data.solr.core.query.SimpleQuery;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSON;
import com.pinyougou.mapper.TbItemMapper;
import com.pinyougou.pojo.TbItem;
import com.pinyougou.pojo.TbItemExample;
import com.pinyougou.pojo.TbItemExample.Criteria;

@Component
public class SolrUtil {

	@Autowired
	private TbItemMapper itemMapper;

	@Autowired
	private SolrTemplate solrTemplate;
	/**
	 * 导入商品数据
	 */
	public void importItemData() {
		TbItemExample example = new TbItemExample();
		Criteria criteria = example.createCriteria();
		criteria.andStatusEqualTo("1");// 状态已审核
		List<TbItem> itemList = itemMapper.selectByExample(example);
		System.out.println("===商品列表===");
		for (TbItem item : itemList) {
			Map specMap = JSON.parseObject(item.getSpec(), Map.class);
			item.setSpecMap(specMap);
			System.out.println(item.getId() + " " + item.getTitle() + " " + item.getPrice());
		}
		solrTemplate.saveBeans(itemList);
		solrTemplate.commit();
		System.out.println("==结束==");

	}

	/**
	 * 删除全部
	 * 
	 * @param args
	 */
	public void testDeleteAll() {
		Query query = new SimpleQuery("*:*");
		solrTemplate.delete(query);
		solrTemplate.commit();
	}


	public static void main(String[] args) {
		ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext(
				"classpath*:spring/applicationContext*.xml");
		SolrUtil solrUtil = (SolrUtil) context.getBean("solrUtil");
		// solrUtil.importItemData();
		solrUtil.testDeleteAll();
	}
}
