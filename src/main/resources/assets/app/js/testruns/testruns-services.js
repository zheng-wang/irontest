'use strict';

//Testruns service used for testruns REST endpoint
angular.module('service-testing-tool').factory('Testruns', ['$resource',
  function($resource) {
    return $resource('api/testruns/:testrunId', {
      testrunId: '@id'
    });
  }
]);
