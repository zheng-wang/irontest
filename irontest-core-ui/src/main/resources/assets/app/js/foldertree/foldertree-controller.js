'use strict';

angular.module('irontest').controller('FolderTreeController', ['$scope', '$state', 'IronTestUtils', 'FolderTreeNodes',
    'Testcases',
  function($scope, $state, IronTestUtils, FolderTreeNodes, Testcases) {
    var NODE_TYPE_TEST_CASE = 'testcase';

    $scope.treeConfig = {
      core: {
        error: function(error) {
          IronTestUtils.openErrorMessageModal('Error from js tree.', angular.toJson(error));
        },
        check_callback: true
      },
      contextmenu: {
        items: function(selectedNode) {    //  selectedNode: the node you right clicked on
          var tree = $scope.treeInstance.jstree(true);
          var items = {
            createTestcase: {
              separator_before: false, separator_after: false, label: 'Create Test Case',
              action: function () {
                var testcase = new Testcases();
                testcase.$save({ parentFolderTreeNodeId: selectedNode.id }, function(response) {
                  //  reload the tree
                  $scope.loadTreeData();
                  //  display the newly created test case in the right pane
                  $state.go('testcase_edit', {testcaseId: response.id, newlyCreated: true});
                  //  enable user to edit the test case's name
                  //var newNode = tree.create_node(selectedNode, {type: NODE_TYPE_TEST_CASE});
                  //tree.edit(newNode);
                }, function(response) {
                  IronTestUtils.openErrorHTTPResponseModal(response);
                });
              }
            },
            createFolder: {
              separator_before: false, separator_after: false, label: 'Create Folder',
              action: function () {
                var newNode = tree.create_node(selectedNode);
                tree.edit(newNode);
              }
            },
            rename: {
              separator_before: false, separator_after: false, label: 'Rename',
              action: function () {
                tree.edit(selectedNode);
              }
            }
          };

          if (selectedNode.type === NODE_TYPE_TEST_CASE) {
            delete items.createTestcase;
            delete items.createFolder;
          }

          return items;
        }
      },
      types: {
			  testcase: {valid_children: [], icon: 'jstree-file'}
      },
      plugins: ['types', 'contextmenu', 'sort', 'dnd'],
      version: 1          //  ngJsTree property
    };

    /* $scope.treeData = [
        {id: '1', parent: '#', text: 'Root', state: {opened: true}},
        {id: '2', parent: '1', text: 'Folder 1', state: {opened: true}},
        {id: '3', parent: '1', text: 'Folder 2 erewradfdsfasfasdfasdfasdfasdfasfdafdfasfaf', state: {opened: true}},
        {id: '4', parent: '1', text: 'Folder 3', state: {opened: true}},
        {id: '100', parent: '2', text: 'Case 1', type: NODE_TYPE_TEST_CASE, data: {testcaseId: 3}}
    ]; */

    $scope.loadTreeData = function() {
      FolderTreeNodes.query(function(folderTreeNodes) {
        //  transform for default display effect (expanding Root folder) and complying with jstree format
        folderTreeNodes.forEach(function(treeNode) {
          if (treeNode.parent === null) {    //  root node(s)
            treeNode.parent = '#';
            treeNode.state = {opened: true};
          }
          if (treeNode.type === NODE_TYPE_TEST_CASE) {
            treeNode.data = {testcaseId: treeNode.testcaseId};
          }
        });

        $scope.treeData = folderTreeNodes;
        //  recreate the tree using new data
        $scope.treeConfig.version++;
      }, function(response) {
        IronTestUtils.openErrorHTTPResponseModal(response);
      });
    };

    var nodeSelected = function(event, data) {
      var node = data.node;
      if (node.type === NODE_TYPE_TEST_CASE) {
        $state.go('testcase_edit', {testcaseId: node.data.testcaseId});
      }
    };

    $scope.treeEventsObj = {
      select_node: nodeSelected
    }
  }
]);
