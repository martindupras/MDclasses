// CommandManager.sc
// Refactored from MDCommandMC.sc
// MD 20250817-1927

//previously known as MDCommandMC
CommandManager {
	//var something, octave, pitchclass, gString; // no longer used
	//var states;
	var <> currentState; // getter and setters for outside

	var <>tree;
	var <>builder;
	var <>queue;
	var <>display, <>displayText;
	var filePath;
	var <>midiManager;
	var <>parentCommandManager;


	var launchpadHandler, footControllerHandler, guitarHandler;
	var <>launchpadID, <>footControllerID, <>guitarID;

	*new {
		^super.new.init;
	}


	init {
		//DEBUG
		if (true) { "CommandManager created".postln };

		currentState = \idle;

		filePath = "~/CommandTreeSavefiles/myTree.json".standardizePath;


		this.createNewTree;
		this.createBuilder;
		this.createCommandQueue;

		display = UserDisplay.new;

		midiManager = MIDIInputManager.new(builder, nil, nil, nil);
		midiManager.parentCommandManager = this;

		^this
	}


	createNewTree {
		tree = MDCommandTree.new("root");
		tree.importJSONFile(filePath);
		if (tree.notNil) {
			"🔮 Tree created".postln;
			//DEBUG
			if (true) { tree.printTreePretty };
		} {
			"🔮 Couldn't create tree".postln;
		}
	}

	createBuilder {
		builder = MDCommandBuilder.new(tree);
		if (builder.notNil) {
			//DEBUG
			if (true) {"🔮 Builder created".postln};
		} {
			"🔮 Couldn't create builder".postln;
		}
	}

	createCommandQueue {
		queue = MDCommandQueue.new;
		if (queue.notNil) {
			//DEBUG
			if (true) {"🔮 Queue created".postln};
		} {
			"🔮 Couldn't create queue".postln;
		}
	}

	// previously only called "print"
	printMIDIInfo { |vel, num, chan|
		("vel: " + vel).postln;
		("num: " + num).postln;
		("chan: " + chan).postln;
		^this
	}

	updateDisplay {
		var modeText, nodeChoicesText, children;

		// Show current state
		modeText = "🧭 Mode: " ++ currentState.asString;
		nodeChoicesText = "⚠️ No choices available.";

		if (currentState == \prog) {
			children = builder.currentNode.children;

			if (builder.isAtLeaf) {
				nodeChoicesText = nodeChoicesText ++ "\n🌿 Leaf node reached.";
			};

			if (children.notEmpty) {
				children.do { |c|
					("🧪 Child: " ++ c.name ++ ", payload: " ++ c.payload).postln;
				};

				nodeChoicesText = "🎚 Current Node: " ++ builder.currentNode.name ++ "\n\n" ++
				"📦 Available Choices:\n" ++
				children.collect { |c|
					"• Fret " ++ c.fret ++ " → " ++ c.name ++ " (payload: " ++ c.payload ++ ")"
				}.join("\n");
			} {
				nodeChoicesText = "🎚 Current Node: " ++ builder.currentNode.name ++ "\n⚠️ No available choices.";
			};
		} {
			nodeChoicesText = "";
		};

		("🖥 Updating display...").postln;
		("State text: " ++ modeText).postln;
		("Choices text: " ++ nodeChoicesText).postln;

		// Update individual display fields
		{display.modeText.string = modeText;}.defer;
		{display.userChoicesText.string = nodeChoicesText;}.defer;
	}


	setStatus { |text|
		if (display.notNil) {
			display.updateStatus(text);
		} {
			("⚠️ Display not available. Status: " ++ text).postln;
		}
	}

}

// alias to old name
MDCommandMC : CommandManager {}