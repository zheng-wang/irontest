'use strict';

//  NOTICE:
//    The $scope here prototypically inherits from the $scope of AssertionsController.
//    ng-include also creates a scope.
angular.module('irontest').controller('XMLValidAgainstXSDController', ['$scope', 'IronTestUtils', 'Upload',
  function($scope, IronTestUtils, Upload) {
    $scope.uploadXSDFile = function(file) {
      if (file) {
        var url = 'api/assertions/' + $scope.assertionsModelObj.assertion.id + '/xsdFile';
        Upload.upload({
          url: url,
          data: {file: file}
        }).then(function successCallback(response) {
          $scope.$emit('successfullySaved');
        }, function errorCallback(response) {
          IronTestUtils.openErrorHTTPResponseModal(response);
        });
      }
    };
  }
]);