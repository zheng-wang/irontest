//  Add underscore to AngularJS
angular.module('underscore', [])
  .factory('_', ['$window', function($window) {
    return $window._; // assumes underscore has already been loaded on the page
  }]);

// Declare app level module dependencies
angular.module('irontest', ['ngResource', 'ngSanitize', 'ui.router', 'ui.grid', 'ui.grid.resizeColumns',
    'ui.grid.moveColumns', 'ui.grid.pagination', 'ui.grid.edit', 'ui.grid.selection', 'ui.bootstrap', 'underscore',
    'ui.grid.draggable-rows', 'ngFileUpload', 'ngJsTree'])
  .config(['$stateProvider', '$urlRouterProvider', function ($stateProvider, $urlRouterProvider) {
    // set default (home) view for the right pane
    $urlRouterProvider.otherwise('/');
  }]);
