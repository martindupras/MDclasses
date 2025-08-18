// MIDIInputManager.sc
// MD 20250817-1926

MIDIInputManager {
	var <>deviceUIDs;         // Dict: symbolic name -> srcID
	var <>deviceHandlers;     // Dict: srcID -> handler object

	var <> currentMode = \idle; // will store the mode that the foot controller has put us in
	var <> builder, <>queue;
	var <> commandManager;
	var <> modes;
	var <>waitingForString, <>navigationCallback;

	// Legacy vars for debugging
	var <>launchpadHandler, <>footControllerHandler, <>guitarHandler, <>launchpadDAWHandler;
	var <>launchpadID, <>footControllerID, <>guitarID, <>launchpadDAWID;

	*new { |builder, launchpad, footController, guitarMIDI, launchpadDAW|
		^super.new.init(builder, launchpad, footController, guitarMIDI, launchpadDAW);
	}

	init { |argBuilder, argLaunchpad, argFootController, argGuitarMIDI, argLaunchpadDAW|
		// is builder passed to anything ever?
		this.modes = IdentityDictionary[
			\idle -> \idle,
			\prog -> \prog,
			\queue -> \queue,
			\send -> \send,
			\play -> \play,
			\numeric -> \numeric,
			\capture -> \capture,
			\record -> \record
		];

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

	setMode { |newMode|
		currentMode = newMode;
		commandManager.currentState = newMode;
		this.handleModeChange(newMode);
		{ commandManager.updateDisplay }.defer;
	}

	handleModeChange { |mode|
		switch(mode,
			modes[\idle], {
				queue.clear;
				builder.resetNavigation;
				"🔄 Tree navigation reset.".postln;
			},

			modes[\prog], {
				var root;

				builder.resetNavigation;

				root = builder.tree.root; // get the root
				if (root.notNil) {
					root.children.do { |child| ("• " ++ child.name).postln };
				};

				this.startNavigationFromString(6);

				"🌲 Tree navigation started.".postln;
			},

			//---
			modes[\queue], {

				var name = builder.getCurrentName;
				("🧩 Current node name to queue: " ++ name).postln;


				if (builder.isAtLeaf) {
					commandManager.setStatus("🌿 Leaf node reached: " ++ name);
				} {
					commandManager.setStatus("📥 Queued non-leaf node: " ++ name);
				};

				queue.enqueueCommand(name);
				("📦 Queue contents after enqueue: " ++ queue.commandList).postln;
				builder.resetNavigation;
				"📥 Added node to queue and restarted navigation.".postln;
				this.setMode(modes[\prog]); // restart navigation

			},
			//---

			modes[\send], {
				var path = queue.exportAsOSCPath;
				("📋 Queue contents before export: " ++ queue.commandList).postln;

				("📤 Sent queue as OSC: " ++ path).postln;
				queue.clear;
			},
			modes[\play], {
				"🎸 Play mode: no interaction.".postln;
			},
			modes[\numeric], {
				"🔢 Numeric input mode (not yet implemented).".postln;
			},
			modes[\capture], {
				"🎼 Capture mode (not yet implemented).".postln;
			},
			modes[\record], {
				"🔴 Record mode (not yet implemented).".postln;
			}
		);
	}
	startNavigationFromString { |stringNum|
		if (stringNum < 1) {
			builder.navigationComplete = true;
			"✅ Navigation complete.".postln;
			^this;
		};

		this.listenForNoteFromString(stringNum, { |fret|
			builder.navigateByFret(stringNum, fret);
			this.startNavigationFromString(stringNum - 1);
		});
	}

	listenForNoteFromString { |stringNum, callback|
		waitingForString = stringNum;
		navigationCallback = callback;
		("🎧 Waiting for note on string " ++ stringNum).postln;
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
		}{
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

} // end of MIDIInputManager class

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

LaunchpadDAWHandler {
	var <>manager;

	*new { |manager| ^super.new.init(manager); }

	init { |manager|
		this.manager = manager;
		^this
	}

	handleMessage { |channel, type, value|
		// DO NOTHING.
	}
}

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
				36, { manager.setMode(manager.modes[\idle]) },
				38, { manager.setMode(manager.modes[\prog]) },
				40, { manager.setMode(manager.modes[\queue]) },
				41, { manager.setMode(manager.modes[\send]) },
				{ ("⚠️ No action for note: " ++ value).postln }
			);
		}
	}
}

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
		var stringBasePitches, basePitch, fret, stringNumber;

		// ✅ Confirm method is being called
		("📥 handleMessage called with channel: " ++ channel ++ ", type: " ++ type ++ ", pitch: " ++ pitch).postln;

		// ✅ Check type
		if (type === \noteOn) {
			"✅ type is noteOn".postln;
		} {
			"❌ type is not noteOn".postln;
		};

		// ✅ Check current mode
		if (manager.currentMode == manager.modes[\prog]) {
			"✅ currentMode is prog".postln;

			stringBasePitches = (
				0: 40, // E string (6th)
				1: 45, // A
				2: 50, // D
				3: 55, // G
				4: 59, // B
				5: 64  // E (1st)
			);

			basePitch = stringBasePitches[channel];
			if (basePitch.notNil) {
				fret = pitch - basePitch;
				stringNumber = 6 - channel;

				("🎸 Received MIDI note: " ++ pitch ++
					" on channel: " ++ channel ++
					" → string: " ++ stringNumber ++
					", base pitch: " ++ basePitch ++
					", calculated fret: " ++ fret).postln;

				// ✅ Navigation logic
				if (manager.waitingForString == stringNumber) {
					manager.waitingForString = nil;
					manager.navigationCallback.value(fret);
				};
			} {
				("⚠️ Unrecognized channel: " ++ channel ++ ". No base pitch defined.").postln;
			}
		} {
			("❌ currentMode is: " ++ manager.currentMode).postln;
		};

		{ manager.commandManager.updateDisplay; }.defer;
	}
}