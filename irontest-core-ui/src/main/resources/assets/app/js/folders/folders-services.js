'use strict';

angular.module('irontest').factory('Folders', ['$resource',
  function($resource) {
    return $resource('api/folders/:folderId', {
      folderId: '@id'
    }, {
      update: {
        method: 'PUT'
      }
    });
  }
]);
