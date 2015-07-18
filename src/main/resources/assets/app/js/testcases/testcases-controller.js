'use strict';

angular.module('service-testing-tool').controller('TestcasesController', ['$scope', 'Testcases', 'Teststeps', '$stateParams', '$state', 'uiGridConstants', '$timeout',
  function($scope, Testcases, Teststeps, $stateParams, $state, uiGridConstants, $timeout) {
    $scope.columnDefs = [
      {
        name: 'name', width: 200, minWidth: 100,
        sort: {
          direction: uiGridConstants.ASC,
          priority: 1
        },
        cellTemplate: 'testcaseGridNameCellTemplate.html'
      },
      {name: 'description', width: 585, minWidth: 300},
      {name: 'delete', width: 80, minWidth: 80,
        cellTemplate: 'testcaseGridDeleteCellTemplate.html'
      }
    ];

    $scope.teststepsColumnDefs = [
      {
        name: 'name', width: 200, minWidth: 100,
        sort: {
          direction: uiGridConstants.ASC,
          priority: 1
        },
        cellTemplate: 'teststepGridNameCellTemplate.html'
      },
      {name: 'description', width: 485, minWidth: 300},
      {name: 'intfaceName', width: 200, minWidth: 100},
      {name: 'delete', width: 80, minWidth: 80,
        cellTemplate: 'teststepGridDeleteCellTemplate.html'
      }
    ];

    $scope.saveSuccessful = null;

    $scope.update = function(isValid) {
      if (isValid) {
        $scope.testcase.$update(function(response) {
          $scope.saveSuccessful = true;
          $scope.testcase = response;
        }, function(error) {
          $scope.savingErrorMessage = error.data.message;
          $scope.saveSuccessful = false;
        });
      } else {
        $scope.submitted = true;
      }
    };

    var timer;
    $scope.autoSave = function(isValid) {
      if (timer) $timeout.cancel(timer);
      timer = $timeout(function() {
        $scope.update(isValid);
      }, 2000);
    }

    $scope.create = function(isValid) {
      if (isValid) {
        var testcase = new Testcases({
          name: this.name,
          description: this.description
        });
        testcase.$save(function(response) {
          $state.go('testcase_edit', {testcaseId: response.id});
        });
      } else {
        $scope.submitted = true;
      }
    };

    $scope.remove = function(testcase) {
      var testcaseService = new Testcases(testcase);
      testcaseService.$remove(function(response) {
        $state.go($state.current, {}, {reload: true});
      });
    };

    $scope.removeTeststep = function(teststep) {
      var teststepService = new Teststeps(teststep);
      teststepService.$remove(function(response) {
        $state.go($state.current, {}, {reload: true});
      }, function(error) {
        alert('Error');
      });
    };

    $scope.find = function() {
      Testcases.query(function(testcases) {
        $scope.testcases = testcases;
      });
    };

    $scope.findOne = function() {
      Testcases.get({
        testcaseId: $stateParams.testcaseId
      }, function(testcase) {
        $scope.testcase = testcase;
      });
    }
  }
]);
