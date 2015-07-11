'use strict';

angular.module('service-testing-tool').controller('TeststepsController', ['$scope', 'Teststeps', '$stateParams', '$state', 'uiGridConstants', '$http',
  function($scope, Teststeps, $stateParams, $state, uiGridConstants, $http) {
    $scope.saveSuccessful = null;

    $scope.update = function(isValid) {
      if (isValid) {
        var teststep = this.teststep;
        teststep.$update(function(response) {
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
          $scope.wsdlBinding = data[0];
          $scope.wsdlOperations = data[0].operations;
          $scope.wsdlOperation = data[0].operations[0];
        })
        .error(function(data, status) {
          alert('Error');
        });
    }

    $scope.create = function(isValid) {
      if (isValid) {
        var teststep = new Teststeps({
          name: this.name,
          description: this.description
        });
        teststep.$save(function(response) {
          $state.go('teststep_edit', {teststepId: response.id});
        });

        this.name = '';
        this.description = '';
      } else {
        $scope.submitted = true;
      }
    };

    $scope.remove = function(teststep) {
      teststep.$remove(function(response) {
        $state.go('teststep_all');
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
        teststepId: $stateParams.teststepId
      }, function(teststep) {
        $scope.teststep = teststep;
      });
    }
  }
]);
