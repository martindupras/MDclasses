// MDCommandMC.sc
// MD 20250806

// The purpose of this class is to handle what is happening with the MIDI input,
// and to call the relevant objects to handle stuff.

MDCommandMC {
    var <> tree; // will hold the tree of commands
    var <> something, <> octave, <> pitchclass, <> gString;
    var <> currentState, <> states; // "states" is the IdentityDictionary that contains the choices
    var <> builder; // the command builder, which traverses the tree
	var <> queue; // the queue to put commands in
	var <> display, displayText;

	// breaks:
	var <> filePath;

    *new {
        ^super.new.init();
    }

    init {
        "MDCommandMC created".postln;

		this.filePath = "~/Command Tree savefiles/myTree.json".standardizePath;
        this.states = IdentityDictionary[
            \idle -> \idle,
            \inTree -> \inTree,
            \inQueue -> \inQueue
        ];

        this.currentState = this.states[\idle];

		// create a new tree but right now it starts empty. We want it to be populated with the JSON file.
        this.tree = MDCommandTree.new("root");
		this.tree.importJSONFile(filePath);
        if (this.tree.notNil) {
            "🔮 Tree created".postln;
			if(true){this.tree.printTreePretty;};
        } {
            "🔮 Couldn't create tree for some reason".postln;
        };

		// create builder that navigates trees
        this.builder = MDCommandBuilder.new(this.tree);
        if (this.builder.notNil) {
            "🔮 Builder created".postln;
        } {
            "🔮 Couldn't create builder for some reason".postln;
        };


		// create command queue
        this.queue = MDCommandQueue.new();
        if (this.queue.notNil) {
            "🔮 Queue created".postln;
        } {
            "🔮 Couldn't create builder for some reason".postln;
        };

		this.display = UserDisplay.new();
        ^this
    }

    handleCommand { |vel, num, chan, srcID|
        octave = (num - 36).div(12).round.asInteger;
        pitchclass = num % 12;
        gString = 6 - octave;

        // Deal with foot controller
        if (srcID == ~launchpad and: chan == 0) {
            switch(num,
                36, {
                    "Reset tree".postln;
                    this.currentState = this.states[\idle];
                    ("State is " ++ this.currentState).postln;
                },
                38, {
                    "Waiting for note".postln;
                    this.currentState = this.states[\inTree];
                    ("State is " ++ this.currentState).postln;

					//display.display(\choices, builder.listChildren);
					//display.display(\choices, displayText)
					{
						displayText = builder.listChildren.collect({ |item, i|
            (i+1).asString ++ ": " ++ item
        }).join("\n");

        display.display(\choices, displayText);
						}.defer; // a GUI thing so needs deferring
                },
                40, { "E2 pressed".postln; },
                { "Other note".postln; }
            );
        };

        // Deal with guitar stand-in (launchpad)
        if (srcID == ~launchpad and: chan == 1) {
            switch(gString,
                1, {
                    ("string1, fret" + pitchclass).postln;
                    switch(pitchclass,
                        0, { "alice".postln; },
                        1, { "bob".postln; },
                        2, { "clara".postln; },
						3, { "david".postln; },
                        4, { "erin".postln; },
                        5, { "fred".postln; },
                        { "unknown".postln; }
                    );
                },
                2, { ("string2, fret" + pitchclass).postln; },
                3, { ("string3, fret" + pitchclass).postln; },
                4, { ("string4, fret" + pitchclass).postln; },
                5, { ("string5, fret" + pitchclass).postln; },
                6, {
                    ("string6, fret" + pitchclass).postln;
                    switch(pitchclass,
                        0, { "6-alice".postln; },
                        1, { "6-bob".postln; },
                        2, { "6-clara".postln; },
                        { "unknown".postln; }
                    );
                },
                { "Other note".postln; }
            );
        };
    }

    print { |vel, num, chan|
        ("vel: " + vel).postln;
        ("num: " + num).postln;
        ("chan: " + chan).postln;
        ^this
    }
}
