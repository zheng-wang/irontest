// Declare app level module dependencies
angular.module('service-testing-tool', ['ngResource', 'ui.router'])
    .config(['$stateProvider', '$urlRouterProvider', function ($stateProvider, $urlRouterProvider) {
        $urlRouterProvider.otherwise('/');

        $stateProvider
            .state('home', {
                url: '/',
                templateUrl: '/ui/views/home/home.html'
            })
}]);
