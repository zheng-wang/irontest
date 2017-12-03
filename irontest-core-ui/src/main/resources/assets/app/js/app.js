//  Add underscore to AngularJS
angular.module('underscore', [])
  .factory('_', ['$window', function($window) {
    return $window._; // assumes underscore has already been loaded on the page
  }]);

// Declare app level module dependencies
angular.module('irontest', ['ngResource', 'ngSanitize', 'ui.router', 'ui.grid', 'ui.grid.resizeColumns',
    'ui.grid.moveColumns', 'ui.grid.pagination', 'ui.grid.edit', 'ui.grid.cellNav', 'ui.grid.selection',
    'ui.grid.draggable-rows', 'ui.bootstrap', 'underscore', 'ngFileUpload', 'ngJsTree'])
  .config(['$stateProvider', '$urlRouterProvider', function(
      $stateProvider, $urlRouterProvider, $http) {

    // set default (home) view for the right pane
    $urlRouterProvider.otherwise('/');

    $stateProvider
      .state('home', {
        url: '/',
        templateUrl: '/ui/views/blank.html'
      })
  }])
  .run(['$http', 'IronTestUtils', 'AppStatus', function($http, IronTestUtils, AppStatus) {
    $http.get('api/appinfo')
      .then(function successCallback(response) {
        AppStatus.appMode = response.data.appMode;
      }, function errorCallback(response) {
        IronTestUtils.openErrorHTTPResponseModal(response);
      });
  }]);
