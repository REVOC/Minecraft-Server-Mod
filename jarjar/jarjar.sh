##
## jarjar.jar appears to literally be comparable to SpecialSource by md-5.
##
echo "Deobfuscating minecraft_server.jar with rules defined in rules.rules. Output file will be minecraft_servero.jar"
java -jar jarjar.jar process rules.rules minecraft_server.jar minecraft_servero.jar
