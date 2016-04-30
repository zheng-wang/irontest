'use strict';

angular.module('iron-test').controller('DBEndpointsController', ['$scope', 'Endpoints', '$stateParams', '$state',
    'IronTestUtils',
  function($scope, Endpoints, $stateParams, $state, IronTestUtils) {
    $scope.create = function(isValid) {
      if (isValid) {
        var endpoint = new Endpoints({
          environment: { id: $stateParams.environmentId },
          name: this.name,
          type: 'DB',
          description: this.description,
          url: this.jdbcURL,
          username: this.username,
          password: this.password
        });
        endpoint.$save(function(response) {
          $state.go('environment_edit', {environmentId: response.environment.id});
        }, function(response) {
          IronTestUtils.openErrorHTTPResponseModal(response);
        });
      } else {
        $scope.submitted = true;
      }
    };
  }
]);
