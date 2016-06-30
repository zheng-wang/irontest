# Iron Test
Iron Test is an open source tool for functionally testing APIs such as SOAP web services, databases (currently only H2), MQ, IIB, RESTful web services (TBD), JMS (TBD), etc. with automation. It helps with SOA, Microservices (TBD) and ESB testing.

It supports neither performance testing (for now) nor GUI testing.

Note: The tool is not yet fully production ready, but you can use it for informal test automation of SOAP web services, MQ and IIB.

## Characteristics
- Web UI, so zero installation for end users.
- Centralized test cases and environments management, enabling team work by default.
- No middleware.
- Platform independent. Can run on Windows, Linux/Unix and Mac.
        
## Primary Dependencies
Dropwizard, H2 Database, AngularJS, Bootstrap.

## Build
Prerequisites: JDK 1.7+, Maven 3.x, NPM, Bower.

Download or clone code to your local machine. cd to the root directory of the project, and run below Maven command

`mvn clean package --projects irontest-core --also-make`

This builds Iron Test without MQ/IIB testing features, and the seed files for deployment can be found in the irontest/irontest-core/dist folder.

To build Iron Test with MQ/IIB testing features, follow below instructions instead

- Install MQ and IIB libraries to your local Maven repository
```
    mvn install:install-file -Dfile="<MQ_Home>/java/lib/com.ibm.mq.jar" -DgroupId=com.ibm -DartifactId=com.ibm.mq -Dversion=<MQ_Version> -Dpackaging=jar
    mvn install:install-file -Dfile="<MQ_Home>/java/lib/com.ibm.mq.jmqi.jar" -DgroupId=com.ibm -DartifactId=com.ibm.mq.jmqi -Dversion=<MQ_Version> -Dpackaging=jar
    mvn install:install-file -Dfile="<MQ_Home>/java/lib/com.ibm.mq.commonservices.jar" -DgroupId=com.ibm -DartifactId=com.ibm.mq.commonservices -Dversion=<MQ_Version> -Dpackaging=jar
    mvn install:install-file -Dfile="<MQ_Home>/java/lib/com.ibm.mq.pcf.jar" -DgroupId=com.ibm -DartifactId=com.ibm.mq.pcf -Dversion=<MQ_Version> -Dpackaging=jar
    mvn install:install-file -Dfile="<MQ_Home>/java/lib/com.ibm.mq.headers.jar" -DgroupId=com.ibm -DartifactId=com.ibm.mq.headers -Dversion=<MQ_Version> -Dpackaging=jar
    mvn install:install-file -Dfile="<MQ_Home>/java/lib/connector.jar" -DgroupId=javax.resource -DartifactId=connector -Dversion=1.3.0 -Dpackaging=jar
	mvn install:install-file -Dfile="<IIB_Home>/classes/ConfigManagerProxy.jar" -DgroupId=com.ibm -DartifactId=ConfigManagerProxy -Dversion=<IIB_Version> -Dpackaging=jar
    mvn install:install-file -Dfile="<IIB_Home>/jre17/lib/ibmjsseprovider2.jar" -DgroupId=com.ibm -DartifactId=ibmjsseprovider2 -Dversion=<IIB_Version> -Dpackaging=jar
```

- Check MQ/IIB versions in irontest/irontest-mqiib/pom.xml. If your MQ or IIB version falls outside the range, modify the POM. I haven't tested that version, but Iron Test might work with it. Refer to [this doc](http://maven.apache.org/enforcer/enforcer-rules/versionRanges.html) for more info about Maven version ranges.
```
    <mq.version>[7.5.0.3, 7.5.0.6]</mq.version>
    <iib.version>[9.0.0.3, 9.0.0.5]</iib.version>
```
 
- Run below Maven command

    `mvn clean package --projects irontest-mqiib --also-make`

    Seed files for deployment can be found in the irontest/irontest-mqiib/dist folder.
    
## Deploy
Prerequisites: JRE 1.7+.

Copy seed files to any folder on any computer/VM that has access to the APIs you want to test. That folder will be referred to as `<IronTest_Home>` hereafter.

To launch Iron Test application, cd to `<IronTest_Home>` and run below command

`java -jar <jarFilename> server config.yml`

To verify the application is successfully launched, open a web browser (Chrome is preferred), and go to Iron Test home page http://localhost:8081/ui (no ending '/').

If this is the first time you launch the application in the `<IronTest_Home>` folder, you will see two sub-folders created

    database - where Iron Test database is located. The database is used to store all test cases, environments, endpoints, etc. you create using Iron Test.
    
    logs - where Iron Test application runtime logs are located.
    
## Maintain
It is highly recommended that you back up `<IronTest_Home>/database` folder regularly. Remember to shut down the application before backing up.

To shut down the application
    
    On Windows: Ctrl + C
    
    On Linux/Unix: kill -SIGINT <pid>
    
To move Iron Test to a different folder or computer/VM, just shut down the application, copy the whole `<IronTest_Home>` folder over, and launch the application from there.

## Using
