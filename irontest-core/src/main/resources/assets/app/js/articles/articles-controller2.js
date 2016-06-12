'use strict';

angular.module('irontest').controller('ArticlesController2', ['$scope', 'Articles', '$stateParams', '$location', '$state', 'uiGridConstants',
  function($scope, Articles, $stateParams, $location, $state, uiGridConstants) {
    $scope.article = {};

    $scope.create_update = function(isValid) {
      if (isValid) {
        if (this.article.id) {
          var article = this.article;
          article.$update(function() {
            $state.go('article_edit3', {articleId: article.id});
          });
        } else {
          var article = new Articles(this.article);
          article.$save(function(response) {
            $state.go('article_edit3', {articleId: response.id});
          });
        }
      } else {
        $scope.submitted = true;
      }
    };

    $scope.create = function(isValid) {
      if (isValid) {
        var article = new Articles({
          title: this.title,
          content: this.content
        });
        article.$save(function(response) {
          $location.path('articles/' + response.id);
        });

        this.title = '';
        this.content = '';
      } else {
        $scope.submitted = true;
      }
    };

    $scope.update = function(isValid) {
      if (isValid) {
        var article = $scope.article;
        article.$update(function() {
          $location.path('articles/' + article.id);
        });
      } else {
        $scope.submitted = true;
      }
    };

    $scope.remove = function(article) {
      article.$remove(function(response) {
          $state.go($state.current, {}, {reload: true});
      });
    };

    $scope.find = function() {
      $scope.columnDefs = [
        {
          name: 'title', width: 200, minWidth: 100,
          sort: {
            direction: uiGridConstants.ASC,
            priority: 1
          },
          cellTemplate:'gridCellTemplate.html'
        },
        {name: 'content', width: 585, minWidth: 300}
      ];

      Articles.query(function(articles) {
        $scope.articles = articles;
      });
    };

    $scope.findOne = function() {
      if ($stateParams.articleId) {
        Articles.get({
          articleId: $stateParams.articleId
        }, function(article) {
          $scope.article = article;
        });
      };
    }
  }
]);
