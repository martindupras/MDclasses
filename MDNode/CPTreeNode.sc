CPTreeNode {
    var <>name, <>rank, <>children;

    *newRoot { |name, rank|
        ^super.new.init(name, rank)
    }

    init { |name, rank|
        this.name = name;
        this.rank = rank.clip(1, 24); // Ensure rank is between 1 and 24
        children = List.new;
        ^this
    }

    addChild { |name, rank|
        var child = CPTreeNode.new.init(name, rank);
        children.add(child);
        ^child
    }

    findOrCreatePath { |pathArray, rankFunc|
        var current = this;
        pathArray.do { |nodeName, i|
            var existing = current.children.detect { |c| c.name == nodeName };
            if (existing.notNil) {
                current = existing;
            } {
                var newRank = rankFunc.value(nodeName, i);
                current = current.addChild(nodeName, newRank);
            }
        };
        ^current
    }

printTree { |indent = 0|
    ("  ".dup(indent).join) ++ "Name: % Rank: %".format(name, rank).postln;
    children.do { |child| child.printTree(indent + 1) };
}

}

