all: p1
	
p1: Main.java Helpers.java JDBC_Connection.java Menu_Book.java Menu_Main.java Menu_ManageRq.java Menu_PostRq.java Menu_RideOffer.java Menu_Search.java
	javac Main.java Helpers.java JDBC_Connection.java Menu_Book.java Menu_Main.java Menu_ManageRq.java Menu_PostRq.java Menu_RideOffer.java Menu_Search.java
	
clean :
        -rm $(objects)
      
run: Main.java Helpers.java JDBC_Connection.java Menu_Book.java Menu_Main.java Menu_ManageRq.java Menu_PostRq.java Menu_RideOffer.java Menu_Search.java
	p1
	java Main.class