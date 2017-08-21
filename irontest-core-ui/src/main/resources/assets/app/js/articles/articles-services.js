'use strict';

angular.module('irontest').factory('Articles', ['$resource',
  function($resource) {
    return $resource('api/articles/:articleId', {
      articleId: '@id'
    }, {
      update: {
        method: 'PUT'
      }
    });
  }
]);
