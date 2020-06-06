cd %~dp0
FOR /F %%i IN ('dir /b /on irontest-*.jar ^| findStr /v "\-SNAPSHOT\.jar$"') DO SET newestJarFile=%%i
java -Djava.net.useSystemProxies=true -Ddw.mode=team -jar %newestJarFile% server config.yml