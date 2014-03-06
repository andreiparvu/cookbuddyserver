build:
	javac Server.java

run: build
	java Server

clean:
	rm -f *.class
