//MIDIHandlers.sc
// MD 20250818
// taken out of MIDIInputManager.sc to make smaller file and cleaner organisation.

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