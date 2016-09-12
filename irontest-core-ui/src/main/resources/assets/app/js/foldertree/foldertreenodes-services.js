'use strict';

angular.module('irontest').factory('FolderTreeNodes', ['$resource',
  function($resource) {
    return $resource('api/foldertreenodes/:type.:idPerType', {
      type: '@type', idPerType: '@idPerType'
    }, {
      update: {
        method: 'PUT'
      }
    });
  }
]);
