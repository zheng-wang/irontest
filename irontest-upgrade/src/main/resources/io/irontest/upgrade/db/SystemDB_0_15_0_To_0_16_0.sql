-- alter Endpoint table
ALTER TABLE ENDPOINT ADD host varchar(200);
ALTER TABLE ENDPOINT ADD port int;

update endpoint set other_properties = '{"@type":"SOAPEndpointProperties",' || substring(other_properties from 2) where type = 'SOAP' and other_properties not like '{"@type":%';
update endpoint set other_properties = '{"@type":"FTPEndpointProperties",' || substring(other_properties from 2) where type = 'FTP' and other_properties not like '{"@type":%';
update endpoint set other_properties = '{"@type":"MQEndpointProperties",' || substring(other_properties from 2) where type = 'MQ' and other_properties not like '{"@type":%';
update endpoint set other_properties = '{"@type":"IIBEndpointProperties",' || substring(other_properties from 2) where type = 'IIB' and other_properties not like '{"@type":%';

-- update IIB endpoints
update endpoint set host = case when other_properties like '%"host":%' then substring(other_properties, locate('"host":', other_properties) + 8, locate('","', other_properties, locate('"host":', other_properties) + 8) - (locate('"host":', other_properties) + 8)) else host end where type = 'IIB';
update endpoint set port = case when other_properties like '%"port":%' then cast(substring(other_properties, locate('"port":', other_properties) + 7, locate(',"', other_properties, locate('"port":', other_properties) + 7) - (locate('"port":', other_properties) + 7)) as int) else port end where type = 'IIB';
update endpoint set other_properties = case when other_properties like '%"useSSL":true%' then '{"@type":"IIBEndpointProperties","useSSL":true}' when other_properties like '%"useSSL":false%' then '{"@type":"IIBEndpointProperties","useSSL":false}' else '{"@type":"IIBEndpointProperties"}' end where type = 'IIB';

-- update MQ endpoints
update endpoint set other_properties = replace(other_properties, '"host":null,"port":null,') where type = 'MQ' and other_properties like '%"connectionMode":"Bindings"%';
update endpoint set host = substring(other_properties, locate('"host":', other_properties) + 8, locate('","', other_properties, locate('"host":', other_properties) + 8) - (locate('"host":', other_properties) + 8)) where type = 'MQ' and other_properties like '%"connectionMode":"Client"%';
update endpoint set port = case when other_properties like '%"port":null%' then null else cast(substring(other_properties, locate('"port":', other_properties) + 7, locate(',"', other_properties, locate('"port":', other_properties) + 7) - (locate('"port":', other_properties) + 7)) as int) end where type = 'MQ' and other_properties like '%"connectionMode":"Client"%';
update endpoint set other_properties = replace(other_properties, substring(other_properties, locate('"host":', other_properties), locate('"svrConnChannelName":', other_properties) - locate('"host":', other_properties))) where type = 'MQ' and other_properties like '%"connectionMode":"Client"%';
update endpoint set other_properties = replace(other_properties, substring(other_properties, locate(',"queueManagerAddress":', other_properties)), '}') where type = 'MQ';

-- update FTP endpoints
update endpoint set host = substring(other_properties, locate('"host":', other_properties) + 8, locate('","', other_properties, locate('"host":', other_properties) + 8) - (locate('"host":', other_properties) + 8)) where type = 'FTP';
update endpoint set port = cast(substring(other_properties, locate('"port":', other_properties) + 7, locate(',"', other_properties, locate('"port":', other_properties) + 7) - (locate('"port":', other_properties) + 7)) as int) where type = 'FTP';
update endpoint set other_properties = case when other_properties like '%"useSSL":true%' then '{"@type":"FTPEndpointProperties","useSSL":true}' when other_properties like '%"useSSL":false%' then '{"@type":"FTPEndpointProperties","useSSL":false}' else '{"@type":"FTPEndpointProperties"}' end where type = 'FTP';