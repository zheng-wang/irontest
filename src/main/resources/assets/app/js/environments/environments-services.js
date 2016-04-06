'use strict';

//Environments service used for environments REST environment
angular.module('iron-test').factory('Environments', ['$resource',
  function($resource) {
    return $resource('api/environments/:environmentId', {
      environmentId: '@id'
    }, {
      update: {
        method: 'PUT'
      }
    });
  }
]).factory('EnvEntries', ['$resource',
  function($resource) {
    return $resource('api/enventries/:enventryId', {
      enventryId: '@id'
    }, {
      update: {
        method: 'PUT'
      },
      queryByEnv: {
        method: 'GET',
        url: '/api/enventries/env/:environmentId',
        params: {environmentId: '@id'},
        isArray: true
      }
    });
  }
]);
