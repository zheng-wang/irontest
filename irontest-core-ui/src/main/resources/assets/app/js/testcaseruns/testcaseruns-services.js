'use strict';

angular.module('irontest').factory('TestcaseRuns', ['$resource',
  function($resource) {
    return $resource('api/testcaseruns/:testcaseRunId', {
      testcaseRunId: '@id'
    });
  }
]);
