// make a function that iterates through a loop the same number of times as the number of arguments passed to the function


// THAT SEEMS TO WORK.

~arr = Array.iota(3,4,5);
~arr[1][2]; // test
~arr[2][2]; // test



(~function = {
	|... args|
	var temp = ~arr;  // should copy the array
	args.do {
		|argIndex|
		temp = temp[argIndex];
	};
	temp
}
)


~function.(2,2);
~function.(1,2,3);


