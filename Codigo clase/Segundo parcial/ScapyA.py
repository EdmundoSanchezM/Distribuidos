from scapy.all import *
# Instantiate the blocks
clf = CLIFeeder()
ijs = InjectSink("enx3495db043a28")

# Plug blocks together
clf > ijs

# Create and start the engine
pe = PipeEngine(clf)
pe.start()