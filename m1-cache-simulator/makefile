# Makefile
# A very simple makefile for compiling a Java program.  This file compiles
# the sim_cache.java file, and relies on javac to compile all its 
# dependencies automatically.
#
# If you require any special options to be passed to javac, modify the
# CFLAGS variable.  You may want to comment out the DEBUG option before
# running your simulations.

JAVAC = javac
CLASSES = \
	cache\simulator\Cache.java \
	cache\simulator\CacheConfig.java \
 	cache\simulator\CacheSimulator.java \
	cache\simulator\CacheUtilities.java \
	cache\simulator\LRU.java \
	cache\simulator\Operation.java \
	cache\simulator\OptimalPolicy.java \
	cache\simulator\PseudoLRU.java \
	cache\simulator\ReplacementPolicy.java \
	cache\simulator\Set.java \
	cache\simulator\Simulator.java

sim_cache:
	$(JAVAC) $(CLASSES)
	jar cfe sim_cache.jar cache.simulator.Simulator cache/simulator/*.class
	@echo ""
	@echo "README: Run program with:  java -jar sim_cache.jar INPUTS"
	@echo "Example:"
	@echo "java -jar sim_cache.jar 16 1024 2 0 0 0 0 gcc_trace.txt"
	
# type "make clean" to remove all your .class files
clean:
	-rm cache/simulator/*.class
	-rm sim_cache.jar