'use strict';

angular.module('service-testing-tool').config(['$routeProvider', function ($routeProvider) {
    $routeProvider
        .when('/articles', {
            templateUrl: '/ui/views/articles/list.html',
            controller: 'ArticlesController'})
        .when('/articles/:articleId', {
            templateUrl: '/ui/views/articles/view.html',
            controller: 'ArticlesController'});
}]);

