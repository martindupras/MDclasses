// basic.sc

// for classes to work, they need to be in a folder known by SC (e.g. ~/Library/Application Support/SuperCollider/Extensions/MDclasses/) and we need to recompile the class library.

// let's make a new class
// Greeter is the class name
Greeter {
	var >name;  // auto-generates getter + setter (is that right?)

    *new { |name = "world"|
        ^super.new.init(name)
    }

    init { |name|
        this.name = name;  // calls auto-generated setter
        ^this
    }

    greet {
        ("Hello, " ++ name ++ "!").postln;
    }
}


// no semicolons here

