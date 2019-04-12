package com.pinyougou.search.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.data.solr.core.query.Criteria;
import org.springframework.data.solr.core.query.FilterQuery;
import org.springframework.data.solr.core.query.GroupOptions;
import org.springframework.data.solr.core.query.HighlightOptions;
import org.springframework.data.solr.core.query.HighlightQuery;
import org.springframework.data.solr.core.query.Query;
import org.springframework.data.solr.core.query.SimpleFilterQuery;
import org.springframework.data.solr.core.query.SimpleHighlightQuery;
import org.springframework.data.solr.core.query.SimpleQuery;
import org.springframework.data.solr.core.query.result.GroupEntry;
import org.springframework.data.solr.core.query.result.GroupPage;
import org.springframework.data.solr.core.query.result.GroupResult;
import org.springframework.data.solr.core.query.result.HighlightEntry;
import org.springframework.data.solr.core.query.result.HighlightPage;

import com.alibaba.dubbo.config.annotation.Service;
import com.pinyougou.pojo.TbItem;
import com.pinyougou.search.service.ItemSearchService;

@Service(timeout = 3000)
public class ItemSearchServiceImpl implements ItemSearchService {

	@Autowired
	private SolrTemplate solrTemplate;

	@Override
	public Map<String, Object> search(Map searchMap) {
		// 关键字处理
		String keywords = (String) searchMap.get("keywords");
		searchMap.put("keywords", keywords.replace(" ", ""));

		Map<String, Object> map = new HashMap<>();
		// 1. 查询列表
		map.putAll(searchList(searchMap));

		// 2.根据关键字查询商品
		List<String> categoryList = searchCategoryList(searchMap);
		map.put("categoryList", categoryList);

		//3.查询品牌和规格列表
		String categoryName = (String) searchMap.get("category");
		// 有分类名称
		if (!"".equals(categoryName)) {
			map.putAll(searchBrandAndSpecList(categoryName));
		} else {
			if (categoryList.size() > 0) {
				map.putAll(searchBrandAndSpecList(categoryList.get(0)));
			}
		}
		return map;
	}
	/**
	 * 高亮搜索关键字
	 */
	private Map searchList(Map searchMap) {
		Map map = new HashMap<>();
		HighlightQuery query = new SimpleHighlightQuery();
		// 1. 设置高亮的域
		HighlightOptions highlightOptions = new HighlightOptions().addField("item_title");
		// 2. 高亮前缀
		highlightOptions.setSimplePrefix("<em style='color:red'>");
		// 3. 高亮后缀
		highlightOptions.setSimplePostfix("</em>");
		// 4. 设置高亮选项
		query.setHighlightOptions(highlightOptions);

		// 5.1.1 按照关键字查询
		Criteria criteria = new Criteria("item_keywords").is(searchMap.get("keywords"));
		query.addCriteria(criteria);
		// 1.2 按分类筛选
		if (!"".equals(searchMap.get("category"))) {
			FilterQuery filterQuery = new SimpleFilterQuery();
			Criteria filtercriteria = new Criteria("item_category").is(searchMap.get("category"));
			filterQuery.addCriteria(filtercriteria);
			query.addFilterQuery(filterQuery);
		}
		// 1.3 按品牌筛选
		if (!"".equals(searchMap.get("brand"))) {
			FilterQuery filterQuery = new SimpleFilterQuery();
			Criteria filtercriteria = new Criteria("item_brand").is(searchMap.get("brand"));
			filterQuery.addCriteria(filtercriteria);
			query.addFilterQuery(filterQuery);
		}
		// 1.4规格过滤
		if (searchMap.get("spec") != null) {
			Map<String, String> specMap = (Map) searchMap.get("spec");
			for (String key : specMap.keySet()) {
				FilterQuery filterQuery = new SimpleFilterQuery();
				Criteria filtercriteria = new Criteria("item_spec_" + key).is(specMap.get("key"));
				filterQuery.addCriteria(filtercriteria);
				query.addFilterQuery(filterQuery);
			}
		}
		// 1.5 按价格筛选
		if (!"".equals(searchMap.get("price"))) {
			String[] price = ((String) searchMap.get("price")).split("-");
			// 如果区间起点不等于 0
			if (!price[0].equals("0")) {
				FilterQuery filterQuery = new SimpleFilterQuery();
				Criteria filtercriteria = new Criteria("item_price").greaterThanEqual(price[0]);
				filterQuery.addCriteria(filtercriteria);
				query.addFilterQuery(filterQuery);
			}
			// 如果区间起点不等于 *
			if (!price[1].equals("*")) {
				FilterQuery filterQuery = new SimpleFilterQuery();
				Criteria filtercriteria = new Criteria("item_price").lessThanEqual(price[1]);
				filterQuery.addCriteria(filtercriteria);
				query.addFilterQuery(filterQuery);
			}
		}
		// 1.6分页查询
		// 提取页码
		// pageNo
		Integer pageNo = (Integer) searchMap.get("pageNo");
		if (pageNo == null) {
			// 默认第一页
			pageNo = 1;
		}
		// 每页记录数
		// pageSize
		Integer pageSize = (Integer) searchMap.get("pageSize");
		// 默认 20
		if (pageSize == null) {
			pageSize = 20;
		}
		// 从第几条记录查询
		// setOffset
		query.setOffset((pageNo - 1) * pageSize);
		// setRows
		query.setRows(pageSize);

		// 1.7排序
		String sortValue = (String) searchMap.get("sort");// ASC DESC
		String sortField = (String) searchMap.get("sortField");// 排序字段
		if (sortValue != null && !sortField.equals("")) {
			if (sortValue.equals("ASC")) {
				Sort sort = new Sort(Sort.Direction.ASC, "item_" + sortField);
				query.addSort(sort);
			}
			if (sortValue.equals("DESC")) {
				Sort sort = new Sort(Sort.Direction.DESC, "item_" + sortField);
				query.addSort(sort);
			}
		}
		// ************获取高亮结果集***************
		HighlightPage<TbItem> page = solrTemplate.queryForHighlightPage(query, TbItem.class);
		// 6. 循环高亮入口集合
		for (HighlightEntry<TbItem> h : page.getHighlighted()) {
			// 7. 获取原实体类
			TbItem item = h.getEntity();
			// 8. 设置高亮的结果
			if (h.getHighlights().size() > 0 && h.getHighlights().get(0).getSnipplets().size() > 0) {
				item.setTitle(h.getHighlights().get(0).getSnipplets().get(0));
			}
		}
		map.put("rows", page.getContent());
		map.put("totalPages", page.getTotalPages());// 返回总页数
		map.put("total", page.getTotalElements());// 返回总记录数
		return map;
	}

