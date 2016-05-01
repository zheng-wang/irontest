'use strict';

angular.module('iron-test')
  .factory('IronTestUtils', function ($uibModal, _) {
    return {
      //  Search elements in the array using property, and return the index of the first element that has the property
      //  with the property value. The elements must be objects, and the property must be of primitive type.
      indexOfArrayElementByProperty: function(array, propertyName, propertyValue) {
        var result = -1;
        for (var i = 0; i < array.length; i += 1) {
          if (array[i][propertyName] === propertyValue) {
            result = i;
            break;
          }
        }
        return result;
      },

      //  Search elements in the array using property, and delete the first element that has the property
      //  with the property value. The elements must be objects, and the property must be of primitive type.
      deleteArrayElementByProperty: function(array, propertyName, propertyValue) {
        array.splice(this.indexOfArrayElementByProperty(array, propertyName, propertyValue), 1);
      },

      //  Search the objArray (by inspecting each obj's name property) to
      //  find the next available name-in-sequence to use.
      //  Name-in-sequence format: '<baseName> <sequence>'. For example: 'XPath 1'.
      getNextNameInSequence: function(objArray, baseName) {
        var checkExistingName = function(obj) {
          return obj.name === this;
        };

        var sequence = 1;
        var name;
        for (; sequence < 10000000; sequence += 1) {
          name = baseName + ' ' + sequence;
          //  replace below underscore usage with Array.findIndex after getting to Chrome 45+
          if (_.findIndex(objArray, checkExistingName, name) === -1) {
            break;
          }
        }

        return name;
      },

      openErrorHTTPResponseModal: function(errorHTTPResponse) {
        var errorMessage = null;
        var errorDetails = null;

        if (!errorHTTPResponse.data) {
          errorMessage = 'Connection refused.';
          errorDetails = 'Unable to talk to Iron Test server.';
        } else {
          errorMessage = errorHTTPResponse.data.message;
          errorDetails = errorHTTPResponse.data.details;
        }

        this.openErrorMessageModal(errorMessage, errorDetails);
      },

      openErrorMessageModal: function(errorMessage, errorDetails) {
        var modalInstance = $uibModal.open({
          templateUrl: 'errorMessageModalTemplate.html',
          controller: 'ErrorMessageModalController',
          size: 'md',
          backdrop: 'static',
          windowClass: 'error-message-modal',
          resolve: {
            errorMessage: function () {
              return errorMessage;
            },
            errorDetails: function () {
              return errorDetails;
            }
          }
        });
      }
    };
  }
);
