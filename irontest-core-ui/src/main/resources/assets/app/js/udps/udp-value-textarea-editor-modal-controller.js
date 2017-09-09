'use strict';

angular.module('irontest').controller('UDPValueTextareaEditorModalController', ['$scope', '$uibModalInstance',
    'udp',
  function($scope, $uibModalInstance, udp) {
    $scope.udp = udp;
  }
]);
