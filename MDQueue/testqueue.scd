
k = MDQueue("sara");
k.name;
k.thequeue;
k.enqueue("item1");
k.enqueue("item2");
k.enqueue("item3");
k.enqueue("item4");
k.thequeue;


k.output;   // doesn't work the first time. But works after executing line below!
k.output2.postln;  // this works.



/////////////
k.dequeue;
k.thequeue;
k.size;
k.get(0)

k.clear;
k.size;
k.get(0)


k.enqueue("metro");
k.enqueue("500");
k.output.postln;
k.value;
k.thequeue;

