'use strict';

angular.module('irontest').factory('DataTable', ['$resource',
  function($resource) {
    return $resource('api/testcases/:testcaseId/datatable/:verb', {
    }, {
      addColumn: {
        method: 'POST',
        params: {
          verb: 'addColumn'
        }
      },
    });
  }
]);
