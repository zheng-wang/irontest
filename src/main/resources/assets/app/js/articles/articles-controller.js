'use strict';

angular.module('service-testing-tool').controller('ArticlesController', ['$scope', 'Articles', '$stateParams',
  function($scope, Articles, $stateParams) {
    $scope.find = function() {
      Articles.query(function(articles) {
        $scope.articles = articles;
      });
    };

    $scope.findOne = function() {
      Articles.get({
        articleId: $stateParams.articleId
      }, function(article) {
        $scope.article = article;
      });
    };
  }
]);
