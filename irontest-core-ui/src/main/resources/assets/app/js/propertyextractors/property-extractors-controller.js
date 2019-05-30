'use strict';

//  NOTICE:
//    The $scope here prototypically inherits from the $scope of the specific test step controller.
//    ng-include also creates a scope.
angular.module('irontest').controller('PropertyExtractorsController', ['$scope', 'IronTestUtils', 'PropertyExtractors',
    '$stateParams',
  function($scope, IronTestUtils, PropertyExtractors, $stateParams) {
    $scope.bottomPaneLoadedCallback();

    $scope.findByTeststepId = function() {
      PropertyExtractors.query({ teststepId: $stateParams.teststepId }, function(returnPropertyExtractors) {
        $scope.propertyExtractors = returnPropertyExtractors;
      }, function(response) {
        IronTestUtils.openErrorHTTPResponseModal(response);
      });
    };

    $scope.createPropertyExtractor = function(type) {
      var propertyName = IronTestUtils.getNextNameInSequence($scope.propertyExtractors, 'Property ', 'propertyName');
      var propertyExtractor = new PropertyExtractors({
        propertyName: propertyName,
        type: type,
      });
      propertyExtractor.$save({ teststepId: $stateParams.teststepId }, function(returnPropertyExtractor) {
        $scope.propertyExtractors.push(propertyExtractor);
        $scope.$emit('successfullySaved');
      }, function(response) {
        IronTestUtils.openErrorHTTPResponseModal(response);
      });
    };
  }
]);
