'use strict';

angular.module('irontest').controller('FolderTreeController', ['$scope',
  function($scope) {
    $scope.treeConfig = {
      core: {
        error: function(error) {
          alert('treeCtrl: error from js tree - ' + angular.toJson(error));
        },
        check_callback: true
      },
      version : 1          //  ngJsTree property
    };

    $scope.treeData = [
      { id : 'ajson1', parent : '#', text : 'Simple root node', state: { opened: true} },
      { id : 'ajson2', parent : '#', text : 'Root node 2', state: { opened: true} },
      { id : 'ajson3', parent : 'ajson2', text : 'Child 1', state: { opened: true} },
      { id : 'ajson4', parent : 'ajson2', text : 'Child 2' , state: { opened: true}}
    ];

    var createNodeCB = function(e, item) {
      console.log('Added new node with the text ' + item.node.text);
    };

    var readyCB = function() {
      // tree is ready and $scope.treeInstance is ready for use
      console.log('JS Tree Ready');
    };

    $scope.treeEventsObj = {
      ready: readyCB,
      create_node: createNodeCB
    }
  }
]);
