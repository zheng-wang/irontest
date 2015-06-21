'use strict';

angular.module('service-testing-tool').config(['$routeProvider', function ($routeProvider) {
    $routeProvider
        .when('/', {
            templateUrl: '/ui/views/home/home.html',
            controller: 'HomeController'})
        .otherwise({redirectTo: '/'});
}]);