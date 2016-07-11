'use strict';

//  NOTICE:
//    The $scope here prototypically inherits from the $scope of teststeps-controller.js.
//    ng-include also creates a scope.
angular.module('irontest').controller('MQTeststepController', ['$scope', 'Testruns', 'IronTestUtils', '$timeout',
    '$http', 'Upload', '$window', 'Teststeps',
  function($scope, Testruns, IronTestUtils, $timeout, $http, Upload, $window, Teststeps) {
    var timer;
    $scope.testrun = {};
    $scope.enqueueMessageActiveTabIndex = 0;

    var clearPreviousRunAndAssertionVerificationStatus = function() {
      if (timer) $timeout.cancel(timer);
      $scope.testrun = {};
      $scope.assertionVerificationResult = null;
    };

    $scope.actionChanged = function(isValid) {
      clearPreviousRunAndAssertionVerificationStatus();

      // initialize new action
      if ($scope.teststep.action === 'Enqueue') {
        if (!$scope.teststep.otherProperties) {
          $scope.teststep.otherProperties = {};
        }
        if (!$scope.teststep.otherProperties.enqueueMessageFrom) {
          $scope.teststep.otherProperties.enqueueMessageFrom = 'Text';
        }
      }

      //  save test step
      $scope.update(isValid);
    };

    $scope.doAction = function() {
      clearPreviousRunAndAssertionVerificationStatus();

      var testrun = {
        teststep: $scope.teststep
      };
      var testrunRes = new Testruns(testrun);
      $scope.testrun.status = 'ongoing';
      testrunRes.$save(function(response) {
        $scope.testrun.response = response.response;
        $scope.testrun.status = 'finished';
        timer = $timeout(function() {
          $scope.testrun.status = null;
        }, 15000);
      }, function(response) {
        $scope.testrun.status = 'failed';
        IronTestUtils.openErrorHTTPResponseModal(response);
      });
    };

    $scope.verifyXMLEqualAssertion = function() {
      var url = 'api/jsonservice/verifyassertion';
      var assertionVerification = {
        input: $scope.testrun.response,
        assertion: $scope.teststep.assertions[0]
      };
      $http
        .post(url, assertionVerification)
        .then(function successCallback(response) {
          $scope.assertionVerificationResult = response.data;
          $scope.assertionVerificationResult.display =
            response.data.error ? response.data.error : response.data.differences;
        }, function errorCallback(response) {
          IronTestUtils.openErrorHTTPResponseModal(response);
        });
    };

    $scope.addRFH2Folder = function(isValid) {
      var folders = $scope.teststep.otherProperties.enqueueMessageRFH2Header.folders;
      folders.push({ string: '<RFH2Folder></RFH2Folder>' });
      $scope.enqueueMessageActiveTabIndex = folders.length;
      $scope.update(isValid);
    };

    $scope.enqueueMessageTabSelected = function(index) {
      $scope.enqueueMessageActiveTabIndex = index;
    };

    $scope.deleteRFH2Folder = function(isValid) {
      var folders = $scope.teststep.otherProperties.enqueueMessageRFH2Header.folders;
      folders.splice($scope.enqueueMessageActiveTabIndex - 1, 1);
      $scope.enqueueMessageActiveTabIndex = $scope.enqueueMessageActiveTabIndex - 1;
      $scope.update(isValid);
    };

    $scope.uploadRequestFile = function(file) {
      if (file) {
        var url = 'api/testcases/' + $scope.teststep.testcaseId + '/teststeps/' + $scope.teststep.id +
          '/uploadRequestFile';
        Upload.upload({
          url: url,
          data: {file: file}
        }).then(function successCallback(response) {
          $scope.$emit('successfullySaved');
          $scope.setTeststep(new Teststeps(response.data));
        }, function errorCallback(response) {
          IronTestUtils.openErrorHTTPResponseModal(response);
        });
      }
    };

    $scope.downloadRequestFile = function() {
      var url = 'api/testcases/' + $scope.teststep.testcaseId + '/teststeps/' + $scope.teststep.id +
        '/downloadRequestFile';
      $window.open(url, '_blank', '');
    };
  }
]);
