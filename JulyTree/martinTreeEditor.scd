// martinTreeEditor.scd

// 20250818-1431
// allows editing of the tree step by step.

/*
Loads your JSON tree from /Users/martindupras/CommandTreeSavefiles/myTree.json
Displays the tree in the console
Lets you add branches interactively
Saves the updated tree back to the same file
*/

// load up tree:
// Load the tree from a JSON file


///// STEP 1 - LOAD
(
// Load tree from JSON file
var path;

path = "/Users/martindupras/CommandTreeSavefiles/myTree.json";
~tree = MDCommandTree.new;
~tree.importJSONFile(path);
"✅ Tree loaded.".postln;
)

///// STEP 2 - PRINT
(
// Print the tree structure
~tree.printTreePretty;
)


///// STEP 3 - ADD NODES
(
// Add a node to the tree
var parentId, nodeName, fret, newNode;

parentId = 19; // ← Replace with actual parent node ID
nodeName = "audio"; // ← Replace with desired name
fret = 7; // ← Replace with desired fret value

newNode = ~tree.addNode(parentId, nodeName, fret);
if (newNode.notNil) {
    ("✅ Added node '" ++ nodeName ++ "' under parent ID " ++ parentId).postln;
    ~tree.printTreePretty;
} {
    "⚠️ Failed to add node.".postln;
};
)

(
// Save tree to JSON file
var savePath;

savePath = "/Users/martindupras/CommandTreeSavefiles/UpdatedTree.json"; // ← Change if needed
~tree.exportJSONFile(savePath);
"💾 Tree saved to: ".postln;
savePath.postln;
)



MDCommandNode.printTreePretty.sourceCode.postln;
~tree.printTreePretty;






















~tree;

(
// Load the tree from JSON
var filePath;
filePath = "/Users/martindupras/CommandTreeSavefiles/myTree.json";

~tree = MDCommandTree.new;
if (~tree.importJSONFile(filePath)) {
    "✅ Tree loaded successfully.".postln;
    ~tree.printTreePretty;
} {
    "❌ Failed to load tree.".postln;
};
)
// === Add branches interactively ===
// Example: Add a branch to node named "A"
var parentNode = tree.findNodeByName("A");
if (parentNode.notNil) {
    var newNode = tree.addNode(parentNode.id, "A_newBranch", 7);
    ("✅ Added branch to 'A': " ++ newNode.name ++ " (ID: " ++ newNode.id ++ ")").postln;
    tree.printTreePretty;
} {
    "⚠️ Parent node 'A' not found.".postln;
};

// === Save the updated tree ===
tree.exportJSONFile(filePath);
"💾 Tree saved to file.".postln;
)
