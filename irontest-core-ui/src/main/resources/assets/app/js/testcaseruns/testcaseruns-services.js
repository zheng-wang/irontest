'use strict';

angular.module('irontest').factory('TestcaseRuns', ['$resource',
  function($resource) {
    return $resource('api/testcaseruns/:testcaseRunId', {
      testcaseRunId: '@id'
    }, {
      getStepRunHTMLReport: {
        url: 'api/teststepruns/:stepRunId/htmlreport',
        method: 'GET',
        transformResponse: function (data) {  //  avoid angularjs turning response html into array of chars
          return { report: data };
        }
      }
    });
  }
]);
