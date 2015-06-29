'use strict';

angular.module('service-testing-tool').config(['$stateProvider', function ($stateProvider) {
    $stateProvider
        .state('article_grid', {
            url: '/articles-grid',
            templateUrl: '/ui/views/articles/grid.html'
        })
        .state('article_all', {
            url: '/articles',
            templateUrl: '/ui/views/articles/list.html'
        })
        .state('article_create', {
            url: '/articles/create',
            templateUrl: '/ui/views/articles/schemaform.html'
        })
        .state('article_edit', {
            url: '/articles/:articleId/edit',
            templateUrl: '/ui/views/articles/edit.html'
        })
        .state('article_schemaform', {
            url: '/article-schemaform',
            templateUrl: '/ui/views/articles/schemaform.html'
        })
        .state('article_id', {
            url: '/articles/:articleId',
            templateUrl: '/ui/views/articles/view.html'
        });
}]);