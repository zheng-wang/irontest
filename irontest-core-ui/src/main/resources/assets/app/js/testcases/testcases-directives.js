'use strict';

angular.module('irontest').directive('selectedStepRunReportArea', function ($timeout) {
  return {
    link: function(scope, element, attrs) {
      $timeout(function() {   //  wait for teststepGrid to be completely loaded
        var gridRect = document.getElementById('teststepGrid').getBoundingClientRect();
        var height = (window.innerHeight - gridRect.bottom) * 0.9;
        element.height(height);
      });
    }
  };
});
