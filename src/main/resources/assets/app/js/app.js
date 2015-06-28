// Declare app level module dependencies
angular.module('service-testing-tool', ['ngResource', 'ui.router', 'schemaForm', 'ui.grid', 'ui.grid.resizeColumns', 'ui.grid.moveColumns', 'ui.grid.pagination'])
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
        //  When a sidebar menu item is clicked (which by design will change ui-router state),
        //  remove 'active' class from all other menu items, so that the clicked menu item is highlighted.
        $rootScope.$on('$stateChangeStart', function () {
            var url = window.location;
            $('ul.nav a').filter(function() {
                return this.href == url;
            }).removeClass('active');
        });
    });
