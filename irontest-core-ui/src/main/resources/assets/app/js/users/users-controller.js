'use strict';

angular.module('irontest').controller('UsersController', ['$scope', 'Users',
    '$stateParams', '$state', 'uiGridConstants', '$timeout', 'IronTestUtils',
  function($scope, Users, $stateParams, $state, uiGridConstants, $timeout, IronTestUtils) {

    var timer;
    $scope.autoSave = function(isValid) {
      if (timer) $timeout.cancel(timer);
      timer = $timeout(function() {
        $scope.update(isValid);
      }, 2000);
    };

    $scope.userGridColumnDefs = [
      {
        name: 'username', width: 200, minWidth: 100,
        sort: {
          direction: uiGridConstants.ASC,
          priority: 1
        },
        cellTemplate:'userGridUsernameCellTemplate.html'
      },
      {
        name: 'delete', width: 100, minWidth: 80, enableSorting: false, enableFiltering: false,
        cellTemplate: 'userGridDeleteCellTemplate.html'
      }
    ];

    $scope.create = function() {
      var user = new Users();
      user.$save(function(response) {
        $state.go('user_edit', {userId: response.id, newlyCreated: true});
      }, function(response) {
        IronTestUtils.openErrorHTTPResponseModal(response);
      });
    };

    $scope.update = function(isValid) {
      if (isValid) {
        $scope.user.$update(function(response) {
          $scope.$broadcast('successfullySaved');
          $scope.user = response;
        }, function(response) {
          IronTestUtils.openErrorHTTPResponseModal(response);
        });
      } else {
        $scope.submitted = true;
      }
    };

    $scope.remove = function(user) {
      user.$remove(function(response) {
        $state.go($state.current, {}, {reload: true});
      }, function(response) {
        IronTestUtils.openErrorHTTPResponseModal(response);
      });
    };

    $scope.find = function() {
      Users.query(function(users) {
        $scope.users = users;
      }, function(response) {
        IronTestUtils.openErrorHTTPResponseModal(response);
      });
    };

    $scope.userNewlyCreated = function() {
      return $stateParams.newlyCreated === true;
    };

    $scope.findOne = function() {
      Users.get({
        userId: $stateParams.userId
      }, function(user) {
        $scope.user = user;
      }, function(response) {
        IronTestUtils.openErrorHTTPResponseModal(response);
      });
    };
  }
]);
