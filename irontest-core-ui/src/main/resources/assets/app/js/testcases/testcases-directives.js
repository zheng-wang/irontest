'use strict';

angular.module('irontest').directive('selectedStepRunReportArea', function () {
  return {
    link: function(scope, element, attrs) {
      //  use scope watch to ensure teststepGrid is completed loaded.
      scope.$watch('testcaseRun.selectedStepRunReport', function (stepRunReport) {
        if (stepRunReport) {
          var gridRect = document.getElementById('teststepGrid').getBoundingClientRect();
          var height = (window.innerHeight - gridRect.bottom) * 0.9;
          element.height(height);
        }
      })
    }
  };
});
