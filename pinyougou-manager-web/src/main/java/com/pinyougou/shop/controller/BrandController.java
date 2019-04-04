package com.pinyougou.shop.controller;

import java.util.List;
import java.util.Map;

import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.pojo.TbBrand;
import com.pinyougou.sellergoods.service.BrandService;

import entity.PageResult;
import entity.Result;

@RestController
@RequestMapping("/brand")
public class BrandController {

	@Reference
	private BrandService brandService;

	@RequestMapping("/findAll")
	public List<TbBrand> finfAll() {
		return brandService.findAll();
	}

	@RequestMapping("/search")
	public PageResult findPage(@RequestBody TbBrand brand, int page, int rows) {
		return brandService.findPage(brand, page, rows);
	}

	@RequestMapping("/add")
	public Result add(@RequestBody TbBrand brand) {

		try {
			brandService.add(brand);
			return new Result(true, "添加成功！");
		} catch (Exception e) {
			e.printStackTrace();
			return new Result(false, "添加失败！");
		}
	}

	@RequestMapping("/update")
	public Result update(@RequestBody TbBrand brand) {
		try {
			brandService.update(brand);
			return new Result(true, "修改成功！");
		} catch (Exception e) {
			e.printStackTrace();
			return new Result(false, "修改失败！");
		}
	}

	@RequestMapping("/findOne")
	public TbBrand findOne(Long id) {
		return brandService.findOne(id);
	}

	@RequestMapping("/delete")
	public Result delete(Long[] ids) {
		try {
			brandService.delete(ids);
			return new Result(true, "删除成功！");
		} catch (Exception e) {
			e.printStackTrace();
			return new Result(false, "删除失败！");
		}
	}

	@RequestMapping("/selectOptionList")
	public List<Map> selectOptionList() {
		return brandService.selectOptionList();
	}

}
