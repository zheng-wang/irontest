'use strict';

angular.module('irontest').controller('UDPValueTextareaEditorModalController', ['$scope', 'udp',
  function($scope, udp) {
    $scope.udp = udp;
  }
]);
