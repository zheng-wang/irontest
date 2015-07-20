'use strict';

angular.module('service-testing-tool').directive('successfulMessage', function () {
    return {
        link: function(scope, element, attrs) {
            var el = element[0];
            el.addEventListener("animationend", function() {
                scope.savingStatus.saveSuccessful = null;
            });
            angular.element(el).addClass('successful-message');
        }
    };
});
