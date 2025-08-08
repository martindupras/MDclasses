// testingCodeForDisplay.scd
// MD 20250806



// FOR NOW: this is the list of choices for the user; will need to call tree.getChildren or something.
~currentchoices = ["item1","item2","item3","item4","item5","item6","item7","item8","item9","item10","item11","item12"];

(
// Test the display system
var display = UserDisplay.new;

Routine({
    1.wait;
    {display.display(\state, "waiting")}.defer;
    1.wait;
    {display.display(\queue, "[oscil, pinknoise]")}.defer;
    1.wait;
    {display.display(\lastCommand, "pinknoise")}.defer;
    1.wait;
    {
        var displayText = ~currentchoices.collect({ |item, i|
            (i+1).asString ++ ": " ++ item
        }).join("\n");
        display.display(\choices, displayText);
    }.defer;
}).play;
)


