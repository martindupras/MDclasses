// MD 20250716 Now working

// v4 added replaceChild and swap, both of which now work. Phew! That was hard.

// v3 changed children to use SortedList. Added several methods.

// v2 in progress (20250715)
//   adding 	.findAtKeys, .hasChildAtfret, .ChildAtfret(argument)

// v1 working (bu20250714)
// addChild, isLeaf, list, traverse

// TODO:
// add findByName
// add some way to insert by doing someting like ~root.cat("ugens").cat("tremolos").addChild("blah", 7). Give this some thought!

NumNodeSF {
	// each node stores its string number and fret, and a name, and has an  initially empty list containing the children. A leaf would therefore have no children (list empty.)

	var <> string, <> fret, <> name, <> children;

	*new {|name = "an MDNode", fret  = 1, string = 6| // default arguments
		name.postln; // print the name;
		fret.postln;
		^super.new.init(name, fret, string);
	}

	init { |argName, argFret, argString|
		this.fret = argFret;
		this.string = argString;
		this.name = argName;

		// 24 of no consequence here, but just as a "default" to imply 24 frets.
		this.children = SortedList.new(24, {|i,j| i.fret < j.fret}); // on initialisation, a SortedList with a function that says how to do sort on objects of the class to be stored, here NumNodeSFs
	}

//WORKING --
	// consider renaming "createChild"; do some error checking to see that parent actually exists
	addChild {
		|aName, aFret|
		var child, result;
		("Name: "+ aName).postln;
		("Fret: "+ aFret).postln;
		("String: "+ this.string).postln;

		child = NumNodeSF.new(aName, aFret, (this.string-1)); // create new node

		// check for collision
		result = children.detect  { |aChild| aChild.fret == aFret };

		if(result.notNil) {
			"There is already something stored at that fret".postln;
			("Name:"+ result.name).postln;
			("Fret "+ result.fret).postln;
		}{
			this.children.add(child); // and attach it to parent
		};
	}

//WORKING
	replaceChild {
    |aName, aFret|
    var child, result;
    child = NumNodeSF.new(aName, aFret, (this.string - 1));

    result = children.detect { |aChild| aChild.fret == aFret };

    if (result.notNil) {
        "There is already something stored at that fret".postln;
        ("Name: " + result.name).postln;
        ("Fret: " + result.fret).postln;

        this.children.remove(result);
    };

    this.children.add(child); // Always add the new child
}

/*
possible alternative to remove; rebuild list:

this.children = this.children.reject({ |c| c.fret == aFret });
this.children.add(child);
*/

//WORKING
	isLeaf {
		^this.children.isEmpty; // QUESTION: what does it mean to be a leaf here?
	}

//WORKING (although not sure how useful right now)

	list {
    //("Fret number  " + this.fret).postln;
    //("Children class: " + this.children.class).postln;
    //("Children size: " + this.children.size).postln;

    this.children.do { |node|
        ("Node class: " + node.class).postln;
        ("fret: " + node.fret + " -> " + node.name).postln;
    };
    ^nil
}

// WORKING
	traverse {
		|level = 0| // assume default when first invoked
		// new approach. Since we will invoke ~root.traverse, ~root is "this"
		level.do{"--".post};
		this.name.postln; // print name

		this.children.do {|child|
			child.traverse(level+1); // I think this is right.
		}
	}

//WORKING
	hasChildAtFret {
		|aFret|
		var child;
		//DEBUG ("Fret number " + aFret).postln;
		child = this.children.detect({ |aChild| aChild.fret == aFret });
		^child.notNil
	}

//WORKING
	childAtFret {
		|aFret|
		var child;
		//("Fret number " + aFret).postln;
		child = this.children.detect({ |aChild| aChild.fret == aFret });
		if (child.notNil) {
			^child
		} {
			("No child at fret number " + aFret).postln;
			^nil
		}
	}

//WORKING
	findAtKeys{
		|... categories|
		var current;
		"in findAtKeys".postln;
		current = this;
		categories.do({
			|fret|
			current = current.childAtFret(fret);
		};
		)
		^current
	}



	swap{|pathA, pathB|
		var parentA, parentB, nodeA, nodeB, fretA, fretB;

		postln("pathA: " + pathA); // ok
		postln("pathB: " + pathB);

		// set frets
		fretA = pathA.last;
		fretA.postln;              // ok
		fretB = pathB.last;
		fretB.postln;

		// find parents:
		parentA = this.findAtKeys(*(pathA.drop(-1))); //ok
		postln("parentA: " + parentA); // ok
		parentB = this.findAtKeys(*(pathB.drop(-1))); //ok
		postln("parentB: " + parentB); // ok


		// find nodes from parents
		nodeA = parentA.childAtFret(fretA);
		postln("nodeA: " + nodeA); // ok

		nodeB = parentB.childAtFret(fretB);
		postln("nodeB: " + nodeB); // ok

		// remove and store nodes
		parentA.children.remove(nodeA);
		parentB.children.remove(nodeB);

		// THIS IS CRITICAL:
		//if we don't do that lookup never works. Of course. But it wasn't obvious!

		nodeA.fret = fretB;
		nodeB.fret = fretA;
		nodeA.string = parentB.string - 1;
		nodeB.string = parentA.string - 1;

		// check that both nodes exist
		if ((parentA.isNil )||(parentB.isNil)) {
			postln("One or both parents are not found"); // we could be more detailed...
			^nil;
		};

		//put nodeB at parentA
		//put nodeA at parentB
		parentA.children.add(nodeB);
		parentB.children.add(nodeA);

		// console print "done"
		("Swapped " + nodeA.name + " and " + nodeB.name).postln;
	}

// NOT WELL UNDERSTOOD. REVISIT IN FULL.
	printOn {
		|stream|
		stream << "NumNodeSF: "<< this.name;
	}
}











