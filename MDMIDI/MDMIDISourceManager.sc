// MDMIDISourceManager.sc
// MD 20250812

// class(es) to setup and store symbols for MIDI inputs so that they can be accessed by symbol rather than inhuman numbers.


MDMIDISourceManager { |argBuilder, argLaunchpad, argFootController, argGuitarMIDI |
    var < midiDevicesDict;

    *new {
        ^super.new.init;
    }

    init {
		// start MIDIClient if not started already
        MIDIClient.init;   // start MIDI listening in SC
		MIDIIn.connectAll; // connect all MIDI devices present; filtering will be done by srcID

		// create dict to keep [\symbol, srcID] pairs so that we can recall MIDI devices by name with getSrcID
        midiDevicesDict = Dictionary.new;

		// list the devices and store in the dict
        this.findDevices;

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
			);P
		});

        ^this
    }

    findDevices {
        MIDIClient.midiDevicesDict.do { |src|
            var name = src.name;
            var uid = src.uid;
            var symbol = name.replace(" ", "_").asSymbol;
            midiDevicesDict[symbol] = uid;
        };
    }

    listDevices {
        "🎛 Connected MIDI Devices:".postln;
        midiDevicesDict.keysValuesDo { |symbol, uid|
            ("% => %".format(symbol, uid)).postln;
        };
    }

    getSrcID { |symbol|
        ^midiDevicesDict[symbol] // return the srcID for that symbol
    }

/* NO LONGER NEEDED... I think

// handle the SC MIDI listening stuff
		initMIDI {
		//var midiManager;
		var launchpadHandler, footControllerHandler, guitarMIDIdHandler;
		var launchpadID, footControllerID, guitarID;

		// create the MIDI source manager
		//midiManager = MDMIDISourceManager.new;

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
		//MIDIIn.connect(0, MIDIClient.midiDevicesDict.detectIndex { |src| src.uid == launchpadID });
/*		MIDIIn.connectAll;*/

/*		// define MIDIdef to receive all MIDI events
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
			);P
		});*/

	}
	// end initMIDI*/

}
