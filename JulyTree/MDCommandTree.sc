// MDCommandTree.sc
// Refactored for clarity and consistency
// MD 20250813

// MDCommandTree.sc
// Refactored for clarity and consistency
// MD 20250813

MDCommandTree {
    var <>root, <>nodeLimit = 50, <>nodeCount = 0, <>nodeMap;

    *new { |rootName = "root", rootId = 0, nodeLimit|
        ^super.new.init(rootName, rootId, nodeLimit);
    }

    *fromDict { |dict|
        var tree;
        tree = MDCommandTree.new(dict[\name], dict[\id], dict[\fret]);
        tree.root = tree.rebuildTreeFromDict(dict);
        ^tree
    }

    init { |rootName, rootId, limit|
        root = MDCommandNode.new(rootName, rootId);
        nodeLimit = limit;
        nodeCount = 1;

        nodeMap = IdentityDictionary.new(100);
        nodeMap.put(rootId, root);

        ^this
    }

    printTreePretty {
        root.printTreePretty;
        ^this
    }

    addNode { |parentId, name, fret|
        var newId, parentNode, newNode;

        newId = nodeCount + 1;
        parentNode = nodeMap.at(parentId);

        if (parentNode.notNil) {
            nodeCount = newId;
            newNode = MDCommandNode.new(name, newId, fret);
            newNode.parent = parentNode;
            parentNode.addChild(newNode);
            nodeMap.put(newId, newNode);
            ^newNode
        } {
            ("⚠️ Invalid parent ID: " ++ parentId).postln;
            ^nil
        }
    }

    removeNode { |nodeId|
        var nodeToRemove, parentNode, found;

        nodeToRemove = nodeMap.at(nodeId);
        parentNode = nodeToRemove.parent;

        if (parentNode.notNil) {
            found = parentNode.children.detect { |c| c === nodeToRemove };
            if (found.notNil) {
                parentNode.removeChildById(found.id);
                nodeMap.removeAt(nodeId);
                ("🗑 Node " ++ nodeId ++ " removed.").postln;
                ^nodeToRemove
            } {
                "⚠️ Node not found in parent's children".postln;
                ^nil
            }
        } {
            "⚠️ Cannot remove root node".postln;
            ^nil
        }
    }

    swapNodes { |nodeId1, nodeId2|
        var node1, node2, parent1, parent2;

        node1 = nodeMap.at(nodeId1);
        node2 = nodeMap.at(nodeId2);
        parent1 = node1.parent;
        parent2 = node2.parent;

        if (parent1.isNil or: { parent2.isNil }) {
            "⚠️ Both nodes must have parents to swap".postln;
            ^nil
        };

        node1 = removeNode(nodeId1);
        node2 = removeNode(nodeId2);

        if (node1.isNil or: { node2.isNil }) {
            "⚠️ Failed to remove nodes for swapping".postln;
            ^nil
        };

        parent1.addChild(node2);
        parent2.addChild(node1);

        "🔄 Nodes swapped".postln;
        ^nil
    }

    getNodeByName { |name|
        var found;
        found = nodeMap.values.detect { |node| node.name == name };
        if (found.notNil) {
            ("🔍 Found node '" ++ found.name ++ "' at ID " ++ found.id).postln;
            ^found
        } {
            "⚠️ Node not found".postln;
            ^nil
        }
    }

    getNodeByNamePath { |nameList|
        var found;
        found = root.getNodeByNamePath(nameList);
        if (found.notNil) {
            ^found
        } {
            ("⚠️ Node not found at path: " ++ nameList.join(" → ")).postln;
            ^nil
        }
    }

    tagDepths {
        root.tagByDepth(0);
        ^this
    }

    rebuildTreeFromDict { |dict|
        var buildNode, rootNode;

        buildNode = { |nodeDict, parent|
            var node;
            node = MDCommandNode.new(
                nodeDict[\name],
                nodeDict[\id],
                nodeDict[\fret]
         );
            node.parent = parent;

            if (parent.notNil) {
                parent.addChild(node);
            };

            nodeMap.put(node.id, node);
            nodeCount = node.id.max(nodeCount);

            nodeDict[\children].do { |childDict|
                buildNode.(childDict, node);
            };

            node
        };

        rootNode = buildNode.(dict, nil);
        ^rootNode
    }

    exportJSONFile { |path|
        var jsonString, file;

        jsonString = JSONlib.convertToJSON(root.asDictRecursively);
        file = File(path, "w");

        if (file.isOpen) {
            file.write(jsonString);
            file.close;
            ("📤 Tree exported to " ++ path).postln;
        } {
            "⚠️ Failed to open file for writing.".warn;
        }
    }

    importJSONFile { |path|
        var jsonString, dict, rebuiltRoot;

        jsonString = File(path, "r").readAllString;
        dict = JSONlib.convertToSC(jsonString);
        rebuiltRoot = rebuildTreeFromDict(dict);
        root = rebuiltRoot;
        ("📥 Tree imported from " ++ path).postln;
    }
}



