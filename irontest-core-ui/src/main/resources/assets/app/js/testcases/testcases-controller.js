'use strict';

angular.module('irontest').controller('TestcasesController', ['$scope', 'Testcases', 'Teststeps', 'TestcaseRuns',
    '$stateParams', '$state', 'uiGridConstants', '$timeout', 'IronTestUtils', '$sce', '$window', '$uibModal',
  function($scope, Testcases, Teststeps, TestcaseRuns, $stateParams, $state, uiGridConstants, $timeout, IronTestUtils,
      $sce, $window, $uibModal) {
    $scope.BASIC_INFO_TAB_INDEX = 0;
    $scope.PROPERTIES_TAB_INDEX = 1;
    $scope.TEST_STEPS_TAB_INDEX = 2;
    $scope.DATA_TABLE_TAB_INDEX = 3;
    $scope.HTTP_STUBS_TAB_INDEX = 4;

    $scope.activeTabIndex = ($window.localStorage.lastSelectedTabOnTestcaseEditView) ?
      parseInt($window.localStorage.lastSelectedTabOnTestcaseEditView) : $scope.TEST_STEPS_TAB_INDEX;

    $scope.tabSelected = function(tabIndex) {
      //  store tab index
      $window.localStorage.lastSelectedTabOnTestcaseEditView = tabIndex;

      if (tabIndex === $scope.BASIC_INFO_TAB_INDEX) {
          $scope.handleTestcaseRunResultOutlineAreaDisplay();
      }
    };

    var timer;
    $scope.autoSave = function(isValid) {
      if (timer) $timeout.cancel(timer);
      timer = $timeout(function() {
        $scope.update(isValid);
      }, 2000);
    };

    $scope.$watch('testcase.teststeps', function() {
      if ($scope.testcase) {
        $scope.teststepGridOptions.data = $scope.testcase.teststeps;
      }
    });

    $scope.teststepGridOptions = {
      enableSorting: false, enableColumnMenus: false,
      rowTemplate: '<div grid="grid" class="ui-grid-draggable-row" draggable="true"><div ng-repeat="(colRenderIndex, col) in colContainer.renderedColumns track by col.colDef.name" class="ui-grid-cell" ng-class="{ \'ui-grid-row-header-cell\': col.isRowHeader, \'custom\': true }" ui-grid-cell></div></div>',
      columnDefs: [
        {
          name: 'sequence', displayName: 'NO.', width: 62, minWidth: 62,
          cellTemplate: 'teststepGridSequenceCellTemplate.html'
        },
        {
          name: 'name', width: '45%', minWidth: 100,
          cellTemplate: 'teststepGridNameCellTemplate.html'
        },
        { name: 'type', width: 80, minWidth: 80 },
        { name: 'description' },
        {
          name: 'delete', width: 78, minWidth: 78, enableSorting: false, enableFiltering: false,
          cellTemplate: 'teststepGridDeleteCellTemplate.html'
        },
      ],
      onRegisterApi: function (gridApi) {
        $scope.teststepGridApi = gridApi;

        gridApi.draggableRows.on.rowDropped($scope, function (info) {
          var toSequence;
          if (info.fromIndex > info.toIndex) {    // row moved up
            toSequence = $scope.testcase.teststeps[info.toIndex + 1].sequence;
          } else {                            // row moved down
            toSequence = $scope.testcase.teststeps[info.toIndex - 1].sequence;
          }

          var testcase = new Testcases({
            id: $scope.testcase.id,
            teststeps: [
              { sequence: info.draggedRowEntity.sequence },    //  from sequence
              { sequence: toSequence }     //  to sequence
            ]
          });
          testcase.$moveStep(function(response) {
            $scope.$broadcast('successfullySaved');
            $scope.testcase = response;   // this is necessary as server side will change sequence values of teststeps.
          }, function(response) {
            IronTestUtils.openErrorHTTPResponseModal(response);
          });
        });

        $scope.handleTestcaseRunResultOutlineAreaDisplay();
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

    $scope.run = function() {
      //  clear previous run result
      $scope.testcaseRun = null;

      var testcaseRun = new TestcaseRuns();
      $scope.testcaseRunStatus = 'ongoing';
      testcaseRun.$save({testcaseId: $scope.testcase.id }, function(response) {
        $scope.testcaseRunStatus = 'finished';
        $scope.testcaseRun = response;
      }, function(response) {
        $scope.testcaseRunStatus = 'failed';
        IronTestUtils.openErrorHTTPResponseModal(response);
      });
    };

    $scope.showStepRunHTMLReport = function(stepRunId) {
      TestcaseRuns.getStepRunHTMLReport({ stepRunId: stepRunId },
        function(response) {
          //  without $sce.trustAsHtml, ngSanitize will strip elements like <textarea>
          var stepRunReport = $sce.trustAsHtml(response.report);

          //  open modal dialog
          var modalInstance = $uibModal.open({
            templateUrl: '/ui/views/testcases/teststep-run-report-modal.html',
            controller: 'TeststepRunReportModalController',
            size: 'lg',
            windowClass: 'teststep-run-report-modal',
            resolve: {
              stepRunReport: function() {
                return stepRunReport;
              }
            }
          });
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

    $scope.handleTestcaseRunResultOutlineAreaDisplay = function() {
      if ($scope.testcaseRun) {
        $timeout(function() {
          var majorElementId;    //  currently, major element must be the last element under the selected tab, and the major element's bottom position must be same as the tabs area's bottom position.
          switch ($scope.activeTabIndex) {
            case $scope.BASIC_INFO_TAB_INDEX:
              majorElementId = 'description';
              break;
            case $scope.PROPERTIES_TAB_INDEX:
              majorElementId = 'testcase-udp-grid';
              break;
            case $scope.TEST_STEPS_TAB_INDEX:
              majorElementId = 'teststep-grid';
              break;
            case $scope.DATA_TABLE_TAB_INDEX:
              majorElementId = 'testcase-datatable-grid';
              break;
            case $scope.HTTP_STUBS_TAB_INDEX:
              majorElementId = 'testcase-httpstub-grid';
              break;
          }
          var pageWrapperObj = document.getElementById('page-wrapper');
          var pageWrapperHeight = pageWrapperObj.offsetHeight;
          var majorElement = document.getElementById(majorElementId);
          var majorElementOldHeight = majorElement.offsetHeight;
          var pageWrapperHeightBelowMajorElement = pageWrapperObj.getBoundingClientRect().bottom - majorElement.getBoundingClientRect().bottom;
          var testcaseRunResultOutlineAreaHeight = pageWrapperHeight * 0.33;

          //  adjust major element's height on selected tab
          var majorElementNewCSSHeight = majorElementOldHeight - (testcaseRunResultOutlineAreaHeight - pageWrapperHeightBelowMajorElement);
          if (majorElement.hasAttribute('ui-grid')) {  // the major element is a ui grid
            //  2 pixels (top and bottom borders) will be added by ui grid on top of its css height, ending up with offset height: cssHeight + 2.
            //  so subtract this 2 pixels before updating the grid's css height.
            majorElementNewCSSHeight = majorElementNewCSSHeight - 2;
          }
          majorElementNewCSSHeight = majorElementNewCSSHeight + 'px';
          $scope.styleOfMajorElementOnSelectedTab = { height: majorElementNewCSSHeight };
          //angular.element(majorElement).css('height', majorElementNewCSSHeight);  //  not using this line of code to adjust major element height, as it seems causing some flash scrollbar appearing on the wrapper element (every time a grid major element is rendered to original height first, then shortened quickly).
          //  below block of code is needed to make grid major element height adjustment effective immediately under the selected tab on $scope.testcaseRun being available
          //  tried the autoResize feature of ui grid, but it did not work properly (maybe due to the grid being under uib-tabset, but not sure),
          //  so ended up calling the ui grid handleWindowResize() function.
          $timeout(function() {
            switch ($scope.activeTabIndex) {
              case $scope.TEST_STEPS_TAB_INDEX:
                $scope.teststepGridApi.core.handleWindowResize();
                break;
              case $scope.PROPERTIES_TAB_INDEX:
              case $scope.DATA_TABLE_TAB_INDEX:
              case $scope.HTTP_STUBS_TAB_INDEX:
                $scope.$broadcast('testcaseRunResultOutlineAreaShown');
                break;
            }
          });

          //  adjust testcase run result outline area height
          angular.element(document.getElementById('testcase-run-result-outline-area')).height(testcaseRunResultOutlineAreaHeight);
        });
      }
    };
  }
]);
