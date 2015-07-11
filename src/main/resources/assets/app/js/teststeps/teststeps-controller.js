'use strict';

angular.module('service-testing-tool').controller('TeststepsController', ['$scope', 'Teststeps', '$stateParams', '$state', 'uiGridConstants',
  function($scope, Teststeps, $stateParams, $state, uiGridConstants) {
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
      console.log('aaaaaaaaaaaaaaaaaaaa');
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
