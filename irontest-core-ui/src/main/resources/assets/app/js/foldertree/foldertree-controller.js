'use strict';

angular.module('irontest').controller('FolderTreeController', ['$scope', '$state', 'IronTestUtils', 'FolderTreeNodes',
    'Testcases', '$timeout',
  function($scope, $state, IronTestUtils, FolderTreeNodes, Testcases, $timeout) {
    var NODE_TYPE_FOLDER = 'folder';
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
          var items = {
            createTestcase: {
              separator_before: false, separator_after: false, label: 'Create Test Case',
              action: function () {
                var parentFolderId = selectedNode.id;
                var testcase = new Testcases();
                testcase.$save({ parentFolderId: selectedNode.data.idPerType }, function(response) {
                  //  reload the tree
                  $scope.loadTreeData(function successCallback() {
                    //  select the newly created test case
                    $timeout(function() {
                      var tree = $scope.treeInstance.jstree(true);
                      tree.select_node(NODE_TYPE_TEST_CASE + response.id);
                    }, 100);

                    //  enable user to edit the test case's name
                    //var newNode = tree.create_node(selectedNode, {type: NODE_TYPE_TEST_CASE});
                    //tree.edit(newNode);
                  });

                }, function(response) {
                  IronTestUtils.openErrorHTTPResponseModal(response);
                });
              }
            },
            createFolder: {
              separator_before: false, separator_after: false, label: 'Create Folder',
              action: function () {
                var tree = $scope.treeInstance.jstree(true);
                var newNode = tree.create_node(selectedNode);
                tree.edit(newNode);
              }
            },
            rename: {
              separator_before: false, separator_after: false, label: 'Rename',
              action: function () {
                var tree = $scope.treeInstance.jstree(true);
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

    $scope.loadTreeData = function(successCallback) {
      FolderTreeNodes.query(function(folderTreeNodes) {
        //  transform for default display effect (expanding Root folder) and complying with jstree format
        folderTreeNodes.forEach(function(treeNode) {
          treeNode.id = treeNode.type + treeNode.idPerType;
          treeNode.data = {idPerType: treeNode.idPerType};
          if (treeNode.parentFolderId === null) {    //  root node(s)
            treeNode.parent = '#';
            treeNode.state = {opened: true};         //  always open root node
          } else {
            treeNode.parent = NODE_TYPE_FOLDER + treeNode.parentFolderId;
          }
        });

        $scope.treeData = folderTreeNodes;
        //  recreate the tree using new data
        $scope.treeConfig.version++;

        if (successCallback) {
          successCallback();
        }
      }, function(response) {
        IronTestUtils.openErrorHTTPResponseModal(response);
      });
    };

    var nodeSelected = function(event, data) {
      var node = data.node;
      if (node.type === NODE_TYPE_TEST_CASE) {
        $state.go('testcase_edit', {testcaseId: node.data.idPerType});
      }
    };

    $scope.treeEventsObj = {
      select_node: nodeSelected
    }
  }
]);
