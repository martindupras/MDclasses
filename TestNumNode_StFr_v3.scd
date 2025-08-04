// TestNumNode_StFr_V2.scd

// v3 20250716 IN PROGRESS now using SortedList.
// v2 20250714 Works now, using Dictionary instead of List
// MD 20250711

// TODO: add findAt
~root = nil; // clean up
~root.value; // sanity check for when I restart

~root = NumNodeSF("myroot");

~root.name.value;
~root.string.value;
~root.fret.value;
~root.children.value;

// TODO: reorganise these less confusingly!
~root.addChild("s6f1", 1); // works
~root.hasChildAtFret(0); // true
~root.hasChildAtFret(1); // false
//~root.childAtRank(0);  // s6f1
~root.childAtFret(1) // Fret number  1
~root.childAtFret(1).name; //-> s6f1

~root.addChild("s6f2", 2);
~root.addChild("s6f3", 3);
~root.addChild("7-ugens", 7);
~root.addChild("9-abstractions", 9);

~root.childAtFret(7)       //-> a NumNodeSF
~root.childAtFret(7).name; //-> ugens

~root.childAtFret(9)       //-> a NumNodeSF
~root.childAtFret(9).name; //-> abstractions


~root.childAtFret(7).addChild("5-osc~", 5);
~root.childAtFret(7).addChild("11-pink~", 11);

~root.childAtFret(7).childAtFret(11).name;
~root.childAtFret(7).childAtFret(11).isLeaf;
~root.childAtFret(7).isLeaf;

~root.traverse; // This seems to work and in order.

~root.list;
~root.childAtFret(7).list;

~root.findAtKeys(7,5); //-> a NumNodeSF
~root.findAtKeys(7,5).name; //-> 5-osc~
// create a collision
~root.addChild("collisiontest", 1); // working





//// stuff below probably obsolete now

~root.children[1].addChild("s5f1", 1);
~root.children[1].addChild("s5f2", 2);
~root.children[1].addChild("s5f3", 3);

~root.children[7].addChild("osc~", 5);
~root.children[7].addChild("pink~", 11);

~root.children[9].addChild("chopper", 1);
~root.children[9].addChild("fooger", 2);

~root.children();
~root.children[7].children();

~root.traverse; // not quite in order... Not sure how to do in order since this uses lists; check List methods


(
~root.list;              // what is on string 6
//~root.children[0].list;  //
~root.children[1].list;  // what is at string 6 fret 7... incorrect
~root.children[2].list;  // what is at string 6 fret 7... incorrect
~root.children[3].list;  // what is at string 6 fret 7... incorrect
~root.children[7].list;  // what is at string 6 fret 7... correct
~root.children[9].list;  // what is at string 6 fret 9... correct

~root.traverse; // not quite in order... Not sure how to do in order since this uses lists; check List methods

~root.children[1].children[2].name; // shows what is at (string6-fret1, string5-fret
)

~root.findAt(1,2);   //s6f2 -- incorrect!
~root.findAt(7);     // a NumNodeSF (correct but we want name)
~root.findAt(7,11);  // ERROR
~root.findAt(1,2,3); //s6f3 -- probably incorrect!

~root.hasChildAtRank(0); //false
~root.hasChildAtRank(1); //true
~root.childAtRank(1); //returns s6f1 -- correct
~root.hasChildAtRank(2); //true
~root.childAtRank(2); //returns s6f2 -- correct
~root.hasChildAtRank(3); //true
~root.hasChildAtRank(4);// false
~root.hasChildAtRank(5); //false
~root.hasChildAtRank(6); //false
~root.hasChildAtRank(7); //true
~root.childAtRank(7);    //ugens -- correct
~root.hasChildAtRank(8);// false
~root.hasChildAtRank(9); //true

