'use strict';

/*
  The element's height is dynamically adjusted when the assertions area is toggled on or off.
*/
angular.module('irontest').directive('assertionsAreaAssociativeElement', function () {
  return {
    link: function(scope, element, attrs) {
      //  Determine the initial height of the element, by using nearest absolute height node.
      //  Chose window object.
      //  Not able to use page-wrapper node as it's height is dynamically changed by
      //  startbootstrap-sb-admin-2 javascript on window resize.
      var topOffset = document.getElementById('page-top-navbar').offsetHeight +
        document.getElementById('page-header').offsetHeight +
        angular.element('.nav-tabs').height();     // tab heading height
      var tabContentsHeight = window.innerHeight - topOffset;
      var initialHeightFactor = attrs['assertionsAreaAssociativeElement'];   //  initial proportion of tabContentHeight
      element.height(tabContentsHeight * initialHeightFactor);

      var assertionsArea = document.getElementById('assertionsArea');

      scope.$on('toggleAssertionsArea', function() {
        if (scope.showAssertionsArea) {     //  toggle off
          element.height(element.height() + assertionsArea.offsetHeight);
          scope.showAssertionsArea = false;
        } else {                            //  toggle on
          scope.showAssertionsArea = true;
        }
      });

      scope.$on('assertionsAreaLoaded', function() {
        element.height(element.height() - assertionsArea.offsetHeight);
      })
    }
  };
});
