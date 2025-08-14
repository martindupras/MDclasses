MDCommandTree {
    var <>root, <>nodeLimit = 200, <>nodeCount = 0, <>nodeMap;

    *new { |rootName = "root", rootId = 0, nodeLimit|
        ^super.new.init(rootName, rootId, nodeLimit);
    }


	*fromDict { |dict|
    var tree;

    // Use a default node limit, or extract from dict if available
    tree = MDCommandTree.new(dict[\name], dict[\id], dict[\nodeLimit] ?? 200);

    if (dict[\children].isKindOf(Array)) {
        dict[\children].do { |childDict|
            tree.rebuildTreeFromDict(childDict, tree.root);
        };
    };

    ^tree;
}

/*    *fromDict { |dict|
        var tree;

        tree = MDCommandTree.new(dict[\name], dict[\id], dict[\fret]);

        if (dict[\children].isKindOf(Array)) {
            dict[\children].do { |childDict|
                tree.rebuildTreeFromDict(childDict, tree.root);
            };
        };

        ^tree;
    }*/

    init { |rootName, rootId, limit|
        root = MDCommandNode.new(rootName, rootId);
        nodeLimit = limit;
        nodeCount = 1;

        nodeMap = IdentityDictionary.new(100);
        nodeMap.put(rootId, root);

        ^this
    }

    rebuildTreeFromDict { |dict, parent|
        var node;

        node = MDCommandNode.new(dict[\name], dict[\id], dict[\fret]);
        parent.addChild(node);

        nodeMap.put(node.id, node);
        nodeCount = node.id.max(nodeCount);

        if (dict[\children].isKindOf(Array)) {
            dict[\children].do { |childDict|
                this.rebuildTreeFromDict(childDict, node);
            };
        };

        ^node;
    }

    printTreePretty {
        root.printTreePretty;
        ^this;
    }

    tagDepths {
        root.tagByDepth(0);
        ^this;
    }

    getNodeByName { |name|
        var found;
        found = nodeMap.values.detect { |node| node.name == name };
        if (found.notNil) {
            ("🔍 Found node '" ++ found.name ++ "' at ID " ++ found.id).postln;
            ^found
        } {
            "⚠️ Node not found".postln;
            ^nil
        }
    }

    getNodeByNamePath { |nameList|
        var found;
        found = root.getNodeByNamePath(nameList);
        if (found.notNil) {
            ^found
        } {
            ("⚠️ Node not found at path: " ++ nameList.join(" → ")).postln;
            ^nil
        }
    }

    addNode { |parentId, name, fret|
        var newId, parentNode, newNode;

        newId = nodeCount + 1;
        parentNode = nodeMap.at(parentId);

        if (parentNode.notNil) {
            nodeCount = newId;
            newNode = MDCommandNode.new(name, newId, fret);
            newNode.parent = parentNode;
            parentNode.addChild(newNode);
            nodeMap.put(newId, newNode);
            ^newNode
        } {
            ("⚠️ Invalid parent ID: " ++ parentId).postln;
            ^nil
        }
    }

    removeNode { |nodeId|
        var nodeToRemove, parentNode, found;

        nodeToRemove = nodeMap.at(nodeId);
        parentNode = nodeToRemove.parent;

        if (parentNode.notNil) {
            found = parentNode.children.detect { |c| c === nodeToRemove };
            if (found.notNil) {
                parentNode.removeChildById(found.id);
                nodeMap.removeAt(nodeId);
                ("🗑 Node " ++ nodeId ++ " removed.").postln;
                ^nodeToRemove
            } {
                "⚠️ Node not found in parent's children".postln;
                ^nil
            }
        } {
            "⚠️ Cannot remove root node".postln;
            ^nil
        }
    }

    swapNodes { |nodeId1, nodeId2|
        var node1, node2, parent1, parent2;

        node1 = nodeMap.at(nodeId1);
        node2 = nodeMap.at(nodeId2);
        parent1 = node1.parent;
        parent2 = node2.parent;

        if (parent1.isNil or: { parent2.isNil }) {
            "⚠️ Both nodes must have parents to swap".postln;
            ^nil
        };

        node1 = removeNode(nodeId1);
        node2 = removeNode(nodeId2);

        if (node1.isNil or: { node2.isNil }) {
            "⚠️ Failed to remove nodes for swapping".postln;
            ^nil
        };

        parent1.addChild(node2);
        parent2.addChild(node1);

        "🔄 Nodes swapped".postln;
        ^nil
    }

    exportJSONFile { |path|
        var jsonString, file;

        jsonString = JSONlib.convertToJSON(root.asDictRecursively);
        file = File(path, "w");

        if (file.isOpen) {
            file.write(jsonString);
            file.close;
            ("📤 Tree exported to " ++ path).postln;
        } {
            "⚠️ Failed to open file for writing.".warn;
        }
    }

importJSONFile { |path|
    var jsonString, dict, newTree;

    if (File.exists(path).not) {
        "❌ File does not exist: %".format(path).postln;
        ^false;
    };

    jsonString = File(path, "r").readAllString;

    if (jsonString.isNil or: { jsonString.isEmpty }) {
        "⚠️ File is empty or unreadable.".postln;
        ^false;
    };

    dict = JSONlib.convertToSC(jsonString);

    if (dict.isNil) {
        "⚠️ Failed to parse JSON.".postln;
        ^false;
    };

    newTree = MDCommandTree.fromDict(dict);
    this.root = newTree.root;
    this.nodeMap = newTree.nodeMap;
    this.nodeCount = newTree.nodeCount;

    ("📥 Tree imported from " ++ path).postln;
    ^true;
}

}
