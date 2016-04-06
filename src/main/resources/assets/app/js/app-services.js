'use strict';

angular.module('iron-test')
  .factory('IronTestUtils', function () {
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
      }
    };
  }
);
