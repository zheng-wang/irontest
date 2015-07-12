'use strict';

angular.module('service-testing-tool').controller('TeststepsController', ['$scope', 'Teststeps', '$stateParams',
  '$state', 'uiGridConstants', '$http', '_',
  function($scope, Teststeps, $stateParams, $state, uiGridConstants, $http, _) {
    $scope.saveSuccessful = null;

    $scope.update = function(isValid) {
      if (isValid) {
        $scope.teststep.$update(function(response) {
          $scope.saveSuccessful = true;
          $scope.teststep = response;
        }, function(error) {
          $scope.savingErrorMessage = error.data.message;
          $scope.saveSuccessful = false;
        });
      } else {
        $scope.submitted = true;
      }
    };

    $scope.loadWsdl = function() {
      $http
        .get('api/wsdls/anywsdl/operations', {
          params: {
            wsdlUrl: $scope.wsdlUrl
          }
        })
        .success(function(data, status) {
          $scope.wsdlBindings = data;
          $scope.wsdlBinding = $scope.wsdlBindings[0];
          $scope.wsdlOperations = $scope.wsdlBindings[0].operations;
          $scope.wsdlOperation = $scope.wsdlOperations[0];
        })
        .error(function(data, status) {
          alert('Error');
        });
    }

    $scope.refreshOperations = function() {
      $scope.wsdlOperations = _.findWhere($scope.wsdlBindings, { name: $scope.wsdlBinding.name }).operations;
      $scope.wsdlOperation = $scope.wsdlOperations[0];
    }

    $scope.create = function(isValid) {
      if (isValid) {
        var teststep = new Teststeps({
          testcaseId: $stateParams.testcaseId,
          name: this.name,
          description: this.description,
          wsdlUrl: this.wsdlUrl,
          wsdlBindingName: this.wsdlBinding.name,
          wsdlOperationName: this.wsdlOperation
        });
        teststep.$save(function(response) {
          $state.go('teststep_edit', {testcaseId: response.testcaseId, teststepId: response.id});
        }, function(error) {
          alert('Error');
        });
      } else {
        $scope.submitted = true;
      }
    };

    $scope.remove = function(teststep) {
      teststep.$remove(function(response) {
        $state.go('testcase_edit', {testcaseId: $stateParams.testcaseId});
      }, function(error) {
        alert('Error');
      });
    };

    $scope.find = function() {
      $scope.columnDefs = [
        {
          name: 'name', width: 200, minWidth: 100,
          sort: {
            direction: uiGridConstants.ASC,
            priority: 1
          },
          cellTemplate: 'gridCellTemplate.html'
        },
        {name: 'description', width: 585, minWidth: 300}
      ];

      Teststeps.query(function(teststeps) {
        $scope.teststeps = teststeps;
      });
    };

    $scope.findOne = function() {
      Teststeps.get({
        testcaseId: $stateParams.testcaseId,
        teststepId: $stateParams.teststepId
      }, function(response) {
        $scope.teststep = response;
      });
    };
  }
]);
