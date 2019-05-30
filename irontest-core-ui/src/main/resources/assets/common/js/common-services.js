'use strict';

angular.module('common')
  .factory('IronTestUtils', function ($uibModal) {
    return {
      //  Search elements in the array using property, and delete the first element that has the property
      //  with the property value. The elements must be objects, and the property must be of primitive type.
      deleteArrayElementByProperty: function(array, propertyName, propertyValue) {
        array.splice(array.findIndex(item => item[propertyName] === propertyValue), 1);
      },

      //  Search the objArray by inspecting the specified property of each obj to
      //  find the next available name-in-sequence to use.
      //  If propertyName is not specified, use 'name' as default.
      //  Name-in-sequence format: '<nameBase><sequence>'. For example: 'XPath 1' if nameBase is 'XPath '.
      getNextNameInSequence: function(objArray, nameBase, propertyName = 'name') {
        var sequence = 1;
        var result;
        for (; sequence < 10000000; sequence += 1) {
          result = nameBase + sequence;
          if (objArray.findIndex(obj => obj[propertyName] === result) === -1) {
            break;
          }
        }

        return result;
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
          errorDetails = 'Please log in and try the operation again.';
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
      },

      getRequestBodyMainPattern: function(requestMethod, bodyPatterns) {
        if (this.requestBodyApplicable(requestMethod)) {
          var result = { name: null, displayName: null, value: null };
            if (bodyPatterns) {
              var bodyPattern = bodyPatterns[0];
              if (bodyPattern) {
                if ('equalToXml' in bodyPattern) {
                  result.name = 'equalToXml';
                  result.displayName = 'Equal to XML';
                  result.value = bodyPattern.equalToXml;
                } else if ('equalToJson' in bodyPattern) {
                  result.name = 'equalToJson';
                  result.displayName = 'Equal to JSON';
                  result.value = bodyPattern.equalToJson;
                }
              }
            } else {
              result.name = 'any';
              result.displayName = 'Can be Any';
            }
            return result;
        } else {
          return null;
        }
      },

      requestBodyApplicable: function(requestMethod) {
        return requestMethod && (requestMethod === 'POST' || requestMethod === 'PUT');
      },

      formatHTTPHeadersObj: function(headersObj) {
        var result = '';
        if (headersObj) {
          Object.keys(headersObj).forEach(function(key, index) {
            if (index > 0) {
              result += '\n';
            }
            result += key + ': ' + headersObj[key];
          });
        }

        return result;
      }
    };
  });
