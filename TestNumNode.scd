// TestNumNode.scd

// v2 20250714 Works now, using Dictionary instead of List
// MD 20250711


// TODO: add findAt


~root.value; // sanity check for when I restart

~root = NumNode("myroot");
~root.name.value;
~root.rank.value;
~root.children[34].value


~root.addChild("s1f1", 1);
~root.addChild("s1f2", 2);
~root.addChild("s1f3", 3);
~root.addChild("ugens", 7);
~root.addChildAt("abstractions", 9);

~root.children[7].name;
~root.children[7].rank;

~root.children[1].addChild("s5f1", 1);
~root.children[1].addChild("s5f2", 2);
~root.children[1].addChild("s5f3", 3);

~root.children[7].addChild("osc~", 5);
~root.children[7].addChild("pink~", 11);

~root.children[9].addChild("chopper", 1);
~root.children[9].addChild("fooger", 2);

~root.children();
~root.children[7].children();



~root.list;

~root.traverse;

~root.findAt(1,2);
~root.findAt(1,2,3);



