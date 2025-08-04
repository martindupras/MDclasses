// a first dumb class written from scratch to see if I can do it with no help.


Bob {
	var <> age, <> name;

	*new {|age = 0, name|
		^super.new.init(age, name);
	}

	init {|age, name|
		this.name = name;
		this.age = age;
	}
}


Rita{
	var  age, <> name; // age does not have a getter or setter

	*new {|ageArg, name|
		^super.new.init(ageArg,name);
	}
	init {|ageArg, name|
		this.name = name;
		age = ageArg;
		^this
	}

	age {
		^age
	}
}
