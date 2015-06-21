'use strict';

angular.module('service-testing-tool').controller('ArticlesController', ['$scope', 'Articles', '$routeParams',
  function($scope, Articles, $routeParams) {
    $scope.find = function() {
      Articles.query(function(articles) {
        $scope.articles = articles;
      });
    };

    $scope.findOne = function() {
      Articles.get({
        articleId: $routeParams.articleId
      }, function(article) {
        $scope.article = article;
      });
    };
  }
]);