//older version

// // MDCommandTree.sc
// // MD 20250724
// //
// // Attempt to build a tree class that uses the MDCommandNode class for nodes. (That one will need revisiting and modifying. Among other things CONSIDER REMOVING createChild method)
//
// /*
// This class
// manages the tree: swapNodes, removeNodes, addNodes, load and save
// counts the number of nodes
// will be the main place for adding, removing and swapping nodes
// navigates the tree for retrieval of commands
// copilot suggests nodeMap identity dictionary that stores (id, name) pairs for fast lookup. Seems reasonable.
//
// (copilot suggests integrity validation. Not yet implemented.)
// */
// MDCommandTree {
// 	/*
// 	instance variables:
// 	root: will contain a reference to the node that is the root
// 	nodeLimit (not entirely sure we need it but why not)
// 	nodeCount: needed to give unique IDs to each node as they get created. The creation argument will be nodeCount+1 every tie.
// 	*/
// 	var <> root, <> nodeLimit = 50, <> nodeCount = 0, <> nodeMap;
//
// 	*new {
// 		| rootName = "root", rootId = 0 , nodeLimit |
// 		^super.new.init(rootName, rootId, nodeLimit)
// 	}
//
// 	*fromDict { |dict|
// 		var tree = MDCommandTree.new(dict[\name], dict[\id], dict[\fret]);
// 		tree.root = tree.rebuildTreeFromDict(dict);
// 		^tree
// 	}
//
//
// 	init {
// 		|argName, argId, argLimit  |
// 		// need a rootnode variable and assign a new node to it
// 		this.root = MDCommandNode.new(argName, argId);
// 		this.nodeLimit = argLimit;
// 		this.nodeCount = 1;
// 		// we've just added one node
// 		//assign an identityDictionary to nodeMap
// 		// put the rootnode ID and the node itself in the dictionary
// 		this.nodeMap = IdentityDictionary.new(100);
// 		this.nodeMap.put(argId,this.root);
//
// 		^this // return the tree
// 	}
//
// 	printTreePretty {
// 		this.root.printTreePretty;
// 		^this // return tree
// 	}
//
// 	addNode {
// 		|argParentId, argName, argFret|
// 		var newNode = nil, currentCount, newId, parentNode;
//
// 		//DEBUG
// 		("Incoming args -> parentID: " ++ argParentId ++ ", name: "
// 		++ argName ++ ", fret: " ++ argFret).postln;
//
// 		// generate new unique ID -- that is because we store then in nodeMap which is an IdentityDictionary
// 		newId = this.nodeCount + 1;
//
// 		// get parent node
// 		parentNode = this.nodeMap.at(argParentId);
//
// 		// check that parent is valid
// 		if (parentNode.notNil) {
// 			this.nodeCount = newId;
//
// 			// create new node
// 			newNode = MDCommandNode.new(argName, newId, argFret);
//
// 			// Assign parent reference
// 			newNode.parent = parentNode;
//
// 			// Add child to parent
// 			parentNode.addChild(newNode);
//
// 			// Store node in map
// 			this.nodeMap.put(newId, newNode);
//
// 			^newNode // return the node that was added
// 		}{
// 			postln("Can't create node; invalid parent ID");
// 			^nil
// 		}
// 	}
//
//
// 	removeNode{|argId|
// 		var nodeToRemove, parentNode, found = nil;
//
// 		nodeToRemove = this.nodeMap.at(argId);
//
// 		//DEBUG:
// 		("nodeToRemove is: " + nodeToRemove.name).postln;
//
// 		parentNode = nodeToRemove.parent;
//
//
// 		if (parentNode.notNil){  		// CHECK THAT WE'RE NOT TRYING TO REMOVE root
//
// 			("parentNode is:" + parentNode.name).postln;
// 			("Inside removeNode").postln; //DEBUG
//
// 			// detach the node from parent: find parent, and remove the child
// 			found = parentNode.children.detect {|c| c === nodeToRemove };
// 			if (found.notNil) {
// 				parentNode.removeChildById(found.id);
// 				("Node " + argId + "removed.").postln;
// 				this.nodeMap.removeAt(argId);
// 				^nodeToRemove; // so that we can use it in swapNodes
// 			}{
// 				("parentNode is: nil (probably root)").postln;
// 				("Can't remove root node").postln;
// 				^nil;
// 			}
// 		}{
// 			postln("Parent node is nil (probably root)").postln;
// 			postln("Cannot remove root").postln;
// 			^nil;
// 		}
// 	}
//
//
// 	swapNodes{
// 		| argNodeId1, argNodeId2 |
// 		var  originalNode1, originalNode2, parent1, parent2;
// 		// find nodeId1 and nodeId2 from nodeMap and assign to swap
// 		originalNode1 = this.nodeMap.at(argNodeId1);
// 		originalNode2 = this.nodeMap.at(argNodeId2);
// 		// find the parents using the nodes
// 		parent1 = originalNode1.parent;
// 		parent2 = originalNode2.parent;
//
// 		// if we don't have two parents, abort
// 		if ( parent1.isNil || parent2.isNil) {
// 			postln("Aborting because need two parents to swap");
// 			^nil
// 		};
//
// 		originalNode1 = this.removeNode(argNodeId1);
// 		originalNode2 = this.removeNode(argNodeId2);
// 		// if we don't have two nodes, abort
// 		if ( originalNode1.isNil || originalNode2.isNil) {
// 			postln("Aborting because need two nodes to swap");
// 			^nil
// 		};
//
// 		// attach the nodes to the new parents
// 		parent1.addChild(originalNode2);
// 		parent2.addChild(originalNode1);
//
// 		("Nodes swapped.").postln;
// 		^nil; // wouldn't make sense to return anything else here
// 	}
//
//
// 	///// HELPER METHODS
// 	getNodeByName{
// 		|argName|
// 		var found;
// 		// the nodeMap contains (key,object pairs). Need to detect the object that has the name
// 		// NOW: this wasn't working before adding '.values'. By default detect iterates over keys rather than values (here MDCommandNode instances).
// 		found = nodeMap.values.detect{| node| node.name == argName};
//
// 		if(found.notNil) {
// 			postln("Found node " + found.name + " at ID" + found.id )
// 			^found
// 		}{
// 			postln("Node not found");
// 			^nil
// 		}
// 	}
//
//
// 	getNodeByNamePath{
// 		|argList|
// 		var found = nil;
// 		found = this.root.getNodeByNamePath(argList);
//
// 		if (found.notNil){
// 			^found
// 		}{
// 			postln("Node not found at this path");
// 			^nil
// 		}
// 	}
//
// 	tagDepths{
// 		root.tagByDepth(0);
// 		^this
// 	}
//
//
//
//
//
// 	// added 20250807 to try JSON import/export
// 	rebuildTreeFromDict { |dict|
// 		var buildNode, rootNode;
//
// 		buildNode = { |nodeDict, parent|
// 			var node = MDCommandNode.new(
// 				nodeDict[\name],
// 				nodeDict[\id],
// 				nodeDict[\fret]
// 			);
// 			node.parent = parent;
//
// 			if (parent.notNil) {
// 				parent.addChild(node);
// 				// turn to TRUE to display
// 				if(false){"Attached '% to parent %'".format(node.name, parent.name).postln;}
// 			};
//
// 			this.nodeMap.put(node.id, node);
// 			this.nodeCount = node.id.max(this.nodeCount);
//
// 			nodeDict[\children].do { |childDict|
// 				buildNode.(childDict, node);
// 			};
//
// 			node // ✅ No caret here — just return the node to the caller
// 		};
//
// 		rootNode = buildNode.(dict, nil); // ✅ This is now the actual root
// 		^rootNode; // ✅ Return the root from the outer method
// 	}
//
//
// 	exportJSONFile { |path|
// 		var jsonString, file;
//
// 		jsonString = JSONlib.convertToJSON(this.root.asDictRecursively);
//
// 		file = File(path, "w");
// 		if (file.isOpen) {
// 			file.write(jsonString);
// 			file.close;
// 			"Tree exported to %".format(path).postln;
// 		} {
// 			"Failed to open file for writing.".warn;
// 		};
// 	}
//
// 	importJSONFile { |path|
// 		var jsonString, dict, rebuiltRoot;
//
// 		jsonString = File(path, "r").readAllString;
// 		dict = JSONlib.convertToSC(jsonString);
//
// 		rebuiltRoot = this.rebuildTreeFromDict(dict);
// 		this.root = rebuiltRoot;
//
// 		"Tree imported from %".format(path).postln;
// 	}
//
//
//
// } // end of class
