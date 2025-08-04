// MD 20250714 Now working

// v2 in progress (20250715)
//   adding 	.findAtKeys, .hasChildAtRank, .ChildAtRank(argument)

// v1 working (bu20250714)
// addChild, isLeaf, list, traverse

// TODO:
// add findAt (x,y,z); doable? IN PROGRESS
// add by names? Can I find keys by searching symbols?


NumNode {
	var <> rank, <> name, <> children;
//	var <> string, <> fret, <> name, <> children;

	*new {|name = "an MDNode", rank  = 0|
		name.postln; // print the name; we could disable it
		rank.postln;
		^super.new.init(name, rank);
	}

	init { |argName, argRank|
		this.rank = argRank;
		this.name = argName;
		this.children = Dictionary.new; // on initialisation, make a dictionary so that we can store rank
	}

	attach {|branchToAdd, rank|
		this.children.put(rank, branchToAdd);
	}

	// NEW approach
	addChild {
		|name, rank|
		var child;
		("Name: "+ name).postln;
		("Rank: "+ rank).postln;
		child = NumNode.new(name, rank); // create new node
		this.children.put(rank, child); // and attach it to parent
	}
// same with different name
	addChildAt {
		|name, rank|
		var child;
		("Name: "+ name).postln;
		("Rank: "+ rank).postln;
		child = NumNode.new(name, rank); // create new node
		this.children.put(rank, child); // and attach it to parent
	}

	isLeaf {
		^this.children.isEmpty; // QUESTION: what does it mean to be a leaf here?
	}

	list {
		this.children.keysValuesDo {
			|rank, node |
			("Rank:" + rank + "->" + node.name).postln;
		}
	}

	traverse {
		|level = 0| // assume default when first invoked
		// new approach. Since we will invoke ~root.traverse, ~root is "this"
		level.do{"--".post};
		this.name.postln; // print name
		// not sure about that... ~root is an MDNode. children is a List; .do is a method of List, which returns every element in turn
		this.children.do {|child|
			child.traverse(level+1); // I think this is right.
		}
	}

	// LOGIC: for each rank in the array:
	// is there an item at that rank? If not print an error; if yes is it a leaf? If yes return; if not recurisvely call findAt on this rank's child'
	findAt{
		|... indexArray| // array of unknown length
		var i,j,k,l;
		i = indexArray[0];
		this.halt; /////
		j = indexArray[1];
		k = indexArray[2];
		l = indexArray[3];
		//DEBUG "In findAt".postln;
		//DEBUG("indexArray" + indexArray).postln;
		//indexArray[0].postln; // TEST
		"here".postln;
		this.children[i].name.postln;
		this.children[i].children[j].name.postln;
		this.children[i].children[j].children[k].name.postln;
		"there".postln;
	}

	findAtKeys{
		|... categories|

	}


	hasChildAtRank{
		|argument|
	}

	childAtRank{
		|argument|
	}

}