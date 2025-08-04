TestTreeNode {
	var <>fret, <>name, <>children;

	*new { |fret, name, rank = nil|
		^super.new.init(fret, name)
	}

	init { |fret, name, rank|
		this.fret = fret;
		this.name = name;
		this.rank = rank;
		this.children = Array.new;
		^this
	}

/*	addChild { |child, atfret|
		child.rank = atfret;
		children.put(atfret, child);
	}*/

	// atPath { |path|
	// 	var node = this;
	// 	path.do { |i|
	// 		node = node.children[i];
	// 		if (node.isNil) {
	// 			^"Path invalid at fret %".format(i);
	// 		}
	// 	};
	// 	^node;
	// }

/*	valueArray { |indices|
		^this.atPath(indices).name;
	}*/

/*	 printTree { |indent = 0|
		(indent.asString.padLeft(indent, " ") ++ "- " ++ name).postln;
		children.do { |child|
			if (child.notNil) {
				child.printTree(indent + 2);
			}
		};
	}*/
}
