'use strict';

angular.module('service-testing-tool').controller('IntfacesController', ['$scope', 'Intfaces', '$stateParams', '$state', 'uiGridConstants',
  function($scope, Intfaces, $stateParams, $state, uiGridConstants) {
    $scope.schema = {
      type: "object",
      properties: {
        id: { type: "integer" },
        name: { type: "string", maxLength: 50 },
        description: { type: "string", maxLength: 500 },
        relpath: {
          type: "string",
          maxLength: 50,
          pattern: "^(\/([A-Za-z0-9]|[A-Za-z0-9][A-Za-z0-9\-\.]*[A-Za-z0-9])+)*$"
        },
        defurl: {
          type: "string",
          maxLength: 200,
          pattern: "^http:\/{2}(([a-zA-Z0-9]|[a-zA-Z0-9][a-zA-Z0-9\-]*[a-zA-Z0-9])\.)*([A-Za-z0-9]|[A-Za-z0-9][A-Za-z0-9\-]*[A-Za-z0-9])(\/([a-z0-9_\.-])+)*\/?[A-Za-z0-9]|[A-Za-z0-9][A-Za-z0-9\-\.]*[A-Za-z0-9]$"
        }
      },
      "required": ["name", "description", "defurl"]
    };

    $scope.form = [
      {
        key: "name",
        title: "Name",
        validationMessage: "The Name is required and should be less than 50 characters"
      },
      {
        key: "description",
        title: "Description",
        type: "textarea",
        validationMessage: "The Description is required and should be less than 500 characters"
      },
      {
        key: "relpath",
        title: "Relative Path",
        validationMessage: "The Context root is required and should start with /"
      },
      {
        key: "defurl",
        title: "Interface Definition",
        validationMessage: "The Definition is required and should be at a valid URL"
      }
    ];

    $scope.intface = {};

    $scope.alerts = [];

    $scope.create_update = function(form) {
      $scope.$broadcast('schemaFormValidate');

      if (form.$valid) {
        if (this.intface.id) {
          var intface = this.intface;
          intface.$update(function() {
            $scope.alerts.push({type: 'success', msg: 'The Intface has been updated successfully'});
          }, function(exception) {
            $scope.alerts.push({type: 'warning', msg: exception.data});
          });
        } else {
          var intface = new Intfaces(this.intface);
          intface.$save(function(response) {
            $state.go('intface_edit', {intfaceId: response.id});
          }, function(exception) {
            $scope.alerts.push({type: 'warning', msg: exception.data});
          });
        }
      }
    };

    $scope.closeAlert = function(index) {
      $scope.alerts.splice(index, 1);
    };

    $scope.remove = function(intface) {
      intface.$remove(function(response) {
          $state.go('intface_all');
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
          cellTemplate:'gridCellTemplate.html'
        },
        {
          name: 'description', width: 600, minWidth: 300
        },
        {
          name: 'relpath', displayName: 'Relative Path', width: 600, minWidth: 300
        }
      ];

      Intfaces.query(function(intfaces) {
        $scope.intfaces = intfaces;
      });
    };

    $scope.findOne = function() {
      if ($stateParams.intfaceId) {
        Intfaces.get({
          intfaceId: $stateParams.intfaceId
        }, function(intface) {
          $scope.intface = intface;
        });
      }
    };
  }
]);
