'use strict';

angular.module('irontest').controller('TeststepRunReportModalController', ['$scope', 'stepRunReport',
  function($scope, stepRunReport) {
    $scope.stepRunReport = stepRunReport;
  }
]);
