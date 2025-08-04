Ttuple {
	var <> index, <>name;

	*new {|index = 0, name =""|
		^super.new.init(index, name);
	}

	init {|anIndex, aName|
		this.index = anIndex;
		this.name = aName;
	}
}