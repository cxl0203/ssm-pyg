package com.pinyougou.sellergoods.service;

import java.util.List;
import java.util.Map;

import com.pinyougou.pojo.TbBrand;

import entity.PageResult;

public interface BrandService {

	// 查询所有
	public List<TbBrand> findAll();


	// 分页条件查询
	public PageResult findPage(TbBrand brand, int pageNum, int pageSize);

	// 添加
	public void add(TbBrand brand);

	// 修改
	public void update(TbBrand brand);

	// 回显修改的数据
	public TbBrand findOne(Long id);

	// 批量删除
	public void delete(Long[] ids);

	// 品牌下拉框
	List<Map> selectOptionList();
}
