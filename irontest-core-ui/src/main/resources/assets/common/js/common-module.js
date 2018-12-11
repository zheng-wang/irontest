angular.module('common', ['ngResource', 'ui.router', 'ui.bootstrap'])
  .config(['$stateProvider', '$urlRouterProvider', function($stateProvider, $urlRouterProvider) {

    // set default (home) view for the right pane
    $urlRouterProvider.otherwise('/');

    $stateProvider
      .state('home', {
        url: '/',
        templateUrl: '/ui/common/views/blank.html'
      })
  }]);