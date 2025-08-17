// MIDIInputManager.sc
// Refactored from MDMIDISourceManager.sc
// MD 20250814

// Main class to manage MIDI input devices and route messages to appropriate handlers
MIDIInputManager {
	var <>deviceUIDs;         // Dictionary: symbolic name -> srcID
	var <>deviceHandlers;     // Dictionary: srcID -> handler object

	var <> currentMode = \idle; // will store the mode that the foot controller has put us in
	var <>builder, <>queue;
	var <>commandManager;


	// Legacy vars for debugging
	var <>launchpadHandler, <>footControllerHandler, <>guitarHandler, <>launchpadDAWHandler;
	var <>launchpadID, <>footControllerID, <>guitarID, <>launchpadDAWID;

	*new { |builder, launchpad, footController, guitarMIDI, launchpadDAW|
		^super.new.init(builder, launchpad, footController, guitarMIDI, launchpadDAW);
	}

	init { |argBuilder, argLaunchpad, argFootController, argGuitarMIDI, argLaunchpadDAW|
		// is builder passed to anything ever?
		this.builder = argBuilder;
		this.queue = MDCommandQueue.new;

		this.launchpadHandler = argLaunchpad ?? LaunchpadHandler.new;
		this.footControllerHandler = argFootController ?? FootControllerHandler.new(this);
		//DEBUG:
		("🧪 footControllerHandler manager is: " ++ footControllerHandler.manager).postln;

		this.guitarHandler = argGuitarMIDI ?? GuitarMIDIHandler.new(this);
		this.launchpadDAWHandler = argLaunchpadDAW ?? LaunchpadDAWHandler.new;

		MIDIClient.init;
		MIDIIn.connectAll;

		deviceUIDs = Dictionary.new; // store (device, UID) pairs
		deviceHandlers = Dictionary.new; // store (device, handler) pairs

		this.scanDevices;

		launchpadID = this.getSrcID(\Launchpad_Mini_MK3_LPMiniMK3_MIDI_Out);
		launchpadDAWID  = this.getSrcID(\Launchpad_Mini_MK3_LPMiniMK3_DAW_Out); // so that we can filter it out
		footControllerID = this.getSrcID(\nanoKEY2_KEYBOARD);
		guitarID = this.getSrcID(\MD_IAC_to_SC);

		//DEBUG:
		("LaunchpadDAWHandler is: " ++ launchpadDAWHandler).postln;

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
		var srcID, symbol;

		if (keyOrID.isKindOf(Symbol)) {
			srcID = this.getSrcID(keyOrID);
			symbol = keyOrID;
		} {
			srcID = keyOrID;
			symbol = deviceUIDs.keys.detect { |k| deviceUIDs[k] == srcID };
		};

		if (srcID.isNil) {
			("⚠️ Could not bind device: " ++ keyOrID ++ " (srcID is nil)").warn;
			^this;
		};

		if (handler.isNil) {
			("⚠️ No handler provided for srcID: " ++ srcID).warn;
			^this;
		};

		deviceHandlers[srcID] = handler;
		("🔗 Bound %" ++ " (% srcID) to handler %")
		.format(symbol, srcID, handler.class.name)
		.postln;
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

	resetTreeNavigation {
		currentMode = \idle;
		commandManager.currentState = \idle;
		builder.resetNavigation;
		{commandManager.updateDisplay;}.defer;
		"🔄 Tree navigation reset.".postln;
	}

/*	resetTreeNavigation {
		currentMode = \idle;
		builder.resetNavigation;
		//manager.updateDisplay;      // update use display
		"🔄 Tree navigation reset.".postln;
	}*/

	startTreeNavigation {
		currentMode = \inTree;
		commandManager.currentState = \inTree;
		builder.resetNavigation;
		{commandManager.updateDisplay;}.defer;
		"🌲 Tree navigation started.".postln;
	}

/*	startTreeNavigation {
		currentMode = \treeNav;
		builder.resetNavigation;
		//manager.updateDisplay;      // update use display
		"🌲 Tree navigation started.".postln;
	}*/

	addCurrentNodeToQueue {
		var name = builder.getCurrentName;
		commandManager.currentState = \inTree;
		queue.enqueueCommand(name);
		builder.resetNavigation;
		//manager.updateDisplay;      // update use display
		"📥 Added node to queue and restarted navigation.".postln;
	}

	sendQueueAsOSC {
		var path = queue.exportAsOSCPath;
		commandManager.currentState = \inTree;
		// You can send it via NetAddr if needed here
		("📤 Sent queue as OSC: " ++ path).postln;
	}

} // end of MIDIInputManager class


//////////////////////////////////////////////////////
// Previously base class for MIDI input handlers - now just used for unknown devices
// Previously: MDMIDIPreprocessor
//////////////////////////////////////////////////////

MIDIInputHandler {
	var <>manager;

	*new { |manager| ^super.new.init(manager); }

	init { |manager|
		this.manager = manager;
		^this
	}

	handleMessage { |channel, type, value|
		"MIDIInputHandler: % % %".format(channel, type, value).postln;
	}
}


//////////////////////////////////////////////////////
// Launchpad Handler
// Previously: MDLaunchpadPreprocessor
//////////////////////////////////////////////////////

LaunchpadHandler  {
	var <>manager;

	*new { |manager| ^super.new.init(manager); }

	init { |manager|
		this.manager = manager;
		^this
	}

	handleMessage { |channel, type, value|
		"Launchpad: % % %".format(channel, type, value).postln;
		// Future: dispatch to builder
	}
}

// Alias for compatibility
LaunchpadSource : LaunchpadHandler {}

LaunchpadDAWHandler {
	var <>manager;

	*new { |manager| ^super.new.init(manager); }

	init { |manager|
		this.manager = manager;
		^this
	}

	handleMessage { |channel, type, value|
		// DO NOTHING. We want to ignore LaunchpadDAW messages.
		//"IGNORE THIS DEVICE - LaunchpadDAW: % % %".format(channel, type, value).postln;

	}
}
//////////////////////////////////////////////////////
// Foot Controller Handler
// Previously: MDfootControllerPreprocessor
//////////////////////////////////////////////////////

FootControllerHandler {
	var <>manager;

	*new { |manager|
		if (manager.isNil) {
			Error("FootControllerHandler requires a manager").throw;
		};
		^super.new.init(manager);
	}

	init { |manager|
		this.manager = manager;
		("✅ FootControllerHandler received manager: " ++ manager).postln;
		^this
	}

	handleMessage { |channel, type, value|
		("🧪 manager class is: " ++ manager.class).postln;

		if (type === \noteOn) {
			switch (value,
				36, {
					manager.resetTreeNavigation;
					{manager.commandManager.updateDisplay;}.defer;
				},
				38, {
					manager.startTreeNavigation;
					{manager.commandManager.updateDisplay;}.defer;
				},
				40, {
					manager.addCurrentNodeToQueue;
					{manager.commandManager.updateDisplay;}.defer;
				},
				41, { manager.sendQueueAsOSC;
					{manager.commandManager.updateDisplay; }.defer;

				},
				{ ("⚠️ No action for note: " ++ value).postln }
			);
		}
	}
}

// Alias
//FootControllerSource : FootControllerHandler {}

//////////////////////////////////////////////////////
// Guitar MIDI Handler
// Previously: MDMIDIGuitarPreprocessor
//////////////////////////////////////////////////////




GuitarMIDIHandler {
    var <>manager;

    *new { |manager|
        var instance = super.new;
        instance.init(manager);
        ^instance
    }

    init { |manager|
        this.manager = manager;
        ("✅ GuitarMIDIHandler received manager: " ++ manager).postln;
        ^this
    }

    handleMessage { |channel, type, pitch|
        // ✅ Confirm method is being called
        ("📥 handleMessage called with channel: " ++ channel ++ ", type: " ++ type ++ ", pitch: " ++ pitch).postln;

        // ✅ Check type
        if (type === \noteOn) {
            "✅ type is noteOn".postln;
        } {
            "❌ type is not noteOn".postln;
        };

        // ✅ Check current mode
        if (manager.currentMode == \inTree) {
            "✅ currentMode is treeNav".postln;
        } {
            ("❌ currentMode is: " ++ manager.currentMode).postln;
        };

        // ✅ Proceed only if both conditions are met
        if (type === \noteOn and: { manager.currentMode == \inTree }) {
            var stringBasePitches = (
                0: 40, // E string (6th)
                1: 45, // A string (5th)
                2: 50, // D string (4th)
                3: 55, // G string (3rd)
                4: 59, // B string (2nd)
                5: 64  // E string (1st)
            );

            var basePitch = stringBasePitches[channel];
            if (basePitch.notNil) {
                var fret = pitch - basePitch;
                var stringNumber = 6 - channel;

                // ✅ Debug conversion
                ("🎸 Received MIDI note: " ++ pitch ++
                    " on channel: " ++ channel ++
                    " → string: " ++ stringNumber ++
                    ", base pitch: " ++ basePitch ++
                    ", calculated fret: " ++ fret).postln;

                manager.builder.navigateByFret(stringNumber, fret);
            } {
                ("⚠️ Unrecognized channel: " ++ channel ++ ". No base pitch defined.").postln;
            }
        };

        { manager.commandManager.updateDisplay; }.defer;
    }
}



// Alias
//GuitarMIDISource : GuitarMIDIHandler {}
