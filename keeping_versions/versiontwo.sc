// a first dumb class written from scratch to see if I can do it with no help.


Versiontwo {
	var  <> name;

	*new {|name|
		"version two".postln;
		^super.new.init(name);
	}

	init {|name|
		this.name = name;
	}
}


