'use strict';

//Teststeps service used for testcases REST endpoint
angular.module('service-testing-tool').factory('Teststeps', ['$resource',
  function($resource) {
    return $resource('api/teststeps/:teststepId', {
      teststepId: '@id'
    }, {
      update: {
        method: 'PUT'
      }
    });
  }
]);
