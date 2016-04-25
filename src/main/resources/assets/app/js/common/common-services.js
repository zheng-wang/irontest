'use strict';

angular.module('iron-test')
  .factory('IronTestUtils', function ($uibModal) {
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

      openErrorMessageModal: function(errorHTTPResponse) {
        var errorMessage = null;
        var errorDetails = null;

        if (!errorHTTPResponse.data) {
          errorMessage = 'Connection refused.';
          errorDetails = 'Unable to talk to Iron Test server.';
        } else {
          errorMessage = errorHTTPResponse.data.message;
          errorDetails = errorHTTPResponse.data.details;
        }

        var modalInstance = $uibModal.open({
          templateUrl: '/ui/views/common/error-message-modal.html',
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
