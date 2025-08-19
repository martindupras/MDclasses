// martinTreeEditor.scd

// 20250818-1431
// allows editing of the tree step by step.

/*
Loads JSON tree from /Users/martindupras/CommandTreeSavefiles/myTree.json
Displays the tree in the console
Adds branches interactively
Saves the updated tree back to the same file
*/


///// STEP 1 - LOAD
(
// Load tree from JSON file
var path;

path = "/Users/martindupras/CommandTreeSavefiles/myTree.json";
~tree = MDCommandTree.new;
~tree.importJSONFile(path);
"JSON Tree loaded from file.".postln;
)

///// STEP 2 - PRINT
(
// Print the tree structure
~tree.printTreePretty;
)


///// STEP 3 - ADD NODES
(
// Add a node
var parentId = 8;
var nodeName = "bob";
var fret = 11;
var newNode = ~tree.addNode(parentId, nodeName, fret);

if (newNode.notNil) {
    ("✅ Added node '" ++ nodeName ++ "' under parent ID " ++ parentId).postln;
    ~tree.printTreePretty;
} {
    "⚠️ Failed to add node.".postln;
};
)

(
// Remove the node
var nodeIdToRemove = 24;
var removed = ~tree.removeNode(nodeIdToRemove);

if (removed.notNil) {
    ("🗑 Node " ++ nodeIdToRemove ++ " removed.").postln;
} {
    ("⚠️ Failed to remove node " ++ nodeIdToRemove).postln;
};

// Always reprint after change
~tree.printTreePretty;
)


(
// Save tree to JSON file
var savePath;

savePath = "/Users/martindupras/CommandTreeSavefiles/UpdatedTree.json";
~tree.exportJSONFile(savePath);
("Tree saved to:" + savePath).postln;
savePath.postln;
)




// TEST save and reload
(
// Save and reload test
var savePath, reloadedTree;

savePath = "/Users/martindupras/CommandTreeSavefiles/testTree.json";

// Save current tree
~tree.exportJSONFile(savePath);
"Tree saved.".postln;

// Reload into a new tree instance
reloadedTree = MDCommandTree.new;
if (reloadedTree.importJSONFile(savePath)) {
    "Tree reloaded successfully.".postln;
    reloadedTree.printTreePretty;
} {
    "Failed to reload tree.".postln;
};
)






















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
