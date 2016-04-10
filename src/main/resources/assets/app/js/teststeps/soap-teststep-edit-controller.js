'use strict';

angular.module('iron-test').controller('SOAPTeststepEditController', ['$scope', 'Testruns',
    '$location', '$state', '$timeout', 'PageNavigation',
  function($scope, Testruns, $location, $state, $timeout, PageNavigation) {
    var timer;
    //  use object instead of primitives, so that child scope can update the values
    $scope.savingStatus = {
      saveSuccessful: null,
      savingErrorMessage: null
    };
    $scope.tempData = {};
    $scope.showAssertionsArea = false;

    $scope.update = function(isValid) {
      if (isValid) {
        $scope.$parent.teststep.$update(function(response) {
          $scope.savingStatus.saveSuccessful = true;
          $scope.$parent.teststep = response;
        }, function(error) {
          $scope.savingStatus.savingErrorMessage = error.data.message;
          $scope.savingStatus.saveSuccessful = false;
        });
      } else {
        $scope.savingStatus.submitted = true;
      }
    };

    $scope.autoSave = function(isValid) {
      if (timer) $timeout.cancel(timer);
      timer = $timeout(function() {
        $scope.update(isValid);
      }, 2000);
    };

    $scope.goto = function(state, params, expect) {
      var context = {
        model: $scope.$parent.teststep,
        url: $location.path(),
        expect: expect
      };

      PageNavigation.contexts.push(context);

      $state.go(state, params);
    };

    $scope.findOne = function() {
      // entry returned from other pages
      var model = PageNavigation.returns.pop();
      if (model) {
        $scope.$parent.teststep = model;
        $scope.autoSave(true);
      }
    };

    $scope.invoke = function(teststep) {
      var testrun;
      if ($scope.$parent.teststep.endpointId) {
        testrun = {
          request: $scope.$parent.teststep.request,
          endpointId: $scope.teststep.endpointId
        };
      } else {
        testrun = {
          request: $scope.teststep.request,
          details: $scope.teststep.properties
        };
      }

      var testrunRes = new Testruns(testrun);
      testrunRes.$save(function(response) {
        $scope.tempData.soapResponse = response.response;
      }, function(error) {
        alert('Error');
      });
    };

    $scope.assertionsAreaLoadedCallback = function() {
      $scope.$broadcast('assertionsAreaLoaded');
    };
  }
]);
