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
      contextmenu: {
        items: function(selectedNode) {    //  selectedNode: the node you right clicked on
          var tree = $scope.treeInstance.jstree(true);
          var items = {
            createTestcase: {
              separator_before: false,
              separator_after: false,
              label: 'Create Test Case',
              action: function () {
                var newNode = tree.create_node(selectedNode, { type : 'file' });
                tree.edit(newNode);
              }
            },
            createFolder: {
              separator_before: false,
              separator_after: false,
              label: 'Create Folder',
              action: function () {
                var newNode = tree.create_node(selectedNode);
                tree.edit(newNode);
              }
            },
            rename: {
              separator_before: false,
              separator_after: false,
              label: 'Rename',
              action: function () {
                tree.edit(selectedNode);
              }
            }
          };

          if (selectedNode.type === 'file') {
            delete items.createTestcase;
            delete items.createFolder;
          }

          return items;
        }
      },
      types: {
			  file: { valid_children: [], icon: 'jstree-file' }
      },
      plugins: [ 'types', 'contextmenu' ],
      version: 1          //  ngJsTree property
    };

    $scope.treeData = [
      { id: '1', parent: '#', text: 'Root', state: { opened: true} },
      { id: '2', parent: '1', text: 'Folder 1', state: { opened: true} },
      { id: '3', parent: '1', text: 'Folder 2 erewradfdsfasfasdfasdfasdfasdfasfdafdfasfaf', state: { opened: true }}
    ];
    $scope.treeData.push({ id: '100', parent: '2', text: 'Case 1', type: 'file' }),

    /*var createNodeCB = function(e, item) {
      console.log('Added new node with the text ' + item.node.text);
    };
    var readyCB = function() {
      // tree is ready and the tree instance $scope.treeInstance.jstree(true) is ready for use
      console.log('JS Tree Ready');
    };*/

    $scope.treeEventsObj = {
      //ready: readyCB,
      //create_node: createNodeCB
    }
  }
]);
