//控制器
	app.controller("baseController",function($scope){
		//重新加载列表 数据
		$scope.reloadList=function(){
		//切换页码
		$scope.search( $scope.paginationConf.currentPage,
		$scope.paginationConf.itemsPerPage);
		}
		//分页控件配置paginationConf
		$scope.paginationConf = {
		currentPage: 1,
		totalItems: 10,
		itemsPerPage: 10,
		perPageOptions: [10, 20, 30, 40, 50],
		onChange: function(){
		 $scope.reloadList();//重新加载
		}
		}; 
		
	$scope.searchEntity={};//定义搜索对象


	$scope.selectIds=[];//选中复选框ID
	//更新
	$scope.updateSelection = function($event,id){
		if($event.target.checked){
			$scope.selectIds.push(id);
		}else{
			var idx = $scope.selectIds.indexOf(id);//查找值得位置
			$scope.selectIds.splice(idx,1);//删除
		}
	}
	//从集合中按照 key 查询对象
	$scope.searchObjectByKey=function(list,key,keyValue){
		for(var i=0;i<list.length;i++){
			if(list[i][key]==keyValue){
				return list[i];
			}
		}
		return null;
	}
	});