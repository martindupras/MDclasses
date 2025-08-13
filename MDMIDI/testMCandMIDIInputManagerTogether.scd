// testMCandMIDIInputManagerTogether.scd
// MD 20250813-1213
// Test suite for CommandManager and MIDIInputManager integration
(
var commandManager = CommandManager.new;

// 1. Confirm initialization
"✅ CommandManager initialized".postln;

// 2. Check internal components
// Safe access to currentState
if (commandManager.respondsTo(\currentState)) {
    ("Current state: " ++ commandManager.currentState).postln;
} {
    "⚠️ Could not access currentState — add `<>` to the var in CommandManager if needed.".warn;
};

("Tree: " ++ commandManager.tree).postln;
("Builder: " ++ commandManager.builder).postln;
("Queue: " ++ commandManager.queue).postln;

// 3. List MIDI devices and symbols
"🎛 Connected MIDI Devices:".postln;
commandManager.midiManager.listDevices;

"🎛 Available device symbols:".postln;
commandManager.midiManager.deviceUIDs.keys.do { |key| ("  → " ++ key).postln; };

// 4. Simulate MIDI noteOn messages from each handler (if IDs are valid)
"--- Simulating Launchpad noteOn ---".postln;
if (commandManager.midiManager.launchpadID.notNil) {
    commandManager.midiManager.deviceHandlers[commandManager.midiManager.launchpadID].handleMessage(1, \noteOn, 64);
} {
    "⚠️ Launchpad ID not found or not bound.".warn;
};

"--- Simulating Foot Controller noteOn ---".postln;
if (commandManager.midiManager.footControllerID.notNil) {
    commandManager.midiManager.deviceHandlers[commandManager.midiManager.footControllerID].handleMessage(1, \noteOn, 60); // Should print "C4"
} {
    "⚠️ Foot Controller ID not found or not bound.".warn;
};

"--- Simulating Guitar MIDI noteOn ---".postln;
if (commandManager.midiManager.guitarID.notNil) {
    commandManager.midiManager.deviceHandlers[commandManager.midiManager.guitarID].handleMessage(1, \noteOn, 45);
} {
    "⚠️ Guitar ID not found or not bound.".warn;
};

// 5. Print device symbol-to-UID mapping
"🎛 Device symbol → UID mapping:".postln;
commandManager.midiManager.listDeviceSymbols;
)
