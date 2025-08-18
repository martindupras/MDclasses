// MDCommandQueue.sc
// Refactored for clarity and consistency
// MD 20250818

MDCommandQueue {
    var <>commandList;

    *new { ^super.new.init(); }

    init {
        commandList = List.new(8);
        "📦 CommandQueue initialized".postln;
        ^this
    }

    enqueueCommand { |command|
        commandList.add(command);
        ("✅ Command added: " ++ command).postln;
        ("📋 Current queue: " ++ commandList).postln;
        ^commandList
    }

    dequeueLastCommand {
        if (commandList.notEmpty) {
            commandList.removeAt(commandList.size - 1);
            "🗑 Last command removed".postln;
        } {
            "⚠️ No command to remove".postln;
        };
        ^commandList
    }

    clearQueue {
        commandList.clear;
        "🧹 Queue cleared".postln;
        ^this
    }

	exportAsOSCPath {
    var oscPath;

    oscPath = commandList.collect { |cmd|
        if (cmd.isKindOf(MDCommandNode)) {
            var name = cmd.name.asString;
            var payload = cmd.payload.notNil.if { "_" ++ cmd.payload.asString } { "" };
            name ++ payload;
        } {
            "unknown"
        }
    }.join("/").prefix("/");

    ~commandToSend = oscPath;
    ("🚀 Exported OSC path: " ++ oscPath).postln;
    ^oscPath
}

/*    exportAsOSCPath {
        var oscPath = "/" ++ commandList.join("/");
        ~commandToSend = oscPath;
        ("🚀 Exported OSC path: " ++ oscPath).postln;
        ^oscPath
    }*/
}