	/**
	 * 查询分类列表
	 */
	private List searchCategoryList(Map searchMap) {
		List<String> list = new ArrayList<>();
		Query query = new SimpleQuery();
		// 1.按照关键字查询
		Criteria criteria = new Criteria("item_keywords").is(searchMap.get("keywords"));
		query.addCriteria(criteria);
		// 2.设置分组选项
		GroupOptions groupOptions = new GroupOptions().addGroupByField("item_category");
		query.setGroupOptions(groupOptions);
		// 3.得到分组页
		GroupPage<TbItem> page = solrTemplate.queryForGroupPage(query, TbItem.class);
		// 4.根据列得到分组结果集
		GroupResult<TbItem> result = page.getGroupResult("item_category");
		// 5.得到分组结果入口页
		Page<GroupEntry<TbItem>> entries = result.getGroupEntries();
		// 6.得到分组入口集合
		List<GroupEntry<TbItem>> content = entries.getContent();
		// 7.将分组结果的名称封装到返回值中
		for (GroupEntry<TbItem> entry : content) {
			list.add(entry.getGroupValue());
		}
		return list;
	}

	@Autowired
	private RedisTemplate redisTemplate;
	/**
	 * 查询品牌和规格列表
	 */
	private Map searchBrandAndSpecList(String category) {
		Map map = new HashMap<>();
		// 获取模板 ID
		Long typeId = (Long) redisTemplate.boundHashOps("itemCat").get(category);
		if (typeId != null) {
			// 根据模板 ID 查询品牌列表brandList
			List brandList = (List) redisTemplate.boundHashOps("brandList").get(typeId);
			map.put("brandList", brandList);// 返回值添加品牌列表

			// 根据模板 ID 查询规格列表specList
			List specList = (List) redisTemplate.boundHashOps("specList").get(typeId);
			map.put("specList", specList);// 返回值添加品牌列表
		}
		return map;
	}

	@Override
	public void importList(List list) {
		solrTemplate.saveBeans(list);
		solrTemplate.commit();
	}

	@Override
	public void deleteByGoodsIds(List goodsIdList) {
		System.out.println("删除商品 ID" + goodsIdList);
		Query query = new SimpleQuery();
		Criteria criteria = new Criteria("item_goodsid").in(goodsIdList);
		query.addCriteria(criteria);
		solrTemplate.delete(query);
		solrTemplate.commit();
	}
}

