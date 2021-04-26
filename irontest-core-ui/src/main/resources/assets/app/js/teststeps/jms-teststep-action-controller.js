'use strict';

//  NOTICE:
//    The $scope here prototypically inherits from the $scope of TeststepsActionController.
//    ng-include also creates a scope.
angular.module('irontest').controller('JMSTeststepActionController', ['$scope', '$timeout', 'IronTestUtils', 'Teststeps',
  function($scope, $timeout, IronTestUtils, Teststeps) {
    $scope.requestMessageActiveTabIndex = 1;
    $scope.responseMessageActiveTabIndex = 2;
    var timer;
    $scope.steprun = {};

    var clearPreviousRunStatus = function() {
      if (timer) $timeout.cancel(timer);
      $scope.steprun = {};
    };

    $scope.destinationTypeChanged = function(isValid) {
      var teststep = $scope.teststep;
      if (teststep.otherProperties.destinationType === 'Topic') {
        teststep.action = 'Publish';
      } else {              //  destinationType is Queue
        teststep.action = null;
      }
      $scope.actionChanged(isValid);
    };

    $scope.actionChanged = function(isValid) {
      clearPreviousRunStatus();

      //  update test step immediately (no timeout)
      $scope.update(isValid);
    };

    $scope.endpointInfoIncomplete = function() {
      var endpoint = $scope.teststep.endpoint;
      var endpointOtherProperties = endpoint.otherProperties;
      return !endpointOtherProperties.jmsProvider ||
        (endpointOtherProperties.jmsProvider === 'Solace' && (!endpoint.host || !endpoint.port ||
          !endpointOtherProperties.vpn));
    };

    $scope.actionInfoIncomplete = function() {
      var teststep = $scope.teststep;
      if (!teststep.action) {
        return true;
      } else if (teststep.otherProperties.destinationType === 'Queue') {
        return !teststep.otherProperties.queueName;
      } else if (teststep.otherProperties.destinationType === 'Topic') {
        return !teststep.otherProperties.topicString;
      } else {
        return true;
      }
    };

    var _doAction = function() {
      var teststep = new Teststeps($scope.teststep);
      teststep.$run(function(basicTeststepRun) {
        $scope.steprun.response = basicTeststepRun.response;
        $scope.steprun.status = 'finished';
        timer = $timeout(function() {
          $scope.steprun.status = null;
        }, 15000);
      }, function(error) {
        $scope.steprun.status = 'failed';
        IronTestUtils.openErrorHTTPResponseModal(error);
      });
    };

    $scope.doAction = function() {
      clearPreviousRunStatus();
      $scope.steprun.status = 'ongoing';

      //  for Browse action, get queue depth first
      if ($scope.teststep.action === 'Browse') {
        $scope.steprun.messageIndex = $scope.teststep.otherProperties.browseMessageIndex;
        var checkQueueDepthStep = new Teststeps($scope.teststep);
        checkQueueDepthStep.action = 'CheckDepth';
        checkQueueDepthStep.$run(function(basicTeststepRun) {
          $scope.steprun.queueDepth = basicTeststepRun.response.queueDepth;
          _doAction();
        }, function(error) {
          $scope.steprun.status = 'failed';
          IronTestUtils.openErrorHTTPResponseModal(error);
        });
      } else {
        _doAction();
      }
    };

    $scope.requestMessagePropertiesGridOptions = {
      data: 'teststep.apiRequest.properties', enableColumnMenus: false,
      columnDefs: [
        {
          name: 'name', width: '30%', enableCellEdit: true, enableCellEditOnFocus: true,
          editableCellTemplate: 'requestPropertiesGridEditableCellTemplate.html'
        },
        {
          name: 'value', enableCellEdit: true, enableCellEditOnFocus: true,
          editableCellTemplate: 'requestPropertiesGridEditableCellTemplate.html'
        },
        {
          name: 'delete', width: 70, minWidth: 60, enableSorting: false,
          cellTemplate: 'requestPropertiesGridDeleteCellTemplate.html'
        }
      ],
      onRegisterApi: function (gridApi) {
        gridApi.edit.on.afterCellEdit($scope, function(rowEntity, colDef, newValue, oldValue){
          if (newValue !== oldValue) {
            //  update test step immediately (no timeout)
            $scope.update(true);
          }
        });
      }
    };

    $scope.createRequestProperty = function() {
      $scope.teststep.apiRequest.properties.push(
        { name: 'Name1', value: 'Value1' }
      );

      //  update test step immediately (no timeout)
      $scope.update(true);
    };

    $scope.deleteRequestProperty = function(property) {
      IronTestUtils.deleteArrayElementByProperty($scope.teststep.apiRequest.properties, '$$hashKey', property.$$hashKey);

      //  update test step immediately (no timeout)
      $scope.update(true);
    };
  }
]);
