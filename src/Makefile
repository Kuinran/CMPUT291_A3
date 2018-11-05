GS = -g
JC = javac

.SUFFIXES: .java .class
.java.class:
	$(JC) $(JFLAGS) $*.java
        
CLASSES = \
        Main_States.java \
        JDBC_Connection.java \
        Menu_Book.java \
        Menu_Main.java \
        Menu_ManageRq.java \
        Menu_PostRq.java \
        Menu_RideOffer.java \
        Menu_Search.java \
        Menu_Login.java \
        Main.java

default: classes

classes: $(CLASSES:.java=.class)

clean:
	$(RM) *.class submit.tar

tar: 
	tar -cvf submit.tar sqlite-jdbc-3.23.1.jar Makefile Report.pdf README.txt Main_States.java JDBC_Connection.java Menu_Book.java Menu_Main.java Menu_ManageRq.java Menu_PostRq.java  Menu_RideOffer.java Menu_Search.java Menu_Login.java Main.java