// SwapTest.scd
// now all working

(~testroot = nil;
~testroot = NumNodeSF.new("root");

~testroot.addChild("b3",3);
~testroot.addChild("b5",5);
~testroot.addChild("b7",7);

~testroot.childAtFret(3).addChild("b3c2", 2); // "A" to swap
~testroot.childAtFret(3).addChild("b3c4", 4);

~testroot.childAtFret(7).addChild("b7c12", 12);
~testroot.childAtFret(7).addChild("b7c14", 14); //"B" to swap
)

// ~testroot = NumNodeSF.new("root");
//~testroot.swap([3,2],[7,14]); // THIS IS PROMISING.


// test replace:
~testroot.childAtFret(3).list;
~testroot.childAtFret(3).replaceChild("newer",2);
~testroot.childAtFret(3).list;
~testroot.childAtFret(3).childAtFret(2).name;  // want NEWER
~testroot.childAtFret(3).list;




// before swap:
~testroot.childAtFret(3).childAtFret(2).name;  // b3c2
~testroot.childAtFret(7).childAtFret(14).name; // b7c14

~testroot.swap([3,2],[7,14]);

// after swap:
~testroot.childAtFret(3).childAtFret(2).name;  // want b7c14
~testroot.childAtFret(7).childAtFret(14).name; // want b3c2





