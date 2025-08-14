// MDCommandBuilder.sc
// Refactored for clarity and consistency
// MD 20250813

MDCommandBuilder {
	var <>tree, <>currentNode, <>currentCommand, <>navigationPath;

	*new { |argTree| ^super.new.init(argTree); }

	init { |argTree|
		tree = argTree;
		currentNode = tree.root;
		navigationPath = List[0];
		"🔧 CommandBuilder initialized".postln;
		("📐 Depth: " ++ currentNode.depthTag).postln;
		^this
	}

	printChildren {
		var childrenNames;
		if (currentNode.children.notEmpty) {
			currentNode.children.do { |item|
				("🎚 Fret: " ++ item.fret ++ " → " ++ item.name).postln;
			};
			childrenNames = currentNode.children.collect(_.name);
		} {
			"⚠️ No children".postln;
		};
		^childrenNames
	}

	navigateByFret { |stringLevel, fretNumber|
		var nextNode;
		("🎸 Navigating by fret: " ++ fretNumber).postln;
		nextNode = currentNode.getChildByFret(fretNumber);  // ✅ correct method name
		if (nextNode.notNil) {
			currentNode = nextNode;
			navigationPath.add(currentNode.fret);
			("🎯 Current node: " ++ currentNode.name).postln;
		} {
			("⚠️ No child found for fret: " ++ fretNumber).postln;
		};
		^currentNode
	}


	navigateByName { |stringLevel, childName|
		var nextNode = currentNode.getChildByName(childName);
		if (nextNode.notNil) {
			currentNode = nextNode;
			navigationPath.add(currentNode.fret);
			("🎯 Current node: " ++ currentNode.name).postln;
			("📍 Path: " ++ currentNode.getFullPathString).postln;
		} {
			("⚠️ Available children: " ++ currentNode.children.collect(_.name).join(", ")).postln;
		};
		^currentNode
	}

	printPathToRoot {
		currentNode.getPathToRoot.postln;
		^this
	}

	getCurrentName {
		currentNode.name.postln;
		^currentNode.name
	}

	resetNavigation {
		currentNode = tree.root;
		navigationPath = List[0];
		"🔄 Navigation reset".postln;
		^this
	}

	printNavigationPath {
		("🧭 Fret path: " ++ navigationPath).postln;
		^this
	}
}











/////// old version below

//
// // MDCommandBuilder.sc
// // MD 20250801
//
// // The purpose of this class is to build a queue and send it somewhere when ready.
//
//
// MDCommandBuilder{
//
// 	var <> tree, <> currentCommand, <> currentNode, <> fretPath ;
//
// 	*new{
// 		|argTree|
// 		^super.new.init(argTree);
// 	}
//
// 	init {
// 		|argTree|
// 		this.tree = argTree;
// 		this.currentNode = tree.root;
// 		fretPath = List[0]; // I think that should work
// 		"Tree created".postln;
// 		("Depth" + this.currentNode.depthTag).postln;
// 		^this // return a MDCommandBuilder
// 	}
//
// 	listChildren {
// 		var childrenNames;
// 		if(this.currentNode.children.notEmpty)
// 		{
// 			this.currentNode.children.do({|item| postln("Fret: "+item.fret + ":" + item.name;)});
// 			childrenNames =  this.currentNode.children.collect({ |item| item.name });
//
// 		}{"No children".postln;}
// 		^childrenNames // return a list of the children names
// 	}
//
// 	navigateToChild {
// 		|argString, argFret|
// 		// CHECK THAT argString corresponds to current level
// 		postln("Current string level is:"+ this.currentNode.getDepth);
// 		postln("argString is:"+ argString);
// 		postln("argFret is:"+argFret);
// 		this.currentNode = this.currentNode.getChildByfret(argFret);
// 		postln("Current node name is: " +this.currentNode.name);
// 		postln("Selected fret: "+argFret + "  Command:"+ this.currentNode.name);
// 		fretPath.add(this.currentNode.fret);
// 		^this.currentNode // return the child
// 	}
//
// 	// previous version
// 	/*	navigateToChild {
// 	|argFret|
// 	postln("argFret is:"+argFret);
// 	this.currentNode = this.currentNode.getChildByfret(argFret);
// 	postln("Current node name is: " +this.currentNode.name);
// 	postln("Selected fret: "+argFret + "  Command:"+ this.currentNode.name);
// 	fretPath.add(this.currentNode.fret);
// 	^this.currentNode // return the child
// 	}*/
//
// 	navigateToChildByName {
// 		|argString, argName|
//
// 		//TODO: check that argString corresponds to current level
// 		var nextNode;
// 		postln("argName is:"+argName);
// 		nextNode = this.currentNode.getChildByName(argName);
// 		if(nextNode.notNil){
// 			this.currentNode = nextNode;
// 			postln("Current node name is: " +this.currentNode.name);
// 			postln("Selected name: "+argName + "  Command:"+ this.currentNode.name);
// 			fretPath.add(this.currentNode.fret);
//
// 			postln("Path: " + this.currentNode.getFullPathString);			//  Print path
// 		}{
// 			postln("Available children: " + this.currentNode.children.collect(_.name).join(", "));
// 		}
// 		^this.currentNode // return the child
// 	}
//
// 	//previous version:
// 	/*	navigateToChildByName {
// 	|argName|
// 	var nextNode;
// 	postln("argName is:"+argName);
// 	nextNode = this.currentNode.getChildByName(argName);
// 	if(nextNode.notNil){
// 	this.currentNode = nextNode;
// 	postln("Current node name is: " +this.currentNode.name);
// 	postln("Selected name: "+argName + "  Command:"+ this.currentNode.name);
// 	fretPath.add(this.currentNode.fret);
//
// 	postln("Path: " + this.currentNode.getFullPathString);			//  Print path
// 	}{
// 	postln("Available children: " + this.currentNode.children.collect(_.name).join(", "));
// 	}
// 	^this.currentNode // return the child
// 	}*/
//
// 	printCurrentPath{
// 		this.currentNode.getPathToRoot.postln;
// 		^this // return the commandbuilder
// 	}
//
// 	getCurrentCommand{
// 		this.currentNode.name.postln; //show me
// 		^this.currentNode.name // returns a String
// 	}
//
// 	reset{
// 		// we could just invoke init, but this is possibly clearer and we're not nuking tree and making a new one
// 		this.currentNode = tree.root;
// 		this.fretPath = List[0];
// 		^this // return the commandbuilder
// 	}
//
// 	printFretPath {
// 		("Fret path: " ++ fretPath).postln;
// 		^this // return the commandbuilder
// 	}
//
//
// } // end of class MDCommandBuilder