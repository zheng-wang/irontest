angular.module('irontest', ['common', 'ngSanitize', 'ui.grid', 'ui.grid.resizeColumns',
    'ui.grid.moveColumns', 'ui.grid.pagination', 'ui.grid.edit', 'ui.grid.cellNav', 'ui.grid.selection',
    'ui.grid.draggable-rows', 'ngFileUpload', 'ngJsTree'])
  .factory('authInterceptor', ['$q', '$rootScope', function($q, $rootScope) {
    return {
      responseError: function(response) {
        if (response.status === 401){
          $rootScope.logout();
        }
        return $q.reject(response);
      }
    };
  }])
  .config(['$httpProvider', function($httpProvider) {
    $httpProvider.interceptors.push('authInterceptor');
  }])
  .directive('input', function($compile) {      //  disable the default trimming on text input fields
    return {
      link(scope, element, attrs) {
        if (element.attr('type') === 'text') {
          attrs.$set('ngTrim', 'false');
        }
      }
    };
  })
  .directive('textarea', function($compile) {      //  disable the default trimming on textarea fields
    return {
      link(scope, element, attrs) {
        attrs.$set('ngTrim', 'false');
      }
    };
  })
  .run(['$rootScope', '$http', '$window', 'IronTestUtils', function($rootScope, $http, $window, IronTestUtils) {
    $rootScope.$on('pageLoaded', function(event, args) {
      //  adjust main panes height on page loaded
      var wrapperObj = document.getElementById('wrapper');
      var bannerBarObj = document.getElementById('page-top-navbar');
      var expandedSideMenuHeight = 122;
      var folderTreeObj = document.getElementById('folder-tree');
      var adjustedWrapperHeight = window.innerHeight - bannerBarObj.offsetHeight;
      var adjustedFolderTreePaneHeight = window.innerHeight - bannerBarObj.offsetHeight - expandedSideMenuHeight;
      angular.element(wrapperObj).height(adjustedWrapperHeight);
      angular.element(folderTreeObj).height(adjustedFolderTreePaneHeight);
    });

    //  initialize appStatus
    $rootScope.appStatus = {
      appMode: null,
      userInfo: angular.fromJson($window.localStorage.userInfo),
      isInTeamMode: function() {
        return this.appMode === 'team';
      },
      isUserAuthenticated: function() {
        return (this.userInfo);
      },
      isAdminUser: function() {
        return this.isUserAuthenticated() && this.userInfo.roles.indexOf("admin") > -1;
      },
      //  rolesAllowed is reserved for future use
      //  for now, the function involves authentication but not authorization
      isForbidden: function(rolesAllowed) {
        return this.isInTeamMode() && !this.isUserAuthenticated();
      },
      getUserId: function() {
        return this.userInfo.id;
      },
      getUsername: function() {
        return this.userInfo.username;
      }
    };

    //  keep user logged in after page refresh
    if ($rootScope.appStatus.userInfo) {
      $http.defaults.headers.common.Authorization = $rootScope.appStatus.userInfo.authHeaderValue;
    }

    //  fetch app info from server side
    $rootScope.appStatusPromise = $http.get('api/appinfo')
      .then(function successCallback(response) {
        $rootScope.appStatus.appMode = response.data.appMode;
      }, function errorCallback(response) {
        IronTestUtils.openErrorHTTPResponseModal(response);
      });

    $rootScope.logout = function() {
      //  log out only when currently logged in, to avoid unnecessary folder tree refresh
      if ($rootScope.appStatus.userInfo || $window.localStorage.userInfo ||
          $http.defaults.headers.common.Authorization) {
        $rootScope.appStatus.userInfo = null;
        $window.localStorage.removeItem("userInfo");
        delete $http.defaults.headers.common.Authorization;

        $rootScope.$emit('userLoggedOut');    //  not using broadcast, for better performance
      }
    };
  }]);
