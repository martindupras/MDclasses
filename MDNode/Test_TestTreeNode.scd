~root = TestTreeNode.new(0, "root", 0);
~root.name;  // "root"
~root.index; // 0



~root = TestTreeNode.new(0, "root", 0);
~root.name;  // "root"
~root.index; // 0




~root = TestTreeNode.new(0, "root");


~branch0 = TestTreeNode.new(1, "branch 0");
~branch1 = TestTreeNode.new(2, "branch 1");

~root.addChild(~branch0, 0);
~root.addChild(~branch1, 1);

~root;  // This must return an instance of TestTreeNode, not nil


~root = TestTreeNode.new(0, "root");
~branch0 = TestTreeNode.new(1, "branch 0");
~root.addChild(~branch0, 0);



~myarray = [];
~myarray.value;
~myarray.add(1);
~myarray.put(2,"bob");

// OK we have an issue here.
//Array won't take new values without being grown to accommodate that size. THat means that addChild needs to be able to check size and grow if necessary.'

// List would work, but what happens if list contains holes?

// What about if we allocate arrays of 24 slots beforehand? Then they don't need to grow.