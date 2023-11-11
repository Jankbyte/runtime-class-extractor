## Runtime class extractor
That's a simple tool for extracting classes from runtime.
For example: we can extract spring-proxies for decompiling.

### Setting for running
For working with library you should compile that into jar
```
mvn clean package
```
Next step, we should prepare properties file with extracting details (D:/extract.properties):
```
# save classes directory
saveDirectory=D:/classes

# regex pattern for extracting classes (patters splitting by "&&")
extractClassPatterns=^(java.lang.String\$CGLIB\$0|java.lang.Integer)$ && ^(java.lang.Object)$
```
When we start project - we should use this library as javaagent (in this example we using multiple javaagents):
```
java -javaagent:"D:/aspectjweaver-1.9.19.jar" -javaagent:"D:/runtime-class-extractor.jar=D:/extract.properties" -jar spring-app.jar
```
