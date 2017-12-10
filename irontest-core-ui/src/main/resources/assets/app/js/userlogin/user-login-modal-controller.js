'use strict';

angular.module('irontest').controller('UserLoginModalController', ['$scope', '$uibModalInstance',
   '$http', 'IronTestUtils',
 function($scope, $uibModalInstance, $http, IronTestUtils) {
   $scope.ok = function() {
     $http
       .get('api/wsdls/' + encodeURIComponent($scope.wsdlURL) + '/bindings/' + $scope.wsdlBinding.name +
         '/operations/' + $scope.wsdlOperation)
       .then(function successCallback(operationInfo) {
         $uibModalInstance.close(operationInfo.data);
       }, function errorCallback(error) {
         IronTestUtils.openErrorHTTPResponseModal(error);
       });
   };

   $scope.cancel = function () {
     $uibModalInstance.dismiss('cancel');
   };
 }
]);
