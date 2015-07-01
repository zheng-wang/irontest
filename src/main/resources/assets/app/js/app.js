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
