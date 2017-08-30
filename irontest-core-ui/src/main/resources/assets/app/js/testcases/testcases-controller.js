'use strict';

angular.module('irontest').controller('TestcasesController', ['$scope', 'Testcases', 'Teststeps', 'TestcaseRuns',
    '$stateParams', '$state', 'uiGridConstants', '$timeout', 'IronTestUtils', '$sce',
  function($scope, Testcases, Teststeps, TestcaseRuns, $stateParams, $state, uiGridConstants, $timeout, IronTestUtils, $sce) {
    var timer;
    $scope.autoSave = function(isValid) {
      if (timer) $timeout.cancel(timer);
      timer = $timeout(function() {
        $scope.update(isValid);
      }, 2000);
    };

    $scope.PROPERTIES_TAB_INDEX = 1;
    $scope.TEST_STEPS_TAB_INDEX = 2;

    $scope.teststepGridOptions = {
      data: 'testcase.teststeps',
      enableSorting: false,
      rowTemplate: '<div grid="grid" class="ui-grid-draggable-row" draggable="true"><div ng-repeat="(colRenderIndex, col) in colContainer.renderedColumns track by col.colDef.name" class="ui-grid-cell" ng-class="{ \'ui-grid-row-header-cell\': col.isRowHeader, \'custom\': true }" ui-grid-cell></div></div>',
      columnDefs: [
        {
          name: 'sequence', displayName: 'NO.', width: 55, minWidth: 55,
          cellTemplate: 'teststepGridSequenceCellTemplate.html'
        },
        {
          name: 'name', width: 520, minWidth: 100,
          cellTemplate: 'teststepGridNameCellTemplate.html'
        },
        {name: 'type', width: 80, minWidth: 80},
        {name: 'description', width: 400, minWidth: 300},
        {
          name: 'delete', width: 100, minWidth: 80, enableSorting: false, enableFiltering: false,
          cellTemplate: 'teststepGridDeleteCellTemplate.html'
        },
        {
          name: 'result', width: 100, minWidth: 80,
          cellTemplate: 'teststepGridResultCellTemplate.html'
        }
      ],
      onRegisterApi: function (gridApi) {
        gridApi.draggableRows.on.rowDropped($scope, function (info, dropTarget) {
          var testcase = new Testcases({
            id: $scope.testcase.id,
            teststeps: [
              { sequence: info.draggedRowEntity.sequence },    //  from sequence
              { sequence: info.targetRowEntity.sequence }     //  to sequence
            ]
          });
          testcase.$moveStep(function(response) {
            $scope.$broadcast('successfullySaved');
            $scope.testcase = response;
          }, function(response) {
            IronTestUtils.openErrorHTTPResponseModal(response);
          });
        });
      }
    };

    $scope.update = function(isValid) {
      if (isValid) {
        $scope.testcase.$update(function(response) {
          $scope.$broadcast('successfullySaved');
          $scope.testcase = response;
        }, function(response) {
          IronTestUtils.openErrorHTTPResponseModal(response);
        });
      } else {
        $scope.submitted = true;
      }
    };

    /*$scope.remove = function(testcase) {
      testcase.$remove(function(response) {
        $state.go($state.current, {}, {reload: true});
      }, function(response) {
        IronTestUtils.openErrorHTTPResponseModal(response);
      });
    };*/

    $scope.run = function() {
      //  clear previous run result
      $scope.testcaseRun = null;

      var testcaseRun = new TestcaseRuns({
        testcase: { id: $scope.testcase.id }
      });
      $scope.testcaseRunStatus = 'ongoing';
      testcaseRun.$save(function(response) {
        $scope.testcaseRunStatus = 'finished';
        $scope.testcaseRun = response;
      }, function(response) {
        $scope.testcaseRunStatus = 'failed';
        IronTestUtils.openErrorHTTPResponseModal(response);
      });
    };

    $scope.showStepRunHTMLReport = function(teststepId) {
      var testcaseRun = new TestcaseRuns({
        id: $scope.testcaseRun.id
      });
      testcaseRun.$getStepRunHTMLReport({ teststepId: teststepId },
        function(response) {
          //  without $sce.trustAsHtml, ngSanitize will strip elements like <textarea>
          $scope.testcaseRun.selectedStepRunReport = $sce.trustAsHtml(response.report);
        }, function(response) {
          IronTestUtils.openErrorHTTPResponseModal(response);
        });
    };

    $scope.removeTeststep = function(teststep) {
      var teststepService = new Teststeps(teststep);
      teststepService.$remove(function(response) {
        $state.go($state.current, {}, {reload: true});
      }, function(response) {
        IronTestUtils.openErrorHTTPResponseModal(response);
      });
    };

    $scope.findOne = function() {
      $scope.activeTabIndex = $scope.TEST_STEPS_TAB_INDEX;
      Testcases.get({
        testcaseId: $stateParams.testcaseId
      }, function(testcase) {
        $scope.testcase = testcase;
      }, function(response) {
        IronTestUtils.openErrorHTTPResponseModal(response);
      });
    };

    $scope.createTeststep = function(type) {
      var teststep = new Teststeps({
        testcaseId: $stateParams.testcaseId,
        type: type,
        otherProperties: {}  //  adding this property here to avoid Jackson 'Missing property' error (http://stackoverflow.com/questions/28089484/deserialization-with-jsonsubtypes-for-no-value-missing-property-error)
      });

      teststep.$save(function(response) {
        $state.go('teststep_edit', {testcaseId: $stateParams.testcaseId, teststepId: response.id, newlyCreated: true});
      }, function(response) {
        IronTestUtils.openErrorHTTPResponseModal(response);
      });
    };
  }
]);
