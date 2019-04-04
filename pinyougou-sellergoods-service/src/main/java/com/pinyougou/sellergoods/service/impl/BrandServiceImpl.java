package com.pinyougou.sellergoods.service.impl;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.pinyougou.mapper.TbBrandMapper;
import com.pinyougou.pojo.TbBrand;
import com.pinyougou.pojo.TbBrandExample;
import com.pinyougou.sellergoods.service.BrandService;

import entity.PageResult;

@Service
public class BrandServiceImpl implements BrandService {

	@Autowired
	private TbBrandMapper brandMapper;

	/**
	 * 查询所有
	 */
	@Override
	public List<TbBrand> findAll() {
		// TODO Auto-generated method stub
		return brandMapper.selectByExample(null);
	}

	/**
	 * 分页
	 */
	@Override
	public PageResult findPage(TbBrand brand,int pageNum, int pageSize) {
		PageHelper.startPage(pageNum, pageSize);
		TbBrandExample example=new TbBrandExample();
		com.pinyougou.pojo.TbBrandExample.Criteria criteria = example.createCriteria();

		if(brand!=null){
		if(brand.getName()!=null && brand.getName().length()>0){
		criteria.andNameLike("%"+brand.getName()+"%");
		}
		if(brand.getFirstChar()!=null && brand.getFirstChar().length()>0){
		criteria.andFirstCharEqualTo(brand.getFirstChar());
		}
		}

		Page<TbBrand> page = (Page<TbBrand>) brandMapper.selectByExample(example);
		return new PageResult(page.getTotal(), page.getResult());
	}

	/**
	 * 添加
	 */
	@Override
	public void add(TbBrand brand) {
		brandMapper.insert(brand);
	}

	/**
	 * 修改
	 */
	@Override
	public void update(TbBrand brand) {
		brandMapper.updateByPrimaryKey(brand);
	}

	/**
	 * 回显
	 */
	@Override
	public TbBrand findOne(Long id) {
		return brandMapper.selectByPrimaryKey(id);
	}

	@Override
	public void delete(Long[] ids) {
		for (Long id : ids) {
			brandMapper.deleteByPrimaryKey(id);
		}
	}

	@Override
	public List<Map> selectOptionList() {

		return brandMapper.selectOptionList();
	}

}
