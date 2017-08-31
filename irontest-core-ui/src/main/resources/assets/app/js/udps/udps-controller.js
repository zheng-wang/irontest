'use strict';

//  NOTICE:
//    The $scope here prototypically inherits from the $scope of TestcasesController,
angular.module('irontest').controller('UDPsController', ['$scope', 'UDPs', 'IronTestUtils', '$stateParams', '$timeout',
  function($scope, UDPs, IronTestUtils, $stateParams, $timeout) {
    //  each UDP grid row uses its own timer, to avoid mutual interference for 'jumping' edits
    var timerMap = {};

    //  user defined properties of the test case
    $scope.udps = [];

    $scope.udpGridOptions = {
      data: 'udps', enableFiltering: true, enableColumnMenus: false,
      columnDefs: [
        {
          name: 'name', width: '30%', headerTooltip: 'Double click to edit', enableCellEdit: true,
          editableCellTemplate: 'udpGridNameEditableCellTemplate.html'
        },
        {
          name: 'value', headerTooltip: 'Double click to edit', enableCellEdit: true,
          editableCellTemplate: 'udpGridValueEditableCellTemplate.html'
        },
        {
          name: 'delete', width: 80, minWidth: 60, enableSorting: false, enableFiltering: false, enableCellEdit: false,
          cellTemplate: 'udpGridDeleteCellTemplate.html'
        }
      ]
    };

    $scope.findByTestcaseId = function() {
      UDPs.query({ testcaseId: $stateParams.testcaseId }, function(returnUDPs) {
        $scope.udps = returnUDPs;
      }, function(response) {
        IronTestUtils.openErrorHTTPResponseModal(response);
      });
    };

    $scope.createUDP = function() {
      var udp = new UDPs();
      udp.$save({ testcaseId: $stateParams.testcaseId }, function(returnUDP) {
        $scope.udps.push(returnUDP);
      }, function(response) {
        IronTestUtils.openErrorHTTPResponseModal(response);
      });
    };

    var udpUpdate = function(udp) {
      udp.$update(function(response) {
        $scope.$emit('successfullySaved');
      }, function(response) {
        IronTestUtils.openErrorHTTPResponseModal(response);
      });
    };

    $scope.udpAutoSave = function(udp) {
      if (timerMap[udp.id]) $timeout.cancel(timerMap[udp.id]);
      timerMap[udp.id] = $timeout(function() {
        udpUpdate(udp);
      }, 2000);
    };

    $scope.removeUDP = function(udp) {
      udp.$remove(function(response) {
        IronTestUtils.deleteArrayElementByProperty($scope.udps, 'id', udp.id);
      }, function(response) {
        IronTestUtils.openErrorHTTPResponseModal(response);
      });
    };
  }
]);
