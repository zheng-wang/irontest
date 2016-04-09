'use strict';

angular.module('iron-test').controller('DBTeststepEditController', ['$scope', 'Testruns',
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
    $scope.responseOptions = {
      enableFiltering: true,
      columnDefs: [ ]
    };

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

    $scope.createDSFieldContainAssertion = function(fieldName) {
      $scope.$broadcast('createDSFieldContainAssertion', fieldName);
    };

    $scope.evaluateDataSet = function() {
      $scope.$broadcast('evaluateDataSet', $scope.responseOptions.data);
    };

    $scope.invoke = function(teststep) {
      var testrun;
      if ($scope.$parent.teststep.endpointId) {
        testrun = {
          request: $scope.$parent.teststep.request,
          endpointId: $scope.$parent.teststep.endpointId
        };
      } else {
        testrun = {
          request: $scope.$parent.teststep.request,
          details: $scope.$parent.teststep.properties
        };
      }

      var testrunRes = new Testruns(testrun);
      testrunRes.$save(function(response) {
        $scope.tempData.soapResponse = response.response;
        $scope.responseOptions.data = response.response;
        $scope.responseOptions.columnDefs = [ ];
        if (response.response.length > 0) {
          var row = response.response[0];
          for (var key in row) {
            $scope.responseOptions.columnDefs.push({
              field: key,
              menuItems: [
                {
                  title: 'Create An Assertion',
                  icon: 'ui-grid-icon-info-circled',
                  context: $scope,
                  action: function() {
                    this.context.createDSFieldContainAssertion(this.context.col.colDef.field);
                  }
                }
              ]
            });
          }
        }
      }, function(error) {
        alert('Error');
      });
    };
  }
]);
