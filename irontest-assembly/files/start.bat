cd %~dp0
java -Djava.net.useSystemProxies=true -jar ${uberJarFileName}.jar server config.yml