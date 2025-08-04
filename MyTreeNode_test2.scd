~root = nil;
~root.postln;
~root.value;


~root = MyTreeNode.new(0, "root");
~child1 = MyTreeNode.new(1, "child1");
~child2 = MyTreeNode.new(2, "child2");

~root.addChild(~child1);
~root.addChild(~child2);

~root.printTree;



~root = MyTreeNode.new(0, "root");
~root.addNewChild(1, "child1");
~root.addNewChild(2, "child2");
~root.addNewChild(3, "child3");
~root.addNewChild(4, "child4");

~root.printTree;


~root.find([4, 2, 3])


~root = MyTreeNode.new(0, "root");
~child1 = MyTreeNode.new(1, "child");
~root.addChild(~child1);

~root.find([1]); // should return the child node


~root = MyTreeNode.new(0, "root");
~child1 = MyTreeNode.new(1, "child");
~root.addChild(~child1);

~root.find([1]);  // -> [1] child
