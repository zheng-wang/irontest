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
      $stateProvider, $urlRouterProvider) {

    // set default (home) view for the right pane
    $urlRouterProvider.otherwise('/');

    $stateProvider
      .state('home', {
        url: '/',
        templateUrl: '/ui/views/blank.html'
      })
  }])
  .run(['$rootScope', '$http', '$window', 'IronTestUtils', function($rootScope, $http, $window, IronTestUtils) {
    //  initialize appStatus
    $rootScope.appStatus = {
      appMode: null,
      isInTeamMode: function() {
        return $rootScope.appStatus.appMode === 'team';
      },
      isUserAuthenticated: function() {
        return ($window.localStorage.authHeaderValue);
      },
      getUsername: function() {
        return $window.localStorage.username;
      }
    };

    //  keep user logged in after page refresh
    var authHeaderValue = $window.localStorage.authHeaderValue;
    if (authHeaderValue) {
      $http.defaults.headers.common['Authorization'] = authHeaderValue;
    }

    //  fetch app info from server side
    $rootScope.appStatusPromise = $http.get('api/appinfo')
      .then(function successCallback(response) {
        $rootScope.appStatus.appMode = response.data.appMode;
      }, function errorCallback(response) {
        IronTestUtils.openErrorHTTPResponseModal(response);
      });
  }]);
