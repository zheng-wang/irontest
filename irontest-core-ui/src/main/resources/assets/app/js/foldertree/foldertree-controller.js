'use strict';

angular.module('irontest').controller('FolderTreeController', ['$scope', '$state', 'IronTestUtils', 'FolderTreeNodes',
    '$timeout',
  function($scope, $state, IronTestUtils, FolderTreeNodes, $timeout) {
    var NODE_TYPE_FOLDER = 'folder';
    var NODE_TYPE_TEST_CASE = 'testcase';

    var createNode = function(parentFolderId, type) {
      var nodeRes = new FolderTreeNodes({ parentFolderId: parentFolderId, type: type });
      nodeRes.$save(function(response) {
        //  reload the tree (a chance to sync between users in a team)
        $scope.loadTreeData(function successCallback() {
          $timeout(function() {    //  wait for the tree to finish loading
            var newNodeId = type + response.idPerType;
            var tree = $scope.treeInstance.jstree(true);

            //  switch the selection from the folder to the newly created test case,
            //  so as the state plugin can remember this selection.
            var parentNodeId = NODE_TYPE_FOLDER + parentFolderId;
            tree.deselect_node(parentNodeId);
            tree.select_node(newNodeId);

            //  enable user to edit the node's name
            tree.edit(newNodeId);     //  as a (good) side effect, this opens all the node's ancestor folders
          }, 100);
        });
      }, function(response) {
        IronTestUtils.openErrorHTTPResponseModal(response);
      });
    };

    $scope.treeConfig = {
      core: {
        multiple: false,
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
              action: function() { createNode(selectedNode.data.idPerType, NODE_TYPE_TEST_CASE); }
            },
            createFolder: {
              separator_before: false, separator_after: false, label: 'Create Folder',
              action: function() { createNode(selectedNode.data.idPerType, NODE_TYPE_FOLDER); }
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
      plugins: ['types', 'contextmenu', 'sort', 'dnd', 'state'],
      version: 1          //  ngJsTree property
    };

    $scope.loadTreeData = function(successCallback) {
      FolderTreeNodes.query(function(folderTreeNodes) {
        //  transform for default display effect (expanding Root folder) and complying with jstree format
        folderTreeNodes.forEach(function(treeNode) {
          treeNode.id = treeNode.type + treeNode.idPerType;
          treeNode.data = {    //  preserve original data
            idPerType: treeNode.idPerType,
            parentFolderId: treeNode.parentFolderId
          };
          if (treeNode.parentFolderId === null) {    //  root node(s)
            treeNode.parent = '#';
          } else {
            treeNode.parent = NODE_TYPE_FOLDER + treeNode.parentFolderId;
          }
        });

        //  set tree data
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

    var openTestcase = function(testcaseId) {
      $state.go('testcase_edit', {testcaseId: testcaseId}, {reload: true});
    };

    var nodeSelected = function(event, data) {
      var node = data.node;
      if (node.type === NODE_TYPE_TEST_CASE) {
        openTestcase(node.data.idPerType);
      }
    };

    var nodeRenamed = function(event, data) {
      var node = data.node;
      if (node.type === NODE_TYPE_TEST_CASE && data.text !== data.old) {      //  test case name is changed
        //  update node at server side
        var nodeRes = new FolderTreeNodes({
          idPerType: node.data.idPerType, parentFolderId: node.data.parentFolderId,
          text: data.text, type: node.type
        });
        nodeRes.$update(function(response) {
          openTestcase(node.data.idPerType);
        }, function(response) {
          IronTestUtils.openErrorHTTPResponseModal(response);
        });
      }
    };

    var nodeMoved = function(event, data) {
      var tree = $scope.treeInstance.jstree(true);
      var node = data.node;
      var newParentNode = tree.get_node(data.parent);

      //  update node at server side
      var nodeRes = new FolderTreeNodes({
        idPerType: node.data.idPerType, parentFolderId: newParentNode.data.idPerType,
        text: node.text, type: node.type
      });
      nodeRes.$update(function(response) {
        //  reload the tree (a chance to sync between users in a team)
        $scope.loadTreeData();
      }, function(response) {
        IronTestUtils.openErrorHTTPResponseModal(response);
      });
    };

    $scope.treeEventsObj = {
      select_node: nodeSelected,
      rename_node: nodeRenamed,
      move_node: nodeMoved
    }
  }
]);
