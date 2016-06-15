'use strict';

angular.module('irontest')
  .directive('irtSuccessfulMessage', function ($timeout) {
    return {
      template: 'Successfully saved',
      link: function(scope, element, attrs) {
        scope.$on('successfullySaved', function () {
          element.removeClass('successful-message-animation');
          $timeout(function() {  //  wait for the removeClass to take effect
            element.addClass('successful-message-animation');
          }, 5);
        })

        //  add class that is always on the element
        element.addClass('successful-message');
      }
    };
  })
  .directive('irtSelect', function($timeout) {
    return {
      link : function(scope, element, attrs) {
        attrs.$observe('irtSelect', function(value) {
          if (value === 'true') {
            $timeout(function() {
              element[0].select();
            }, 50);    //  without the 50 milliseconds delay, select() often turns into focus()
          }
        });
      }
    };
  });
