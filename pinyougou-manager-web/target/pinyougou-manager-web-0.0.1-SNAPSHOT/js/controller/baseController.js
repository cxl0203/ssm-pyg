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
	//提取 json 字符串数据中某个属性，返回拼接字符串 逗号分隔
	$scope.jsonToString=function(jsonString,key){
		var json=JSON.parse(jsonString);//将 json 字符串转换为 json 对象
		var value="";
		for(var i=0;i<json.length;i++){
				if(i>0){
					value+=","
				}
				value+=json[i][key];
		}
		return value;
	}
});