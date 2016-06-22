'use strict';

//  NOTICE:
//    The $scope here prototypically inherits from the $scope of teststeps-controller.js.
//    ng-include also creates a scope.
angular.module('irontest').controller('MQTeststepController', ['$scope', 'Testruns', 'IronTestUtils', '$timeout',
    '$http', 'Upload',
  function($scope, Testruns, IronTestUtils, $timeout, $http, Upload) {
    var timer;
    $scope.testrun = {};

    var clearPreviousRunAndAssertionVerificationStatus = function() {
      if (timer) $timeout.cancel(timer);
      $scope.testrun = {};
      $scope.assertionVerificationResult = null;
    };

    $scope.actionChanged = function(isValid, oldAction) {
      //  backup old action's assertions
      if (oldAction === 'CheckDepth') {
        $scope.teststep.otherProperties.queueDepthAssertionPropertiesBackup =
          $scope.teststep.assertions[0].otherProperties;
      } else if (oldAction === 'Dequeue') {
        $scope.teststep.otherProperties.dequeueAssertionPropertiesBackup =
          $scope.teststep.assertions[0].otherProperties;
      }

      // clear action data
      clearPreviousRunAndAssertionVerificationStatus();
      $scope.teststep.assertions = [];

      // setup new action's assertions
      if ($scope.teststep.otherProperties.action === 'CheckDepth') {
        $scope.teststep.assertions[0] = {
          name: 'MQ queue depth equals',
          type: 'IntegerEqual'
        };
        // restore old assertion properties if there is a backup
        var propertiesBackup = $scope.teststep.otherProperties.queueDepthAssertionPropertiesBackup;
        $scope.teststep.assertions[0].otherProperties = propertiesBackup ? propertiesBackup : { number: 0 };
      } else if ($scope.teststep.otherProperties.action === 'Dequeue') {
        $scope.teststep.assertions[0] = {
          name: 'Dequeue XML equals',
          type: 'XMLEqual'
        };
        // restore old assertion properties if there is a backup
        var propertiesBackup = $scope.teststep.otherProperties.dequeueAssertionPropertiesBackup;
        $scope.teststep.assertions[0].otherProperties = propertiesBackup ? propertiesBackup : { expectedXML: null };
      }

      // setup new action's other stuff
      if ($scope.teststep.otherProperties.action === 'Enqueue') {
        if (!$scope.teststep.otherProperties.enqueueMessageType) {
          $scope.teststep.otherProperties.enqueueMessageType = 'Text';
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

    $scope.uploadRequestFile = function(file) {
      if (file) {
        Upload.upload({
          url: 'api/testcases/' + $scope.teststep.testcaseId + '/teststeps/' + $scope.teststep.id + '/uploadRequestFile',
          data: {file: file}
        }).then(function (response) {
          $scope.teststep = response;
        }, function (response) {
          IronTestUtils.openErrorHTTPResponseModal(response);
        });
      }
    }
  }
]);
