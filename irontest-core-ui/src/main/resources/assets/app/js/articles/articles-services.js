'use strict';

//Articles service used for articles REST endpoint
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
