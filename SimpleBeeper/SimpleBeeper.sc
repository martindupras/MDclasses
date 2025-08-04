// SimpleBeeper.sc

SimpleBeeper {
	var <> freq;

	*new { |freq = 345|
		^super.new.init(freq)
	}

	init{|argFreq|
		this.freq = argFreq;

	}

	beep{
		SynthDef("beeper",
		{Out.ar(0, (EnvGen.ar(Env.perc())*SinOsc.ar(this.freq)));}
	).play;
	}
}