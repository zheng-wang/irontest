# Iron Test
Iron Test is an open source tool for functionally testing APIs such as SOAP web services, databases (currently only H2), MQ, IIB, RESTful web services (TBD), JMS (TBD), etc. with automation. It helps with SOA, Microservices (TBD) and ESB testing.

It supports neither performance testing (for now) nor GUI testing.

Note: The tool is not yet fully production ready, but you can use it for informal test automation of SOAP web services, MQ and IIB.

## Features
- Web UI, so zero installation for end users.
- API test automation.
- Centralized test cases and environments management and sharing.
- No middleware.

## Primary Dependencies
- Dropwizard
- H2 Database
- AngularJS
- Bootstrap

## Build
Prerequisites: JDK 1.7+, Maven 3.x, NPM, Bower.
#### Build without MQ/IIB testing features
#### Build with MQ/IIB testing features
