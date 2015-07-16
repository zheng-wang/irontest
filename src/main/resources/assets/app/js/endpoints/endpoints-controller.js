'use strict';

angular.module('service-testing-tool').controller('EndpointsController', ['$scope', 'Endpoints', 'PageNavigation', '$location', '$stateParams', '$state', 'uiGridConstants',
  function($scope, Endpoints, PageNavigation, $location, $stateParams, $state, uiGridConstants) {
    $scope.schema = {
      type: "object",
      properties: {
        id: { type: "integer" },
        name: { type: "string", maxLength: 50 },
        description: { type: "string", maxLength: 500 },
        handler: {
          type: "string",
          maxLength: 50
        },
        details: {
          type: "array",
          items: {
            type: "object",
            properties: {
              name: { type: "string", maxLength: 50 },
              value: { type: "string", maxLength: 200 }
            }
          }
        }
        /*url: {
          type: "string",
          maxLength: 200,
          pattern: "^http:\/{2}(([a-zA-Z0-9]|[a-zA-Z0-9][a-zA-Z0-9\-]*[a-zA-Z0-9])\.)*([A-Za-z0-9]|[A-Za-z0-9][A-Za-z0-9\-]*[A-Za-z0-9])(\/([a-z0-9_\.-])+)*\/?[A-Za-z0-9]|[A-Za-z0-9][A-Za-z0-9\-\.]*[A-Za-z0-9]$"
        }*/
      },
      "required": ["name", "description", "handler"]
    };

    $scope.form = [
      {
        key: "name",
        title: "Name",
        htmlClass: 'spacer-bottom-0',
        validationMessage: "The Name is required and should be less than 50 characters"
      },
      {
        key: "description",
        title: "Description",
        htmlClass: 'spacer-bottom-0',
        type: "textarea",
        validationMessage: "The Description is required and should be less than 500 characters"
      },
      {
        key: "handler",
        title: "Handler",
        htmlClass: 'spacer-bottom-0',
        type: "select",
        titleMap: [
          {value: "DBHandler", name: "DBHandler"},
          {value: "SOAPHandler", name: "SOAPHandler"},
          {value: "MQHandler", name: "MQHandler"}
        ],
        onChange: function (modelValue, form) {
          Endpoints.getProperties({
            handlerName: modelValue
          }, function(details) {
              $scope.endpoint.details = details;
          });
        }
      }
    ];

    $scope.endpoint = {};

    $scope.alerts = [];

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

    $scope.closeAlert = function(index) {
      $scope.alerts.splice(index, 1);
    };

    $scope.remove = function(endpoint) {
      endpoint.$remove(function(response) {
          $state.go('endpoint_all');
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
          name: 'handler', width: 200, minWidth: 100
        }
      ];

      Endpoints.query(function(endpoints) {
        $scope.endpoints = endpoints;
      });
    };

    $scope.return = function() {
      PageNavigation.returns.push($scope.context.model);
      $location.path($scope.context.url);
    };

    $scope.select = function() {
      $scope.context.model.endpointId = $scope.endpoint.id;

      Endpoints.get({
        endpointId: $scope.context.model.endpointId
      }, function(endpoint) {
        $scope.context.model.endpoint = endpoint;

        PageNavigation.returns.push($scope.context.model);
        $location.path($scope.context.url);
      });
    };

    $scope.findOne = function() {
      $scope.columnDefs = [
        {
          name: 'name', displayName: 'Property Name', enableCellEdit: false, width: 200, minWidth: 100,
          sort: {
            direction: uiGridConstants.ASC,
            priority: 1
          }
        },
        {
          name: 'value', displayName: 'Property Value', width: 600, minWidth: 300
        }
      ];

      $scope.context = PageNavigation.contexts.pop();

      if ($stateParams.endpointId) {
        Endpoints.get({
          endpointId: $stateParams.endpointId
        }, function(endpoint) {
          $scope.endpoint = endpoint;
        });
      }
    };
  }
]);
