//  Add underscore to AngularJS
angular.module('underscore', [])
  .factory('_', ['$window', function($window) {
    return $window._; // assumes underscore has already been loaded on the page
  }]);

// Declare app level module dependencies
angular.module('irontest', ['ngResource', 'ui.router', 'ui.grid', 'ui.grid.resizeColumns', 'ui.grid.moveColumns',
    'ui.grid.pagination', 'ui.grid.edit', 'ui.grid.selection', 'ui.bootstrap', 'underscore', 'ui.grid.draggable-rows',
    'ngFileUpload'])
  .config(['$stateProvider', '$urlRouterProvider', function ($stateProvider, $urlRouterProvider) {
    // set default(home) view
    // $urlRouterProvider.otherwise('/');
    $urlRouterProvider.otherwise('/testcases');

    $stateProvider
      .state('blank', {
        url: '/blank',
        templateUrl: '/ui/views/blank.html'
      });
  }])
  .run(function ($rootScope) {
    //  When a leaf (i.e. <a>) sidebar menu item is clicked (which by design will change ui-router state),
    //  remove 'active' class from previous menu item (i.e. with the url before state transition),
    //  so that only the clicked menu item is highlighted.
    $rootScope.$on('$stateChangeStart', function () {
      var url = window.location;
      $('ul.nav a').filter(function() {
        return url.href.indexOf(this.href) === 0;
      }).removeClass('active');
    });
  });
