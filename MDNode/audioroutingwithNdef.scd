// audioroutingwithNdef.scd
// MD 20250709 still trying to figure it all out!



// a processor
(
Ndef(\reverb, {
	FreeVerb.ar([\in1.ar, \in2.ar], 0.6, 0.8);
}).play;
)

// a one-channel source
Ndef(\a, { LFPulse.ar(MouseY.kr(30, 1000, 1))  *0.3}).stop;

Ndef(\reverb) <<>.in1 Ndef(\a); // just send to left channel
Ndef(\reverb) <<>.in2 Ndef(\a); // just send to right channel
Ndef(\reverb) <<>.in1 nil;
Ndef(\reverb) <<>.in2 nil;



(
Ndef(\reverb, {
	FreeVerb.ar([\in1.ar, \in2.ar], 0.6, 0.8);
}).play;
)


(
  SynthDef(\tapedelay, { |out = 0, in|
    var maxDelay = 10; // 10 seconds
    var signal = (LocalIn.ar(1) * \feedback.kr(0.5)) + in; // will this work with Ndefs?
    var tapesig = AnalogTape.ar(signal);
    var delayed = DelayC.ar(tapesig, maxDelay, \delay.kr(0.4));


    LocalOut.ar(delayed);
    Out.ar(out,
      Pan2.ar(
        XFade2.ar(in, delayed, (\drywet.kr(0.2) * 2) - 1)
      )
    );
  }, [0, \ar]).add;
)



(
(
Ndef(\testtape)[0]= \tapedelay;
).play;
)

Ndef(\testtape) <<>.in1 Ndef(\a);