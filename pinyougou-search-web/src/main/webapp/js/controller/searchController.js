app.controller("searchController",function($scope,$location,searchService){
//搜索
	$scope.search=function(){
		$scope.searchMap.pageNo = parseInt($scope.searchMap.pageNo);
			searchService.search($scope.searchMap).success(
					function(response){
						$scope.resultMap=response;//搜索返回的结果
						buildPageLabel();
					})
	}
	//关键字，分类，品牌，规格
	$scope.searchMap={'keywords':'','category':'','brand':'','spec':{},'price':'','pageNo':1,'pageSize':40,'sortField':'','sort':''};
	//添加搜索项
	$scope.addSearchItem = function(key,value){
		//如果点击的是分类或者是品牌
		if(key=='category' || key=='brand' || key=='price'){
			$scope.searchMap[key] = value;
		}else{
			$scope.searchMap.spec[key] = value;
		}
		$scope.search();//执行搜索
	}
	//移除复合搜索条件
	$scope.removeSearchItem=function(key){
		if(key=="category" || key=="brand" || key=='price'){//如果是分类或品牌
			$scope.searchMap[key]="";
		}else{//否则是规格
			delete $scope.searchMap.spec[key];//移除此属性
			}
		$scope.search();//执行搜索
		}
	//构建分页标签(totalPages 为总页数)
	buildPageLabel=function(){
		//1.新增分页栏属性
		$scope.pageLabel=[];
		//2.得到最后页码
		var maxPageNo = $scope.resultMap.totalPages;
		//3.开始页码

		//4.截止页码
		var lastPage = maxPageNo;
		$scope.firstDot = true;//前面
		$scope.lastDot = true;//后面
		//5.如果总页数大于 5 页,显示部分页码
		if($scope.resultMap.totalPages>5){
			//6.如果当前页小于等于 3
			if($scope.searchMap.pageNo<=3){
				//7.前 5 页
				lastPage = 5;
				$scope.firstDot = false;
				//8.如果当前页大于等于最大页码-2
			}else if($scope.searchMap.pageNo>=lastPage-2){
				//9.后 5 页
				firstPage = maxPageNo-4;
				$scope.lastDot = false;
			}else{
				//10.显示当前页为中心的 5 页
				firstPage = $scope.searchMap.pageNo - 2;
				lastPage = $scope.searchMap.pageNo + 2;
			}
		}else{
			$scope.firstDot = false;//前面
			$scope.lastDot = false;//后面
		}
		//11.循环产生页码标签
		for (var i = firstPage; i <= lastPage; i++) {
			$scope.pageLabel.push(i);
		}
		//12.在查询后调用此方法
	}		
	//根据页码查询
	$scope.queryByPage = function(pageNo){
		//页码验证
		if(pageNo<1 || pageNo>$scope.resultMap.totalPages){
			return ;
		}
		$scope.searchMap.pageNo = pageNo;
		$scope.search();
	}
	//判断当前页为第一页
	$scope.isTopPage = function(){
		if($scope.resultMap.pageNo ==1){
			return true;
		}else{
			return false;
		}
	}
	//判断当前页是否未最后一页
	$scope.isEndPage = function(){
		if($scope.resultMap.pageNo == $scope.resultMap.totalPages){
			return true;
		}else{
			return false;
		}
	}
	//设置排序规则
	$scope.sortSearch = function(sortField,sort){
		$scope.searchMap.sortField = sortField;
		$scope.searchMap.sort = sort;
		$scope.search();
	}
	//判断关键字是不是品牌
	$scope.keywordsIsBrand = function(){
		for (var i = 0; i < $scope.resultMap.brandList.length; i++) {
			if($scope.searchMap.keywords.indexOf($scope.resultMap.brandList[i].text)>=0){
				return true;
			}
		}
		return false;
	}
	//加载查询字符串
	$scope.loadkeywords = function(){
		$scope.searchMap.keywords = $location.search()['keywords'];
		$scope.search();
	}
})