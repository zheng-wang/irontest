'use strict';

angular.module('service-testing-tool').controller('ArticlesController', ['$scope', 'Articles', '$stateParams', '$location', '$state', 'uiGridConstants',
  function($scope, Articles, $stateParams, $location, $state, uiGridConstants) {
    $scope.schema = {
      type: "object",
      properties: {
        id: { type: "string" },
        title: { type: "string" },
        content: { type: "string" },
        created: { type: "string" },
        updated: { type: "string" }
      },
      "required": ["title", "content"]
    };

    $scope.form = [
      {
        key: "title",
        title: "Title",
        condition: "! article.id"
      },
      {
        key: "content",
        title: "Content"
      },
      {
        key: "created",
        title: "Created Date",
        readonly: true,
        condition: "article.id"
      },
      {
        key: "updated",
        title: "Updated Date",
        readonly: true,
        condition: "article.id"
      },
      {
        type: "actions",
        items: [
          { type: 'submit', style: 'btn-success', title: 'Save' },
          { type: 'button', style: 'btn-warning', title: 'Delete', onClick: "delete()" }
        ]
      }
    ];

    $scope.article = {};

    $scope.create = function(form) {
      $scope.$broadcast('schemaFormValidate');

      if (form.$valid) {
        var article = new Articles(this.article);
        article.$save(function(response) {
          $location.path('articles/' + response.id);
        });
      }
    };

    $scope.go = function(path) {
      $location.path(path);
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
      }
    };
  }
]);
