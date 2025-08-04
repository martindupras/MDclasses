// sortedListTests.scd
// uses temporary class Ttuple.sc

~mysortedlist = SortedList.new(8 ,{|i,j| i.index <= j.index}); // the function to use to compare upon insertion

m = Ttuple.new(4,"banana");
n = Ttuple.new(2,"apple");
o = Ttuple.new(13,"carrot");

~mysortedlist.add(m);
~mysortedlist.add(n);
~mysortedlist.add(o);

~mysortedlist[0].name;
~mysortedlist[1].name;
~mysortedlist[2].name;

/*
 How to get list output? I'm guessing I need to make a "list" method and make it do what I want to see
*/
~mysortedlist.postln; //SortedList[a Ttuple, a Ttuple, a Ttuple, a Ttuple]


