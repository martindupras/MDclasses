// MIDIInputManager.sc
// Refactored from MDMIDISourceManager.sc
// MD 20250813

// Main class to manage MIDI input devices and route messages to appropriate handlers
MIDIInputManager {
	var <>deviceUIDs;         // Dictionary: symbolic name -> srcID
	var <>deviceHandlers;     // Dictionary: srcID -> handler object

	// Legacy vars for debugging
	var <>launchpadHandler, <>footControllerHandler, <>guitarHandler, <>launchpadDAWHandler;
	var <>launchpadID, <>launchpadDAWID, <>footControllerID, <>guitarID;

	*new { |builder, launchpad, footController, guitarMIDI, launchpadDAW|
		^super.new.init(builder, launchpad, footController, guitarMIDI, launchpadDAW);
	}

	init { |builder, launchpad, footController, guitarMIDI, launchpadDAW|
		// is builder passed to anything ever?
		this.launchpadHandler = launchpad;
		this.footControllerHandler = footController;
		this.guitarHandler = guitarMIDI;
		this.launchpadDAWHandler = launchpadDAW;


		MIDIClient.init;
		MIDIIn.connectAll;

		deviceUIDs = Dictionary.new; // store (device, UID) pairs
		deviceHandlers = Dictionary.new; // store (device, handler) pairs

		this.scanDevices;

		launchpadID = this.getSrcID(\Launchpad_Mini_MK3_LPMiniMK3_MIDI_Out);
		launchpadDAWID  = this.getSrcID(\Launchpad_Mini_MK3_LPMiniMK3_DAW_Out); // so that we can filter it out
		footControllerID = this.getSrcID(\nanoKEY2_KEYBOARD);
		guitarID = this.getSrcID(\MD_IAC_to_SC);

		this.bindDevice(launchpadID, launchpadHandler);
		this.bindDevice(footControllerID, footControllerHandler);
		this.bindDevice(guitarID, guitarHandler);
		this.bindDevice(launchpadDAWID, launchpadDAWHandler);

		this.setupMIDIDef;
		^this
	}


	scanDevices {
		MIDIClient.sources.do { |src|
			var symbol = (src.device ++ "_" ++ src.name)
			.replace(" ", "_")
			.replace("-", "_")
			.asSymbol;
			deviceUIDs[symbol] = src.uid;
		};
	}

	listDevices {
		"🎛 Connected MIDI Devices:".postln;
		deviceUIDs.keysValuesDo { |symbol, uid|
			("% => %".format(symbol, uid)).postln;
		};
	}

	getSrcID { |symbol|
		^deviceUIDs[symbol];
	}

	listDeviceSymbols {
		deviceUIDs.keysValuesDo { |symbol, uid|
			("Symbol: " ++ symbol ++ " → UID: " ++ uid).postln;
		};
	}

	bindDevice { |keyOrID, handler|
		var srcID;

		if (keyOrID.isKindOf(Symbol)) {
			srcID = this.getSrcID(keyOrID);
		} {
			srcID = keyOrID;
		};

		if (srcID.notNil) {
			deviceHandlers[srcID] = handler;
			("🔗 Bound srcID % to handler %".format(srcID, handler.class.name)).postln;
		} {
			("⚠️ Could not bind device: " ++ keyOrID).warn;
		}
	}

	setupMIDIDef {
		MIDIdef.noteOn(\midiToManager, { |vel, num, chan, srcID|
			var handler = deviceHandlers[srcID];
			if (handler.notNil) {
				handler.handleMessage(chan, \noteOn, num);
			} {
				("⚠️ No handler bound for srcID: " + srcID).postln;
			}
		});
	}
} // end of MIDIInputManager class





//////////////////////////////////////////////////////
// Base class for MIDI input handlers
// Previously: MDMIDIPreprocessor
//////////////////////////////////////////////////////

MIDIInputHandler {
	*new { ^super.new.init }

	init { ^this }

	handleMessage { |channel, type, value|
		"MIDIInputHandler: % % %".format(channel, type, value).postln;
	}
}

//////////////////////////////////////////////////////
// Launchpad Handler
// Previously: MDLaunchpadPreprocessor
//////////////////////////////////////////////////////

LaunchpadHandler : MIDIInputHandler {
	handleMessage { |channel, type, value|
		"Launchpad: % % %".format(channel, type, value).postln;
		// Future: dispatch to builder
	}
}

// Alias for compatibility
LaunchpadSource : LaunchpadHandler {}

LaunchpadDAWHandler : MIDIInputHandler {
	handleMessage { |channel, type, value|
		"Launchpad: % % %".format(channel, type, value).postln;
		// Future: dispatch to builder
	}
}
//////////////////////////////////////////////////////
// Foot Controller Handler
// Previously: MDfootControllerPreprocessor
//////////////////////////////////////////////////////

FootControllerHandler : MIDIInputHandler {
	var <>noteActions;

	*new { ^super.new.init }

	init {
		noteActions = Dictionary.newFrom([
			48, { "C".postln },
			50, { "D".postln },
			52, { "E".postln },
			53, { "F".postln },
			55, { "G".postln },
			57, { "A".postln },
			59, { "B".postln },
			60, { "C4".postln },
			62, { "D4".postln }
		]);
		^this
	}

	handleMessage { |channel, type, value|
		var action;
		if (type === \noteOn) {
			("Foot controller: % % %".format(channel, type, value)).postln;
			action = noteActions[value];
			if (action.notNil) {
				action.();
			} {
				("No command for note: " + value).postln;
			}
		}
	}
}

// Alias
FootControllerSource : FootControllerHandler {}

//////////////////////////////////////////////////////
// Guitar MIDI Handler
// Previously: MDMIDIGuitarPreprocessor
//////////////////////////////////////////////////////

GuitarMIDIHandler : MIDIInputHandler {
	handleMessage { |channel, type, pitch|
		if (type === \noteOn)
	{ var stringNum;
	stringNum = 6 - channel; // Channels are 1-6, strings are 6 to 1
		//"MIDI Guitar: % % %".format(channel, type, value).postln;

		"String %: Pitch %".format(stringNum, pitch).postln;
	}{
			"Type was %, note \noteOn".format(type).postln;
		}

	}
}

// Alias
GuitarMIDISource : GuitarMIDIHandler {}
