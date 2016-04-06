'use strict';

angular.module('iron-test').directive('successfulMessage', function () {
  return {
    link: function(scope, element, attrs) {
      var el = element[0];
      el.addEventListener("animationend", function() {
        if (scope.savingStatus) {
          scope.savingStatus.saveSuccessful = null;
        } else {
          scope.saveSuccessful = null;
        }
      });
      angular.element(el).addClass('successful-message');
    }
  };
});
