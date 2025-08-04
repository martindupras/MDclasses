// tests for bob.sc

b = Bob.new;
b.name;
b.name_("Martin");
b.age_(55);
b.age;

c = Rita.new(40,"Reetta");
c.age;
c.age_(41); // not possible!


r = Rita.new(40, "Reetta");

r.age.postln;     // → 40 (works)
r.name = "Rita!"; // works
r.name.postln;    // → "Rita!"

