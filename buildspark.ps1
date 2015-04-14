cd "C:\Users\Lukas\Downloads\spark-1.3.0"
set MAVEN_OPTS "-Xmx1024m -XX:MaxPermSize=512m"
mvn -DskipTests clean package -e