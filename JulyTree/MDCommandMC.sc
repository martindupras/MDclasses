// MDCommandMC.sc
// MD 20250806

// The purpose of this class is to handle what is happening with the MIDI input,
// and to call the relevant objects to handle stuff.

/*
NEW 20250811 Added some handler methods using polymorphism; see at the end of this file.
I'm not sure if that's the right approach but let's try.

They are:
  MDMIDISource
    MDLaunchpadSource : MDMIDISource
    MDfootControllerSource : MDMIDISource
    MDMIDIGuitarSource : MDMIDISource
*/

// Its business is to create the objects that need to work together, and that when they're' initialised they received the paths that they need.

MDCommandMC {
	var <> tree; // will hold the tree of commands
	var <> something, <> octave, <> pitchclass, <> gString;
	var <> currentState;
	var <> states; // "states" contains the symbols representing the states; may not actually be needed
	var <> builder; // the command builder, which traverses the tree
	var <> queue; // the queue to put commands in
	var <> display, <> displayText;
	var <> filePath;
	var <> midiManager;
	var <> footControllerManager; // THAT NEEDS TO BE WRITTEN

	*new {
		^super.new.init();
	}

	init {
		"MDCommandMC created".postln;

		states = [\idles,\inTree,\inQueue]; //possibly not needed
		this.currentState = \idle; // ADD CHECK TO VERIFY IT'S IN 'states'
//TODO NEXT
		midiManager = MDMIDISourceManager.new(builder, footControllerManager);
//instead of
		this.initMIDI(); // a method defined later in this file

		// SH would use a setter because it makes tracing easier. We can talk about it another time.
		this.filePath = "~/Command Tree savefiles/myTree.json".standardizePath;

		// create a new tree but right now it starts empty. We want it to be populated with the JSON file.
		this.createNewTree();

		// create builder
		this.createBuilder();

        //create queue
		this.createCommandQueue();

		// create user display window
		this.display = UserDisplay.new();

		^this
	} // end of init method for class



	///// new one using MDMIDISourceManager
	initMIDI {
		var midiManager;
		var launchpadHandler, footControllerHandler, guitarMIDIdHandler;
		var launchpadID, footControllerID, guitarID;

		// create the MIDI source manager
		midiManager = MDMIDISourceManager.new;

		// list devices for debugging and to know the symbol names
		midiManager.listDevices;

		// create handler objects
		launchpadHandler = MDLaunchpadSource.new;
		footControllerHandler = MDfootControllerSource.new;
		guitarMIDIdHandler = MDMIDIGuitarSource.new;

		// get symbolic srcIDs to put in dictionary
		launchpadID = midiManager.getSrcID(\LPMiniMK3_MIDI_Out); // get those by using listDevices and copy-pasting from the console
		footControllerID = midiManager.getSrcID(\KEYBOARD); // nanokey2
		guitarID = midiManager.getSrcID(\to_SC); // example

		// connect MIDI device input
		//MIDIIn.connect(0, MIDIClient.sources.detectIndex { |src| src.uid == launchpadID });
		MIDIIn.connectAll;

		// define MIDIdef to receive all MIDI events
		MIDIdef.noteOn(\midiToMC, { |vel, num, chan, srcID|
			switch(srcID,
				launchpadID, {
					launchpadHandler.handleMessage(chan, \noteOn, num);
				},
				footControllerID, {
					footControllerHandler.handleMessage(chan, \noteOn, num);
				},
				guitarID, {
					guitarMIDIdHandler.handleMessage(chan, \noteOn, num);
				},
				{
					("Unknown MIDI source: " + srcID).postln;
				}
			);
		});

	}
	// end initMIDI



	/////
	createNewTree{
		this.tree = MDCommandTree.new("root");
		this.tree.importJSONFile(filePath);
        if (this.tree.notNil) {
            "🔮 Tree created".postln;
			if(true){this.tree.printTreePretty;};
        } {
            "🔮 Couldn't create tree for some reason".postln;
        };
	}

	/////
	createBuilder{
		// create builder that navigates trees
        this.builder = MDCommandBuilder.new(this.tree);
        if (this.builder.notNil) {
            "🔮 Builder created".postln;
        }{
            "🔮 Couldn't create builder for some reason".postln;
        };
	}

	/////
	createCommandQueue{
		// create command queue
        this.queue = MDCommandQueue.new();
        if (this.queue.notNil) {
            "🔮 Queue created".postln;
        } {
            "🔮 Couldn't create builder for some reason".postln;
        };
	}

    print { |vel, num, chan|
        ("vel: " + vel).postln;
        ("num: " + num).postln;
        ("chan: " + chan).postln;
        ^this
    }
} // END OF MDCommandMD class

MDMIDISource {
	*new { ^super.new.init } // turns out we don't need an init method here... yet

	init {
		^this
	}

	handleMessage {|channel, type, value|
		"MDMIDISOURCE: % % %".format(channel, type, value).postln;
	}
} // end of MDMIDISource class


MDLaunchpadSource : MDMIDISource {

	handleMessage {|channel, type, value|
		"Launchpad: % % %".format(channel, type, value).postln;
		// dispatch string and fret to the builder to navigate tree
	}
} // end of MDLaunchpadSource class


MDfootControllerSource : MDMIDISource{

	// dictionary to keep midiNote -> function mappings
 var <> footSwitchActions;

	*new { ^super.new.init }  // we do need .init here!

	init{
		// Map what gets done by each foot switch // IMPORTANT: newFrom wants an ARRAY
		footSwitchActions = Dictionary.newFrom(
			[48, {"C".postln;},
			50, {"D".postln;},
			52, {"E".postln;},
			53, {"F".postln;},
			55, {"G".postln;},
			57, {"A".postln;},
			59, {"B".postln;},
			60, {"C4".postln;},
			62, {"D4".postln;}]
		);
		^this
	}

	handleMessage {|channel, type, value|
		var action;
		// TRUE FOR DEBUGGING
		if (true) {		"Foot controller: % % %".format(channel, type, value).postln; };
		if (type === \noteOn){
			//("Type is noteOn, inside handleMessage of MDfootControllerSource").postln; // works
			("Value is " + value).postln; //


			// TEMP DEACT

		action = footSwitchActions[value]; // get the function at that place in the dictionary
			("action = " + action).postln;
			if (action.notNil){
				action.();
			}{
				("No such command for that note").postln;
			}
		};
	}
} // end of MDLaunchpadSource class


MDMIDIGuitarSource : MDMIDISource {

	handleMessage {|channel, type, value|
		"MIDI Guitar: % % %".format(channel, type, value).postln;
	}
} // end of MDLaunchpadSource class

MDMIDIManager {
	// instantiate MIDIDef

	// this will want to talk to the footControllerManages
	// and the fret manager "builder"

	// when MC creates the MIDI manager, the new takes two params:
	//    the builder
	//    the footControllerManager
	// saves those as instance variables
	// then we don't really need the MDLaunchpadSource classes etc

}