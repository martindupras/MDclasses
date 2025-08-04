// MDCommandTree.sc
// MD 20250724
//
// Attempt to build a tree class that uses the MDCommandNode class for nodes. (That one will need revisiting and modifying. Among other things CONSIDER REMOVING createChild method)

/*
This class
  manages the tree
  counts the number of nodes
  will be the main place for adding, removing and swapping nodes
  navigatesthe tree for retrieval of commands
  copilot suggests nodeMap identity dictionary that stores (id, name) pairs for fast lookup. Seems reasonable.

(copilot suggests integrity validation. Not yet implemented.)
*/
MDCommandTree {
/*
 instance variables:
    root: will contain a reference to the node that is the root
    nodeLimit (not entirely sure we need it but why not)
    nodeCount: needed to give unique IDs to each node as they get created. The creation argument will be nodeCount+1 every tie.
*/
	var <> root, <> nodeLimit = 50, <> nodeCount = 0, <> nodeMap;

	*new {
		| rootName = "root", rootId = 0 , nodeLimit |
		^super.new.init(rootName, rootId, nodeLimit)
	}

	init {
		|argName, argId, argLimit  |
		// need a rootnode variable and assign a new node to it
		this.root = MDCommandNode.new(argName, argId);
		this.nodeLimit = argLimit;
		this.nodeCount = 1;
		// we've just added one node
		//assign an identityDictionary to nodeMap
		// put the rootnode ID and the node itself in the dictionary
		this.nodeMap = IdentityDictionary.new(100);
		this.nodeMap.put(argId,this.root);

		^this // return the tree
	}

	printTreePretty {
		this.root.printTreePretty;
		^this // return tree
	}

	addNode {
		|argParentId, argName, argFret|
		var newNode = nil, currentCount, newId, parentNode;

		//DEBUG
		("Incoming args -> parentID: " ++ argParentId ++ ", name: "
			++ argName ++ ", fret: " ++ argFret).postln;

		// generate new unique ID -- that is because we store then in nodeMap which is an IdentityDictionary
		newId = this.nodeCount + 1;

		// get parent node
		parentNode = this.nodeMap.at(argParentId);

		// check that parent is valid
		if (parentNode.notNil) {
			this.nodeCount = newId;

			// create new node
			newNode = MDCommandNode.new(argName, newId, argFret);

			// Assign parent reference
			newNode.parent = parentNode;

			// Add child to parent
			parentNode.addChild(newNode);

			// Store node in map
			this.nodeMap.put(newId, newNode);

			^newNode // return the node that was added
		}{
			postln("Can't create node; invalid parent ID");
			^nil
		}
	}


	removeNode{|argId|
		var nodeToRemove, parentNode, found = nil;

		nodeToRemove = this.nodeMap.at(argId);

		//DEBUG:
		("nodeToRemove is: " + nodeToRemove.name).postln;

		parentNode = nodeToRemove.parent;

		// CHECK THAT WE'RE NOT TRYING TO REMOVE root
		if (parentNode.notNil) {
			("parentNode is: " + parentNode.name).postln; // DEBUG
		} {
			("parentNode is: nil (probably root)").postln;
			("Can't remove root node").postln;
			^nil;
		};

		if (parentNode.notNil){
			("parentNode is:" + parentNode.name).postln;
			("Inside removeNode").postln; //DEBUG

			// detach the node from parent: find parent, and remove the child
        found = parentNode.children.detect {|c| c === nodeToRemove };
			if (found.notNil) {
				parentNode.removeChildById(found.id);
				("Node " + argId + "removed.").postln;
				this.nodeMap.removeAt(argId);
				^nodeToRemove; // so that we can use it in swapNodes
			}{
				("Parent node not found").postln;
				^nil;
			}
		}{
			postln("Parent node is nil (probably root)").postln;
			postln("Cannot remove root").postln;
			^nil;
		}
	}


	swapNodes{
		| argNodeId1, argNodeId2 |
		var  originalNode1, originalNode2, parent1, parent2;
		// find nodeId1 and nodeId2 from nodeMap and assign to swap
		originalNode1 = this.nodeMap.at(argNodeId1);
		originalNode2 = this.nodeMap.at(argNodeId2);
		// find the parents using the nodes
		parent1 = originalNode1.parent;
		parent2 = originalNode2.parent;

		// if we don't have two parents, abort
		if ( parent1.isNil || parent2.isNil) {
			postln("Aborting because need two parents to swap");
			^nil
		};

		originalNode1 = this.removeNode(argNodeId1);
		originalNode2 = this.removeNode(argNodeId2);
		// if we don't have two nodes, abort
		if ( originalNode1.isNil || originalNode2.isNil) {
			postln("Aborting because need two nodes to swap");
			^nil
		};

		// attach the nodes to the new parents
		parent1.addChild(originalNode2);
		parent2.addChild(originalNode1);

		("Nodes swapped.").postln;
	}


	///// HELPER METHODS
	getNodeByName{
		|argName|
		var found;
		// the nodeMap contains (key,object pairs). Need to detect the object that has the name
		// NOW: this wasn't working before adding '.values'. By default detect iterates over keys rather than values (here MDCommandNode instances).
		found = nodeMap.values.detect{| node| node.name == argName};

		if(found.notNil) {
			postln("Found node " + found.name + " at ID" + found.id )
			^found
		}{
			postln("Node not found");
			^nil
		}
	}


	getNodeByNamePath{
		|argList|
	var found = nil;
	found = this.root.getNodeByNamePath(argList);

	if (found.notNil){
	^found

		}{
			postln("Node not found at this path")
		}
	}

	tagDepths{
		root.tagByDepth(0);
	}

} // end of class
