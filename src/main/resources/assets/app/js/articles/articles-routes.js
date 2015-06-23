'use strict';

angular.module('service-testing-tool').config(['$stateProvider', function ($stateProvider) {
    $stateProvider
        .state('article_all', {
            url: '/articles',
            templateUrl: '/ui/views/articles/list.html'
        })
        .state('article_id', {
            url: '/articles/:articleId',
            templateUrl: '/ui/views/articles/view.html'
        });
}]);