'use strict';

angular.module('irontest').controller('ArticlesController', ['$scope', 'Articles', '$stateParams', '$state', 'uiGridConstants',
  function($scope, Articles, $stateParams, $state, uiGridConstants) {
    $scope.schema = {
      type: "object",
      properties: {
        id: { type: "string" },
        title: { type: "string" },
        content: { type: "string" }
      },
      "required": ["title", "content"]
    };

    $scope.form = [
      {
        key: "title",
        title: "Title"
      },
      {
        key: "content",
        title: "Content",
        type: "textarea"
      }
    ];

    $scope.article = {};

    $scope.create_update = function(form) {
      $scope.$broadcast('schemaFormValidate');

      if (form.$valid) {
        if (this.article.id) {
          var article = this.article;
          article.$update(function() {
            $state.go('article_edit', {articleId: article.id});
          });
        } else {
          var article = new Articles(this.article);
          article.$save(function(response) {
            $state.go('article_edit', {articleId: response.id});
          });
        }
      }
    };

    $scope.stateGo = function(state) {
      $state.go(state);
    };

    $scope.remove = function(article) {
      article.$remove(function(response) {
          $state.go('article_grid');
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
        {
          name: 'content', width: 600, minWidth: 300
        }
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
      }
    };
  }
]);
