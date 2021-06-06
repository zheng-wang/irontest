'use strict';

//  NOTICE:
//    The $scope here prototypically inherits from the $scope of AssertionsController.
//    ng-include also creates a scope.
angular.module('irontest').controller('JSONValidAgainstJSONSchemaController', ['$scope', 'IronTestUtils', 'Upload', '$window',
  function($scope, IronTestUtils, Upload, $window) {
    $scope.uploadJSONSchemaFile = function(file) {
      if (file) {
        var url = 'api/assertions/' + $scope.assertionsModelObj.assertion.id + '/jsonSchemaFile';
        Upload.upload({
          url: url,
          data: {file: file}
        }).then(function successCallback(response) {
          $scope.$emit('successfullySaved');
          $scope.assertionsModelObj.clearCurrentAssertionVerificationResult();
          $scope.findOne($scope.assertionsModelObj.reselectCurrentAssertionInGrid);   //  refresh the entire test step to reload the current assertion
        }, function errorCallback(response) {
          IronTestUtils.openErrorHTTPResponseModal(response);
        });
      }
    };

    $scope.downloadJSONSchemaFile = function() {
      var url = 'api/assertions/' + $scope.assertionsModelObj.assertion.id + '/jsonSchemaFile';
      $window.open(url, '_blank', '');
    };
  }
]);