// Some things work... but I'm not entirely sure I got this right.

~root = nil;

~root.value; // sanity check for when I restart


~root = MDNode("myroot");
~root.list;
~branch1 = MDNode("branch1");
~branch2 = MDNode("branch2");
~branch3 = MDNode("branch3");
~branch4 = MDNode("branch4");
~root.list;

~root.attachAt(1, ~branch1);
~root.attachAt(3, ~branch2);
~root.attachAt(5, ~branch3);
~root.attachAt(7, ~branch4);
~root.list;


~root.name;
~root.children[0].name; // ok this works
~root.children[1].name;

~b1sub1 = MDNode("b1sub1");
~b1sub2 = MDNode("b1sub2");
~b1sub3 = MDNode("b1sub3");

~branch1.attach(~b1sub1);
~branch1.attach(~b1sub2);
~branch1.attach(~b1sub3);

~b2sub1 = MDNode("b2sub1");
~b2sub2 = MDNode("b2sub2");
~b2sub3 = MDNode("b2sub3");

~branch2.attach(~b2sub1);
~branch2.attach(~b2sub2);
~branch2.attach(~b2sub3);


~root.children[1].children[1].name;
~root.children[1].children[2].name;


({4.do {"".postln;}}.value);
~root.traverse; // Yay! This works!

~root.entryAt(1,2,3); // works with testing code (doesn't do anythin useful)
~branch1.entryAt(1,2,3); // works



~root.isEmpty;
~branch1.isEmpty;
~branch1.isLeaf;
~branch1.list;

~b1sub1.list;
~b1sub1.isEmpty;
~b1sub1.isLeaf;