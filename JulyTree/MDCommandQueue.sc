// MDCommandQueue.sc
// Refactored for clarity and consistency
// MD 20250813

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
        var oscPath = "/" ++ commandList.join("/");
        ~commandToSend = oscPath;
        ("🚀 Exported OSC path: " ++ oscPath).postln;
        ^oscPath
    }
}












// old version


/*// MDCommandQueue.sc
// MD 20250801


// The purpose of this class is to enqueud the commands found in the tree; when the queue is complete, we invoke .exportToEnv which outputs the queue to the environment variable ~commandToSend. It will then be sent to PD to build a graph, eventually to SC instead.

MDCommandQueue{

	var <> queue;

	*new{
		^super.new.init();
	}

	init{
		this.queue = List.new(8);
		"Queue created".postln;
		^this
	}

	addCommand{
		|argCommand|
		this.queue.add(argCommand);
		postln("Command added to queue");
		postln(this.queue);
		^queue
	}

	removeLastCommand{
		if(this.queue.notEmpty){
			this.queue.removeAt(this.queue.size-1); // should remove last element of the list
			postln("Last command removed");
		}{
			postln("No command in queue");
		}
	}


	exportToEnv{
		~commandToSend = "/" ++ queue.join("/"); // make it OSC formatted
		~commandToSend.postln;
	}

} // end of class MDCommandQueue*/