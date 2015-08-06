/**
 * 
 */
(function (){
	
	var movie = {title: "Toy Story", tags:["fantasy", "pixar", "animation"]};
	
	var app = angular.module('movierecommender', []);
	app.controller('RecommenderController', function(){
		this.item = movie;
		
	});
	
	app.controller('MovieList', function($scope, $http){
		var self = this;
		$scope.movies = [];

		$scope.movies = [
		                 {
		                     "id": "1",
		                     "title": "Local Story (1995)",
		                     "like": false,
		                     "tags":["fantasy", "pixar", "animation"]
		                   },
		                   {
		                     "id": "2",
		                     "title": "Jumanji (1995)",
		                     "like": false,
		                     "tags":["fantasy", "pixar", "animation"]
		                   },
		                   {
		                     "id": "3",
		                     "title": "Grumpier Old Men (1995)",
		                     "like": false,
		                     "tags":["fantasy", "pixar", "animation"]
		                   }];
		$http.get('/movies').success(function(data){
			$scope.movies = data;
		});
		$scope.likeClick = function(movie){
			$scope.like = true;
			$http.post('/like', {movieid:movie.id}).
			success(function(data, status, headers, config) {
				console.log("send like in movielistCtrl");
				  });
				$scope.userlike = "test";
			};
	
	});
	app.controller('Tag', function($scope){
		$scope.tag = "";
		$scope.addedtag = "hans";
		$scope.addTag = function(movie, tag){
			if(movie.tags.indexOf(tag) < 0){
			movie.tags.push(tag);
		}
			console.log("add tag: " + $scope.addedtag );
			$scope.addedtag = "";
		};
	});
	
	app.controller('Movie', function($scope, $http){
		$scope.likeClick = function(movie){
			$scope.like = true;
			$http.post('/like', {movieid:movie.id}).
			  success(function(data, status, headers, config) {
				  console.log("send like in MovieCtrl");
			  });
			$scope.userlike = "test";
		}
	});
	
	app.controller('Recommendations',function($scope, $http){
		$http.get('/topn').success(function(data){
			$scope.recommendations = data;
		});
	});
})();