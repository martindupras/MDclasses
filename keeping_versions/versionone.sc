// a first dumb class written from scratch to see if I can do it with no help.


Versionone {
	var  <> name;

	*new {|name|
		"version one".postln;
		^super.new.init(name);
	}

	init {|name|
		this.name = name;
	}
}


