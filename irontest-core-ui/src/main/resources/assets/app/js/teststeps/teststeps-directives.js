'use strict';

/*
  The element's height is dynamically adjusted when there is another element being inserted/removed from the column (tab content).
*/
angular.module('irontest').directive('heightAdjustableElementInColumn', function () {
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
      var initialHeightFactor = attrs['heightAdjustableElementInColumn'];   //  initial proportion of tabContentHeight
      element.height(tabContentsHeight * initialHeightFactor);

      //  another element has been removed from the column
      scope.$on('elementRemovedFromColumn', function(event, args) {
        element.height(element.height() + args.elementHeight);
      });

      //  another element has been inserted into the column
      scope.$on('elementInsertedIntoColumn', function(event, args) {
        element.height(element.height() - args.elementHeight);
      });
    }
  };
});
