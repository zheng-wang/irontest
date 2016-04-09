'use strict';

angular.module('iron-test').controller('DBTeststepCreationController', ['$scope', 'Teststeps',
    '$location', '$stateParams', '$state', 'PageNavigation',
  function($scope, Teststeps, $location, $stateParams, $state, PageNavigation) {
    $scope.teststep = {};

    $scope.create = function(isValid) {
      if (isValid) {
        var teststep = new Teststeps({
          testcaseId: this.teststep.testcaseId,
          name: this.teststep.name,
          description: this.teststep.description,
          type: 'DB'
        });
        if (this.teststep.intfaceId) {
          teststep.intfaceId = this.teststep.intfaceId;
        }
        teststep.$save(function(response) {
          $state.go('teststep_edit', {testcaseId: response.testcaseId, teststepId: response.id});
        }, function(error) {
          alert('Error');
        });
      } else {
        $scope.submitted = true;
      }
    };

    $scope.goto = function(state, params, expect) {
      var context = {
        model: $scope.teststep,
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
        $scope.teststep = model;
      } else {
        if ($stateParams.teststepId) {
          // edit an existing entry
          Teststeps.get({
            testcaseId: $stateParams.testcaseId,
            teststepId: $stateParams.teststepId
          }, function (response) {
            $scope.teststep = response;
          });
        } else {
          // create a new entry
          $scope.teststep.testcaseId = $stateParams.testcaseId;
        }
      }
    };
  }
]);
