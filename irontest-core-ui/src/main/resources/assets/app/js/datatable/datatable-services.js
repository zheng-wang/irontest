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
      deleteColumn: {
        method: 'POST',
        params: {
          verb: 'deleteColumn'
        }
      },
      renameColumn: {
        method: 'POST',
        params: {
          verb: 'renameColumn'
        }
      },
      moveColumn: {
        method: 'POST',
        params: {
          verb: 'moveColumn'
        }
      },
      addRow: {
        method: 'POST',
        params: {
          verb: 'addRow'
        }
      },
      deleteRow: {
        method: 'POST',
        params: {
          verb: 'deleteRow'
        }
      },
      updateCell: {
        method: 'POST',
        params: {
          verb: 'updateCell'
        }
      }
    });
  }
]);
