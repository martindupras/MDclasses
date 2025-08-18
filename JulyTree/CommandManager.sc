// CommandManager.sc
// Refactored from MDCommandMC.sc
// MD 20250817-1927

//previously known as MDCommandMC
CommandManager {
	var something, octave, pitchclass, gString;
	var <> currentState; // getter and setters for outside
	var states;
	var <>tree;
	var <>builder;
	var <>queue;
	var <>display, <>displayText;
	var filePath;
	var <>midiManager;
	var <>commandManager;


	var launchpadHandler, footControllerHandler, guitarHandler;
	var <>launchpadID, <>footControllerID, <>guitarID;

	*new {
		^super.new.init;
	}

	init {
		if (true) { "CommandManager created".postln };

		// states = [\idle, \inTree, \inQueue];
		currentState = \idle;

		filePath = "~/CommandTreeSavefiles/myTree.json".standardizePath;

		this.createNewTree;
		this.createBuilder;
		this.createCommandQueue;
		display = UserDisplay.new;

		midiManager = MIDIInputManager.new(builder, nil, nil, nil);
		midiManager.commandManager = this;


/*        launchpadHandler = LaunchpadHandler.new;
        footControllerHandler = FootControllerHandler.new;
        guitarHandler = GuitarMIDIHandler.new;

        midiManager = MIDIInputManager.new(builder, launchpadHandler, footControllerHandler, guitarHandler);*/

		^this
	}


	createNewTree {
		tree = MDCommandTree.new("root");
		tree.importJSONFile(filePath);
		if (tree.notNil) {
			"🔮 Tree created".postln;
			if (true) { tree.printTreePretty };
		} {
			"🔮 Couldn't create tree".postln;
		}
	}

	createBuilder {
		builder = MDCommandBuilder.new(tree);
		if (builder.notNil) {
			"🔮 Builder created".postln;
		} {
			"🔮 Couldn't create builder".postln;
		}
	}

	createCommandQueue {
		queue = MDCommandQueue.new;
		if (queue.notNil) {
			"🔮 Queue created".postln;
		} {
			"🔮 Couldn't create queue".postln;
		}
	}

	print { |vel, num, chan|
		("vel: " + vel).postln;
		("num: " + num).postln;
		("chan: " + chan).postln;
		^this
	}

	updateDisplay {
		var stateText, choicesText, children;

		// Show current state
		stateText = "🧭 Mode: " ++ currentState.asString;
		choicesText = "⚠️ No choices available.";

		if (currentState == \prog) {
			children = builder.currentNode.children;

			if (builder.isAtLeaf) {
				choicesText = choicesText ++ "\n🌿 Leaf node reached.";
			};

			if (children.notEmpty) {
				children.do { |c|
					("🧪 Child: " ++ c.name ++ ", payload: " ++ c.payload).postln;
				};

				choicesText = "🎚 Current Node: " ++ builder.currentNode.name ++ "\n\n" ++
				"📦 Available Choices:\n" ++
				children.collect { |c|
					"• Fret " ++ c.fret ++ " → " ++ c.name ++ " (payload: " ++ c.payload ++ ")"
				}.join("\n");
			} {
				choicesText = "🎚 Current Node: " ++ builder.currentNode.name ++ "\n⚠️ No available choices.";
			};
		} {
			choicesText = "";
		};

		("🖥 Updating display...").postln;
		("State text: " ++ stateText).postln;
		("Choices text: " ++ choicesText).postln;

		// Update individual display fields
		{display.stateText.string = stateText;}.defer;
		{display.userChoicesText.string = choicesText;}.defer;
	}

/*	updateDisplay {
		var stateText, choicesText, children;

		// Show current state

		stateText = "🧭 Mode: " ++ currentState.asString;

		choicesText = "⚠️ No choices available.";

		if (currentState == \prog) {
			children = builder.currentNode.children;
			if (builder.isAtLeaf) {
				choicesText = choicesText ++ "\n🌿 Leaf node reached.";
			};


			if (children.notEmpty) {
				choicesText = "🎚 Current Node: " ++ builder.currentNode.name ++ "\n\n" ++
				"📦 Available Choices:\n" ++
				children.collect { |c|
					"• Fret " ++ c.fret ++ " → " ++ c.name
				}.join("\n");
			} {
				choicesText = "🎚 Current Node: " ++ builder.currentNode.name ++ "\n⚠️ No available choices.";
			};
		} {
			choicesText = "";
		};


		("🖥 Updating display...").postln;
		("State text: " ++ stateText).postln;
		("Choices text: " ++ choicesText).postln;

		// Update individual display fields
		{display.stateText.string = stateText;}.defer;
		{display.userChoicesText.string = choicesText;}.defer;
	}*/




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