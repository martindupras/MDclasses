// MD 20250618
// MyTreeNode.sc

// this is now working; use MyTreeNode_test.scd to test.


MyTreeNode {
	var <> nodenum, <> nodename, children;

	*new { |nodenum, nodename|
		var instance = super.new;
		instance.init(nodenum, nodename);
		^instance
	}

	init { |nodenum, nodename|
		this.nodenum = nodenum;
		this.nodename = nodename;
		children = IdentityDictionary.new;
		^this
	}

	addChild { |child|
		children.put(child.nodenum, child);
	}

	addNewChild {
		|nodenum, nodename|
		var child = MyTreeNode.new(nodenum, nodename);
		this.addChild(child);
		^child

	}


	getChild { |nodenum|
		^children.at(nodenum);
	}

	isLeaf {
		^children.isEmpty;
	}

	// traverse the tree from "here", typically the variable that would be ~root
	find {
		|path|
		var current = this;
		path.do {
			|nodenum|
			current = current.getChild(nodenum);
			if (current.isNil) {
				("Node % not found.".format(nodenum)).postln;
			}
		};
		^current;
	}

	printTree { |indent = 0|
		var space = "  ".dup(indent).asString;
		var line = space ++ ("[%] %".format(nodenum, nodename)).asString;
		line.postln;

		children.keysValuesDo { |key, child|
			child.printTree(indent + 1);
		};
	}

	asString {
		^"[%] %".format(nodenum, nodename);
	}
}
