'use strict';

angular.module('irontest').config(['$stateProvider', function ($stateProvider) {
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
        .state('article_create2', {
            url: '/articles/create2',
            templateUrl: '/ui/views/articles/create.html'
        })
        .state('article_create3', {
            url: '/articles/create3',
            templateUrl: '/ui/views/articles/create_edit.html'
        })
        .state('article_edit', {
            url: '/articles/:articleId/edit',
            templateUrl: '/ui/views/articles/schemaform.html'
        })
        .state('article_edit2', {
            url: '/articles/:articleId/edit2',
            templateUrl: '/ui/views/articles/edit.html'
        })
        .state('article_edit3', {
            url: '/articles/:articleId/edit3',
            templateUrl: '/ui/views/articles/create_edit.html'
        })
        .state('article_id', {
            url: '/articles/:articleId',
            templateUrl: '/ui/views/articles/view.html'
        });
}]);