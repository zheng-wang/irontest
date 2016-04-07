'use strict';

angular.module('iron-test').controller('SOAPTeststepsController', ['$scope', 'Teststeps', 'Testruns',
    '$location', '$stateParams', '$state', '$http', '_', '$timeout', 'PageNavigation',
  function($scope, Teststeps, Testruns, $location, $stateParams, $state, $http, _, $timeout, PageNavigation) {
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

    $scope.loadWsdl = function() {
      if ($scope.$parent.teststep.intfaceId && $scope.$parent.teststep.intface.deftype==='WSDL') {
        $scope.$parent.teststep.wsdlUrl = $scope.$parent.teststep.intface.defurl;
      }
      $http
        .get('api/wsdls/anywsdl/operations', {
          params: {
            wsdlUrl: $scope.$parent.teststep.wsdlUrl
          }
        })
        .success(function(data, status) {
          $scope.$parent.teststep.wsdlBindings = data;
          $scope.$parent.teststep.wsdlBinding = $scope.$parent.teststep.wsdlBindings[0];
          $scope.$parent.teststep.wsdlOperations = $scope.$parent.teststep.wsdlBindings[0].operations;
          $scope.$parent.teststep.wsdlOperation = $scope.$parent.teststep.wsdlOperations[0];
        })
        .error(function(data, status) {
          alert('Error');
        });
    };

    $scope.refreshOperations = function() {
      $scope.wsdlOperations = _.findWhere($scope.wsdlBindings, { name: $scope.wsdlBinding.name }).operations;
      $scope.wsdlOperation = $scope.wsdlOperations[0];
    };

    $scope.create = function(isValid) {
      if (isValid) {
        var teststep = new Teststeps({
          testcaseId: this.teststep.testcaseId,
          name: this.teststep.name,
          description: this.teststep.description
        });
        if (this.teststep.intfaceId) {
          teststep.intfaceId = this.teststep.intfaceId;
          if (this.teststep.intface.deftype === "WSDL") {
            teststep.type = 'SOAP';
            teststep.properties = {
              wsdlUrl: this.teststep.wsdlUrl,
              wsdlBindingName: this.teststep.wsdlBinding.name,
              wsdlOperationName: this.teststep.wsdlOperation
            };
          }
        } else {
          teststep.type = 'SOAP';
          teststep.properties = {
            wsdlUrl: this.teststep.wsdlUrl,
            wsdlBindingName: this.teststep.wsdlBinding.name,
            wsdlOperationName: this.teststep.wsdlOperation
          };
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
        model: $scope.$parent.teststep,
        url: $location.path(),
        expect: expect
      };

      PageNavigation.contexts.push(context);

      $state.go(state, params);
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
          endpointId: $scope.teststep.endpointId
        };
      } else {
        testrun = {
          request: $scope.teststep.request,
          details: $scope.teststep.properties
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

    $scope.assertionsAreaLoadedCallback = function() {
      $scope.$broadcast('assertionsAreaLoaded');
    };
  }
]);
