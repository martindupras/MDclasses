MDQueue {

	var  <> name, <> thequeue;

	*new {|name = "aqueue"|
		name.postln;
		^super.new.init(name);
	}

	init {|argName|
		thequeue = List.new();
		this.name = argName;
		// this.name_(argName); alternative way
	}

	enqueue {|argItem|
		thequeue.insert(0, argItem);
	}

	dequeue {
		^thequeue.pop; // returns what we popped
	}

	size {
		this.thequeue.size;
		//thequeue.size; //equivalent
		^thequeue.size;
	}

	get {|argIndex|
		thequeue.at(argIndex).postln;
	}

	clear {
		thequeue.clear;
	}

	// Trying to understand this:
	output{
		this.size.do ({|i,j|
			//this.halt;
			this.dequeue.postln;
			//this.halt;
		}) ; // this doesn't work. Returns an error. Why? ANSWER: thequeue is a List, which does not know about method dequeue
		0;
	}

	output2{
		thequeue.size.do ({|i| thequeue.pop.postln;})  // this works
	}

}



//l.size.do {|i|	l.pop.postln; }