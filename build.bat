rem Lo script compila e fa il deply del progetto
cd kafka_as400

rem  Compilazione del progetto spring boot 
call mvn clean validate compile install package 

echo on

rem Far partire il programma utilizzare il comando:
rem java -jar kafka_as400\target\kafkaas400-0.0.1-SNAPSHOT.jar

rem Per AS400 eseguire i comandi
rem CPY OBJ('/qntc/10.120.32.179/fccrt/PFC/PFC1/JO/test/kafkaas400.jar') 
rem     TOOBJ('/tmp/kafkaas400.jar')   REPLACE(*YES)
rem     qsh + java -jar /tmp/kafkaas400.jar
rem oppure 
rem     JAVA CLASS(kafkaas400.jar) CLASSPATH('/tmp')                                  