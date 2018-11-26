'use strict';

angular.module('irontest').controller('FolderTreeController', ['$scope', '$rootScope', '$state', 'IronTestUtils',
    'FolderTreeNodes', '$timeout', '$transitions', 'Testcases', '$window', '$uibModal', 'Upload',
  function($scope, $rootScope, $state, IronTestUtils, FolderTreeNodes, $timeout, $transitions, Testcases, $window,
      $uibModal, Upload) {
    var NODE_TYPE_FOLDER = 'folder';
    var NODE_TYPE_TEST_CASE = 'testcase';
    var disableContextMenuItems = false;
    var idOfTestcaseCopied = null;

    var createNode = function(parentFolderId, nodeType) {
      var nodeRes = new FolderTreeNodes({ parentFolderId: parentFolderId, type: nodeType });
      nodeRes.$save(function(response) {
        //  reload the tree (a chance to sync between users in a team)
        $scope.reloadTreeData(function successCallback() {
          var newNodeId = nodeType + response.idPerType;
          var tree = $scope.treeInstance.jstree(true);

          //  switch the selection from the folder to the newly created test case,
          //  so that the state plugin can remember this status.
          var parentNodeId = NODE_TYPE_FOLDER + parentFolderId;
          tree.deselect_node(parentNodeId);
          tree.select_node(newNodeId);

          //  open the node's URL
          displayNodeDetails(nodeType, response.idPerType);

          //  enable user to edit the node's name
          tree.edit(newNodeId);
        });
      }, function(response) {
        IronTestUtils.openErrorHTTPResponseModal(response);
      });
    };

    var copyTestcase = function(testcaseId) {
      idOfTestcaseCopied = testcaseId;
    };

    var pasteTestcase = function(folderId) {
      var testcaseRes = new Testcases({
        id: idOfTestcaseCopied
      });
      testcaseRes.$duplicate({ targetFolderId: folderId }, function(response) {
        var newTestcaseId = response.id;
         //  reload the tree (a chance to sync between users in a team)
         $scope.reloadTreeData(function successCallback() {
           displayNodeDetails(NODE_TYPE_TEST_CASE, newTestcaseId);
         });
      }, function(response) {
        IronTestUtils.openErrorHTTPResponseModal(response);
      });
    };

    var exportTestcase = function(testcaseId) {
      var url = 'api/testcases/' + testcaseId + "/export";
      $window.open(url, '_blank', '');
    };

    var importTestcase = function(folderId) {
      //  open modal dialog
      var modalInstance = $uibModal.open({
        templateUrl: '/ui/views/foldertree/import-testcase-modal.html',
        controller: 'ImportTestcaseModalController',
        size: 'sm',
        windowClass: 'import-testcase-modal'
      });

      //  handle result from modal dialog
      modalInstance.result.then(function closed(file) {
        if (file) {
          var url = 'api/folders/' + folderId + '/importTestcase';
          Upload.upload({ url: url, data: {file: file}}
          ).then(function successCallback(response) {
            var testcaseId = response.data.id;

            //  reload the tree (a chance to sync between users in a team)
            $scope.reloadTreeData(function successCallback() {
              var newNodeId = NODE_TYPE_TEST_CASE + testcaseId;
              var tree = $scope.treeInstance.jstree(true);

              //  switch the selection from the folder to the newly created test case,
              //  so that the state plugin can remember this status.
              var parentNodeId = NODE_TYPE_FOLDER + folderId;
              tree.deselect_node(parentNodeId);
              tree.select_node(newNodeId);

              //  open the new node's URL
              displayNodeDetails(NODE_TYPE_TEST_CASE, testcaseId);
            });
          }, function errorCallback(response) {
            IronTestUtils.openErrorHTTPResponseModal(response);
          });
        }
      }, function dismissed() {
        //  Modal dismissed. Do nothing.
      });
    };

    $scope.treeData = [];

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
              action: function() { createNode(selectedNode.data.idPerType, NODE_TYPE_TEST_CASE); },
              _disabled: function() {
                return disableContextMenuItems;
              }
            },
            createFolder: {
              separator_before: false, separator_after: false, label: 'Create Folder',
              action: function() { createNode(selectedNode.data.idPerType, NODE_TYPE_FOLDER); },
              _disabled: function() {
                return disableContextMenuItems;
              }
            },
            rename: {
              separator_before: false, separator_after: false, label: 'Rename',
              action: function () {
                var tree = $scope.treeInstance.jstree(true);
                tree.edit(selectedNode);
              },
              _disabled: function() {
                return disableContextMenuItems;
              }
            },
            copyTestcase: {
              separator_before: false, separator_after: false, label: 'Copy',
              action: function() { copyTestcase(selectedNode.data.idPerType); },
              _disabled: function() {
                return disableContextMenuItems;
              }
            },
            pasteTestcase: {
              separator_before: false, separator_after: false, label: 'Paste',
              action: function() { pasteTestcase(selectedNode.data.idPerType); }
            },
            exportTestcase: {
              separator_before: false, separator_after: false, label: 'Export',
              action: function() { exportTestcase(selectedNode.data.idPerType); },
              _disabled: function() {
                return disableContextMenuItems;
              }
            },
            importTestcase: {
              separator_before: false, separator_after: false, label: 'Import Test Case',
              action: function() { importTestcase(selectedNode.data.idPerType); },
              _disabled: function() {
                return disableContextMenuItems;
              }
            }
          };

          switch (selectedNode.type) {
            case NODE_TYPE_TEST_CASE:
              delete items.createTestcase;
              delete items.createFolder;
              delete items.pasteTestcase;
              delete items.importTestcase;
              break;
            case NODE_TYPE_FOLDER:
              delete items.copyTestcase;
              if (!idOfTestcaseCopied) {
                delete items.pasteTestcase;
              }
              delete items.exportTestcase;
              break;
            default:
              break;
          }

          return items;
        }
      },
      types: {
        folder: {},
			  testcase: {valid_children: [], icon: 'jstree-file'}
      },
      plugins: ['types', 'contextmenu', 'sort', 'state'],
      version: 1          //  ngJsTree property
    };

    var selectNodeByUIRouterState = function(stateName, params) {
      var tree = $scope.treeInstance.jstree(true);
      tree.deselect_all();
      switch (stateName) {
        case 'testcase_edit':
        case 'teststep_edit':
        case 'httpstub_edit':
          tree.select_node(NODE_TYPE_TEST_CASE + params.testcaseId);
          break;
        case 'folder':
          tree.select_node(NODE_TYPE_FOLDER + params.folderId);
          break;
        default:
          break;
      }
    };

    $transitions.onSuccess({}, function(trans) {
      if (trans.$from().name !== '') {    //  do nothing if refreshing browser (node selection is done by the treeReady function)
        selectNodeByUIRouterState(trans.$to().name, trans.params('to'));
      }
    });

    var alignAccessRightOfTheTree = function() {
      if ($rootScope.appStatus.isForbidden()) {    //  make the tree readonly
        //  disable context menu items
        disableContextMenuItems = true;

        //  disable node moving
        var index = $scope.treeConfig.plugins.indexOf('dnd');
        if (index > -1) {
          $scope.treeConfig.plugins.splice(index, 1)
        }
      } else {                                     // make the tree writable
        //  enable context menu items
        disableContextMenuItems = false;

        //  enable node moving
        var index = $scope.treeConfig.plugins.indexOf('dnd');
        if (index === -1) {
          $scope.treeConfig.plugins.push('dnd');
        }
      }
    };

    $rootScope.$on('userLoggedIn', function() {
      alignAccessRightOfTheTree();
      $scope.treeConfig.version++;         //  recreate the tree without reloading data from server side
    });
    $rootScope.$on('userLoggedOut', function() {
      alignAccessRightOfTheTree();
      $scope.treeConfig.version++;         //  recreate the tree without reloading data from server side
    });

    $scope.reloadTreeData = function(successCallback) {
      alignAccessRightOfTheTree();

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
          $timeout(function() {    //  wait for the tree to finish building
            successCallback();
          }, 60);
        }
      }, function(response) {
        var instruction = 'Please refresh the page. If problem is still there, please contact the system administrator.'
        IronTestUtils.openErrorHTTPResponseModal(response, instruction);
      });
    };

    var displayNodeDetails = function(type, idPerType) {
      if (type === NODE_TYPE_TEST_CASE) {
        $state.go('testcase_edit', {testcaseId: idPerType}, {reload: true});
      } else if (type === NODE_TYPE_FOLDER) {
        $state.go('folder', {folderId: idPerType});
      }
    };

    var treeReady = function() {
      if ($scope.treeConfig.version === 1) {       //  initial tree data loading
        //  consider the page is loaded when the tree is loaded)
        $rootScope.$emit('pageLoaded');    //  not using broadcast, for better performance

        $rootScope.appStatusPromise.then(    //  ensure appStatus is ready for use before reloading the tree data
          function() {
            $scope.reloadTreeData(function successCallback() {
              selectNodeByUIRouterState($state.current.name, $state.params);
            });
          }
        )
      }
    };

    var nodeSelected = function(event, data) {
      var node = data.node;
      //  open node's URL only on mouse left or right click
      if (data.event && (data.event.type === 'click' || data.event.type === 'contextmenu')) {
        displayNodeDetails(node.type, node.data.idPerType);
      }
    };

    var nodeRenamed = function(event, data) {
      var node = data.node;
      if (data.text !== data.old) {      //  node is actually renamed, update it at server side and open its URL
        //  update node at server side
        var nodeRes = new FolderTreeNodes({
          idPerType: node.data.idPerType, parentFolderId: node.data.parentFolderId,
          text: data.text, type: node.type
        });
        nodeRes.$update(function(response) {
          displayNodeDetails(node.type, node.data.idPerType);
        }, function(response) {
          IronTestUtils.openErrorHTTPResponseModal(response);
          //  reload the tree to restore previous status
          $scope.reloadTreeData();
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
        $scope.reloadTreeData(function successCallback() {
          var tree = $scope.treeInstance.jstree(true);

          //  open the new parent folder, so that the state plugin can remember this status.
          tree.open_node(data.parent);
        });
      }, function(response) {
        IronTestUtils.openErrorHTTPResponseModal(response);
        //  reload the tree to restore previous status
        $scope.reloadTreeData();
      });
    };

    $scope.treeEventsObj = {
      ready: treeReady,
      select_node: nodeSelected,
      rename_node: nodeRenamed,
      move_node: nodeMoved
    }
  }
]);
