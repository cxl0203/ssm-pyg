//品牌服务层
	app.service("brandService",function($http){
		//查询所有
		this.findAll = function(){
			return $http.get("../brand/findAll.do");
		}
		//条件查询
		this.search = function(page,rows,searchEntity){
			return $http.post('../brand/search.do?page='+page+"&rows="+rows,
					searchEntity);
		}
		//添加
		this.add = function(entity){
			return $http.post('../brand/add.do',entity);
		}
		//修改
		this.update = function(entity){
			return $http.post('../brand/update.do',entity);
		}
		//回显
		this.findOne = function(id){
			return $http.get('../brand/findOne.do?id='+id)
		}
		//删除
		this.dele = function(ids){
			return $http.get('../brand/delete.do?ids='+ids);
		}
		//下拉
		this.selectOptionList = function(){
			return $http.get("../brand/selectOptionList.do")
		}
	});