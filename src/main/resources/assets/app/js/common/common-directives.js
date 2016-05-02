'use strict';

angular.module('iron-test').directive('irtSuccessfulMessage', function () {
  return {
    template: 'Successfully saved',
    link: function(scope, element, attrs) {
      var watchExpression;
      var animationendCallback;

      if (scope.savingStatus) {
        watchExpression = 'savingStatus.saveSuccessful';

        animationendCallback = function() {
          scope.savingStatus.saveSuccessful = null;
        };
      } else {
        watchExpression = 'saveSuccessful';

        animationendCallback = function() {
          scope.saveSuccessful = null;
        };
      }

      //  register function for starting animation
      scope.$watch(watchExpression, function(newVal) {
        element.toggleClass('successful-message-animation', !!newVal);
      });

      //  register listener for animation end processing
      element[0].addEventListener("animationend", animationendCallback);

      //  add class that is always on the element
      element.addClass('successful-message');
    }
  };
});
