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
      renameColumn: {
        method: 'POST',
        params: {
          verb: 'renameColumn'
        }
      },
      addRow: {
        method: 'POST',
        params: {
          verb: 'addRow'
        }
      },
      updateStringCellValue: {
        method: 'POST',
        params: {
          verb: 'updateStringCellValue'
        }
      },
      updateEndpointCellValue: {
        method: 'POST',
        params: {
          verb: 'updateEndpointCellValue'
        }
      }
    });
  }
]);
