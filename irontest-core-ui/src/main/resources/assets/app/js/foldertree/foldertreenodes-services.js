'use strict';

angular.module('irontest').factory('FolderTreeNodes', ['$resource',
  function($resource) {
    return $resource('api/foldertreenodes', {}, {
      update: {
        method: 'PUT'
      }
    });
  }
]);
