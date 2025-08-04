//MyTreeNode_test.scd 20250618
// this uses my new MyTreeNode which seems to be working for making a tree that contains at each node a number (so we can access by number) and a name. I


(
// Build the tree step by step
~root = MyTreeNode.new(0, "root");

// Level 1
~child1 = MyTreeNode.new(1, "branch A");
~child2 = MyTreeNode.new(2, "branch B");
~root.addChild(~child1);
~root.addChild(~child2);

// Level 2
~child11 = MyTreeNode.new(1, "A.1");
~child12 = MyTreeNode.new(2, "A.2");
~child21 = MyTreeNode.new(1, "B.1");
~child1.addChild(~child11);
~child1.addChild(~child12);
~child2.addChild(~child21);

// Level 3
~child111 = MyTreeNode.new(1, "A.1.a");
~child112 = MyTreeNode.new(2, "A.1.b");
~child11.addChild(~child111);
~child11.addChild(~child112);

// Level 4
~child1111 = MyTreeNode.new(1, "A.1.a.i");
~child111.addChild(~child1111);
)


~root.printTree;

~root.find([1, 1, 1, 1]);  // Should return: [1] A.1.a.i

~myarray = [1,2,3];
~root.find(~myarray); // TO IMPROVE: right now it outputs the symbol if we are at a leaf, but otherwise it outputs the array, which is not what we want.





/////////////////////////////////
// MOMENT OF GLORY.
// ALL HAIL THE N-dim ARRAY!
//
// ...maybe. But very promising!
//////////////////////////////////

// Let's see if we can make all this work with a MultiLevelIdentityDictionary instead... Can we?
z = MultiLevelIdentityDictionary.new; // it's empty
z.value;
z.put(\a, \b, \c, 1); // we can put keys, which are unique symbols.


// What about using an n-dimensional array?

a = Array.iota(24,24,24);
a[3][3][3] = \bob;
a[3][3][3].value;

a[2][2] = \category_2_2; // ah but that fucks things up. That replaces a branch with a leaf.
a[2][2].value;

//a[~myarray].value; // can't do that... how do I turn my array in three single value arrays?


~myarray = [3,3,3];

// with help from chatGPT (the devil):

~myarray.inject(a, { |acc, i| acc[i] });  //  I really don't understand much but this works.

a[12][10][8] = \tremolo;
a[12][10][8].value;
~myarray = [3,3,3];
~myarray.inject(a, { |acc, i| acc[i] });  //  I really don't understand much but this works.

~myarray = [12,10,8];
~myarray.inject(a, { |acc, i| acc[i] });  //  I really don't understand much but this works.









