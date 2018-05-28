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
}).directive('testcaseRunOutlineArea', function ($timeout) {
  return {
    link: function(scope, element, attrs) {
      $timeout(function() {
        var testcaseRunOutlineAreaHeight = window.innerHeight * 0.4;
        var tabsAreaObj = document.getElementById('tabs-area');
        var tabsAreaCurrentHeight = tabsAreaObj.offsetHeight.
        element.height(testcaseRunOutlineAreaHeight);
        angular.element(tabsAreaObj).height(tabsAreaCurrentHeight - testcaseRunOutlineAreaHeight);
      });
    }
  };
});
