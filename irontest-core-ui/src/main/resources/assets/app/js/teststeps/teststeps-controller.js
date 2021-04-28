'use strict';

angular.module('irontest').controller('TeststepsController', ['$scope', 'Teststeps', '$stateParams', '$timeout',
    'IronTestUtils',
  function($scope, Teststeps, $stateParams, $timeout, IronTestUtils) {
    $scope.INVOCATION_TAB_INDEX = 2;

    $scope.teststep = {
      assertions: []
    };

    $scope.teststepNewlyCreated = function() {
      return $stateParams.newlyCreated === true;
    };

    $scope.activeTabIndex = $scope.teststepNewlyCreated() ? 0 : 2;

    var timer;
    //  use object instead of primitives, so that child scope can update the values
    $scope.savingStatus = {};

    $scope.autoSave = function(isValid, successCallback) {
      $scope.savingStatus.changeUnsaved = true;
      if (timer) $timeout.cancel(timer);
      timer = $timeout(function() {
        $scope.update(isValid, successCallback);
      }, 2000);
    };

    $scope.update = function(isValid, successCallback) {
      if (timer) $timeout.cancel(timer);  //  cancel existing timer if the update function is called directly (to avoid duplicate save)
      $scope.savingStatus.changeUnsaved = true;
      if (isValid) {
        $scope.teststep.$update(function(response) {
          $scope.savingStatus.changeUnsaved = false;
          $scope.$broadcast('successfullySaved');
          $scope.teststep = new Teststeps(response.teststep);
          $scope.teststepParameters = response.parameters;
          if (successCallback) {
            successCallback();
          }
        }, function(response) {
          $scope.savingStatus.changeUnsaved = false;
          IronTestUtils.openErrorHTTPResponseModal(response);
        });
      } else {
        $scope.savingStatus.changeUnsaved = false;
        $scope.savingStatus.submitted = true;
      }
    };

    //  This function is used by child controllers to replace the teststep object in this scope.
    $scope.setTeststep = function(teststep) {
      $scope.teststep = teststep;
    };

    //  successCallback is for descendant controllers to do some extra things
    $scope.findOne = function(successCallback) {
      Teststeps.get({
        testcaseId: $stateParams.testcaseId,
        teststepId: $stateParams.teststepId
      }, function (response) {
        $scope.teststep = new Teststeps(response.teststep);
        $scope.teststepParameters = response.parameters;
        if (successCallback) {
          successCallback();
        }
      }, function(response) {
        IronTestUtils.openErrorHTTPResponseModal(response);
      });
    };

    $scope.getActionOrInvocationTabHeading = function() {
      var teststep = $scope.teststep;
      return ['JMS', 'FTP', 'MQ', 'IIB', 'AMQP', 'MQTT', 'Wait'].indexOf(teststep.type) >= 0 ? 'Action' : 'Invocation';
    }
  }
]);
