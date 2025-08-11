a = MDMIDISource.new();
l = MDLaunchpadSource.new();
g = MDMIDIGuitarSource.new();
f = MDfootControllerSource.new();

a.handleMessage(3,\noteOn, 60);
l.handleMessage(3,\noteOn, 60);
g.handleMessage(3,\noteOn, 60);
f.handleMessage(3,\noteOn, 60);
