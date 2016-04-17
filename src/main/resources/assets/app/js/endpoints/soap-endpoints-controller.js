'use strict';

angular.module('iron-test').controller('SOAPEndpointsController', ['$scope', 'Endpoints',
    '$location', '$stateParams', '$state', 'uiGridConstants',
  function($scope, Endpoints, $location, $stateParams, $state, uiGridConstants) {
    var timer;
    //  use object instead of primitives, so that child scope can update the values
    $scope.savingStatus = {
      saveSuccessful: null,
      savingErrorMessage: null
    };

    $scope.autoSave = function(isValid) {
      if (timer) $timeout.cancel(timer);
      timer = $timeout(function() {
        $scope.update(isValid);
      }, 2000);
    };

    $scope.create = function(isValid) {
      if (isValid) {
        var endpoint = new Endpoints({
          environmentId: $stateParams.environmentId,
          name: this.name,
          type: 'SOAP',
          description: this.description,
          url: this.soapAddress,
          username: this.username,
          password: this.password
        });
        endpoint.$save(function(response) {
          $state.go('environment_edit', {environmentId: response.environmentId});
        }, function(error) {
          alert('Error');
        });
      } else {
        $scope.submitted = true;
      }
    };

    $scope.create_update = function(form) {
      $scope.$broadcast('schemaFormValidate');

      if (form.$valid) {
        if (this.endpoint.id) {
          var endpoint = this.endpoint;
          endpoint.$update(function() {
            $scope.alerts.push({type: 'success', msg: 'The Endpoint has been updated successfully'});
          }, function(exception) {
            $scope.alerts.push({type: 'warning', msg: exception.data});
          });
        } else {
          var endpoint = new Endpoints(this.endpoint);
          endpoint.$save(function(response) {
            PageNavigation.contexts.push($scope.context);
            $state.go('endpoint_edit', {endpointId: response.id});
          }, function(exception) {
            $scope.alerts.push({type: 'warning', msg: exception.data});
          });
        }
      }
    };
  }
]);
