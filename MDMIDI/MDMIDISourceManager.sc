// MDMIDISourceManager.sc
// MD 20250812

// class(es) to setup and store symbols for MIDI inputs so that they can be accessed by symbol rather than inhuman numbers.


MDMIDISourceManager {
    var < sources;

    *new {
        ^super.new.init;
    }

    init {
		// start MIDIClient if not started already
        MIDIClient.init;

		// create dict to keep [\symbol, srcID] pairs
        sources = Dictionary.new;

		// list the devices and store in the dict
        this.findDevices;

        ^this
    }

    findDevices {
        MIDIClient.sources.do { |src|
            var name = src.name;
            var uid = src.uid;
            var symbol = name.replace(" ", "_").asSymbol;
            sources[symbol] = uid;
        };
    }

    listDevices {
        "🎛 Connected MIDI Devices:".postln;
        sources.keysValuesDo { |symbol, uid|
            ("% => %".format(symbol, uid)).postln;
        };
    }

    getSrcID { |symbol|
        ^sources[symbol] // return the srcID for that symbol
    }
}
