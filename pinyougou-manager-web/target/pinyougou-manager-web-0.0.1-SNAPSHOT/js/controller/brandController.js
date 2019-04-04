//控制器
	app.controller("brandController",function($scope,$controller,brandService){
	$controller("baseController",{$scope:$scope});
		//查询所有
		$scope.findAll = function(){
			brandService.findAll().success(
					function(response){
						$scope.list=response;
				}
			);
		}
		
		//条件查询
		$scope.search = function(page,rows){
			brandService.search(page,rows,$scope.searchEntity).success(
					function(response){
						$scope.paginationConf.totalItems=response.total;//总记录数
						$scope.list=response.rows;//给列表变量赋值
					});
		}
		//添加
		$scope.save = function() {
			var object = null //默认添加
			if($scope.entity.id != null){
				object = brandService.update($scope.entity);//有id 则修改
			}else{
				object = brandService.add($scope.entity);
			}
			object.success(
			function(response) {
				if(response.success){
					$scope.reloadList();//重新加载
				}else{
					alert(response.message);
				}
			})
		}
		//回显
		$scope.findOne = function(id){
			brandService.findOne(id).success(
			function(response){
				$scope.entity=response;
			})
		}
	//批量删除
	$scope.dele = function(){
		brandService.dele($scope.selectIds).success(
				function(response){
					if(response.success){
						$scope.reloadList();//重新加载
			}
		})
	}
	
	});