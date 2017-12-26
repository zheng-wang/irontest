'use strict';

angular.module('irontest')
  .factory('IronTestUtils', function ($uibModal) {
    return {
      //  Search elements in the array using property, and delete the first element that has the property
      //  with the property value. The elements must be objects, and the property must be of primitive type.
      deleteArrayElementByProperty: function(array, propertyName, propertyValue) {
        var indexOfElement = array.findIndex(
          function(element) {
            return element[propertyName] === propertyValue;
          }
        );
        array.splice(indexOfElement, 1);
      },

      //  Search the objArray (by inspecting each obj's name property) to
      //  find the next available name-in-sequence to use.
      //  Name-in-sequence format: '<baseName> <sequence>'. For example: 'XPath 1'.
      getNextNameInSequence: function(objArray, baseName) {
        var isExistingName = function(obj) {
          return obj.name === this;
        };

        var sequence = 1;
        var name;
        for (; sequence < 10000000; sequence += 1) {
          name = baseName + ' ' + sequence;
          if (objArray.findIndex(isExistingName, name) === -1) {
            break;
          }
        }

        return name;
      },

      //  instruction is to tell user how to handle the error
      openErrorHTTPResponseModal: function(errorHTTPResponse, instruction) {
        var errorMessage = null;
        var errorDetails = null;

        if (!errorHTTPResponse.data) {
          errorMessage = 'Connection refused.';
          errorDetails = 'Unable to talk to Iron Test server.';
        } else if (errorHTTPResponse.status === 401) {
          errorMessage = 'User not authenticated.';
          errorDetails = 'Please log in and do the operation again.';
        } else {
          errorMessage = errorHTTPResponse.data.message;
          errorDetails = errorHTTPResponse.data.details;
        }

        if (instruction) {
          errorMessage += ' ' + instruction;
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
  });
