NumericNode {

	var <> name, <> children;

	*new {|name = "an MDNode"|
		name.postln; // print the name; we could disable it
		^super.new.init(name);
	}

	init { |argName|
		this.name = argName;
		this.children = List.new() // on initialisation, make this node contain a list of (0) children
	}

	attach {|branchToAdd|
		this.children.add(branchToAdd);
	}

	// TODO (trivial)
	add {
		// take a name as the argument and create and attach the branch in one go
	}


	// // THIS IS WHERE I'M STRUGGLING right now.
	// entryAt{
	// 	|... indices| // indices is array of values
	// 	var result;
	// 	values.do {|idx|
	// 		result = values[idx];
	// 	};
	// 	result.postln;
	// }


	/*
	(~iterate = {
	|... values| // values is an array
	var result;
	values.do {|idx|
		result = values[idx];

	};
	result.postln;
};)

	*/

	entryAtArray{|argArray| // we give this an array and we iterate through the branches until we have gone through the whole array

	}

	isLeaf {
		^this.children.isEmpty; // QUESTION: what does it mean to be a leaf here?
	}

	isEmpty{
		^this.children.isEmpty; // list is empty therefore this node doesn't contain anything.

	}

	list {
		this.children.dump.postln;
	}


	traverse {|level = 0| // assume default when first invoked

		// new approach. Since we will invoke ~root.traverse, ~root is "this"
		level.do{"--".post};
		this.name.postln; // print name
		// not sure about that... ~root is an MDNode. children is a List; .do is a method of List, whcih returns every element in turn
		this.children.do {|child|
			child.traverse(level+1); // I think this is right.
		}
	}


/*		backupWorkingTraverse {
		// new approach. Since we will invoke ~root.traverse, ~root is "this"
		this.name.postln; // print name
		// not sure about that... ~root is an MDNode. children is a List; .do is a method of List, whcih returns every element in turn
		this.children.do {|child|
			child.traverse; // I think this is right.
		}
	}*/


/*
First attempt didn't work
        brokentraverse {|newroot|

		// ok: We need to pass the "root" to traverse from:
		// print name, then
		// invoke traverse on all elements. So we need something that does "for each element of the list do this". What is that method?
		// Let's try this:

		"in traverse".postln;

		newroot.postln;  // show me where we are
		newroot.do { |child|  // then for each child...
			child.traverse;
		}
	}*/
}