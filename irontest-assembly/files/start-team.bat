cd %~dp0
java -Djava.net.useSystemProxies=true -Ddw.mode=team -jar ${uberJarFileName}.jar server config.yml