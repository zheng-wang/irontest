'use strict';

angular.module('irontest').controller('UsersController', ['$scope', '$rootScope', 'Users', '$state', 'uiGridConstants',
    'IronTestUtils', '$uibModal',
  function($scope, $rootScope, Users, $state, uiGridConstants, IronTestUtils, $uibModal) {
    $rootScope.$on('userLoggedOut', function() {
      if ($state.current.name === 'user_all') {
        $state.go('home');
      }
    });

    $scope.userGridColumnDefs = [
      {
        name: 'username', width: 200, minWidth: 100,
        sort: {
          direction: uiGridConstants.ASC,
          priority: 1
        }
      },
      {
        name: 'delete', width: 100, minWidth: 80, enableSorting: false, enableFiltering: false,
        cellTemplate: 'userGridDeleteCellTemplate.html'
      }
    ];

    $scope.create = function() {
      //  open modal dialog
      var modalInstance = $uibModal.open({
        templateUrl: '/ui/views/users/create-user-modal.html',
        controller: 'CreateUserModalController',
        size: 'md',
        windowClass: 'create-user-modal'
      });

      //  handle result from modal dialog
      modalInstance.result.then(function closed(username) {
        var user = new Users({ username: username});
        user.$save(function(response) {
          $state.go($state.current, {}, {reload: true});
        }, function(response) {
          IronTestUtils.openErrorHTTPResponseModal(response);
        });
      }, function dismissed() {
        //  Modal dismissed. Do nothing.
      });
    };

    /*$scope.update = function(isValid) {
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
    };*/

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

    /*$scope.findOne = function() {
      Users.get({
        userId: $stateParams.userId
      }, function(user) {
        $scope.user = user;
      }, function(response) {
        IronTestUtils.openErrorHTTPResponseModal(response);
      });
    };*/
  }
]);
