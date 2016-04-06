'use strict';

angular.module('iron-test').directive('requestResponseTextareas', function () {
  return {
    link: function(scope, element, attrs) {
      //  Use nearest absolute height node to determine reqResTextareas height.
      //  Chose window object.
      //  Not able to use page-wrapper node as it's height is dynamically changed by
      //  startbootstrap-sb-admin-2 javascript on window resize.
      var topOffset = document.getElementById('page-top-navbar').offsetHeight +
        document.getElementById('page-header').offsetHeight +
        angular.element('.nav-tabs').height();     // tab heading height
      var tabContentsHeight = window.innerHeight - topOffset;
      element.height(tabContentsHeight * 0.8);

      var toggleButton = angular.element(document.getElementById('assertionAreaToggleButton'));
      toggleButton.bind('click', function() {
        var newHeight = element.height() + document.getElementById('assertionsArea').offsetHeight;
        element.height(newHeight);
        scope.showAssertionsArea = !(scope.showAssertionsArea);
        scope.$apply();
      });

      scope.$on('assertionsAreaLoaded', function () {
        var newHeight = element.height() - document.getElementById('assertionsArea').offsetHeight;
        element.height(newHeight);
      })
    }
  };
});
