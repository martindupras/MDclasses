

// sanity check
y = Array.with(0,1,2,3,5,7);
y.value;
y.at(2);

(
~findy = {
	|index|
	y.at(index);
})

~findy.(5);


a = Array.iota(3,4,5,6);
a[1][3][2][3].value;




/*(
~findAt = {
	| i1,i2,i3,i4	|
	a[i1][i2][i3][i4]
}
)*/


// This works as long as exact number of arguments
(
~findAt = {
	| ...indices	|
	a[indices[0]][indices[1]][indices[2]][indices[3]];
}
)

~findAt.(1,3,2,3).value; // these all work! Except if there are arguments missing.
~findAt.(1,3,2,2).value;





// testing ideas
(~function = {
	|... args|
	args[0].postln;
	args[1].postln;
	args[2].postln;

})

~function.(1,2,3, 4,5);



//////
y.value; // outputs as array
y.something? // is there a way to output a collection?