/************************************************
old unused methods kept here for reference only
************************************************/

		// look up .collect, .select, .detect ====
		// >>>
		//   .inspect
		// <<<<<<<

	// UNUSED:
/*	attach {|branchToAdd, fret|
		this.children.put(fret, branchToAdd);
	}*/

// UNUSED:
/*	addChildAt {
		|name, fret|
		var child;
		("Name: "+ name).postln;
		("Fret: "+ fret).postln;
		child = NumNodeSF.new(name, fret); // create new node
		// HERE: what is put for sorted list? And let's check first if there's something already there.
		//this.children.put(fret, child); // and attach it to parent
	}*/


	// GETTING THERE: but I need on each level to iterate through all at that level until we find the right fret and pass that node index to "temp = this.children"
// superseded by findAtKeys
/*	findAt{
		|... indexArray| // array of unknown length
		var temp = this;
		indexArray.do {
			|index|
			// HERE: does that work with SortedList?
			temp = this.children[index]; /// ... but that's not right: we need to query the fret
		};
		temp.name.postln;
	^temp;
	}*/

// UNUSED:
/*	hasChildAtRank {
		|index|
		// HERE: does that work with SortedList?
		(if (this.children.at(index).notNil)
			{"true".postln}
			{"false".postln});
	}*/


/*	oldSwapNotWorking {
		|arrayA, arrayB|
		var newA, newB, temp;
		// testing:
/*		"arrayA"+array[0].postln;
		"arrayB"+array[1].postln;*/

		// find the first one
		//"Before findAtKeys(arrayA)".postln;
		newB = this.findAtKeys(*arrayA);
		("newB: "+ newB.name).postln;
		//"After findAtKeys(arrayA)".postln;
// find the second one
		newA = this.findAtKeys(*arrayB);
		("newA: "+ newA.name).postln;

		// WHAT SHOULD I DO HERE?
	}*/