<#t>Put file ${ (apiRequest.fileFrom = 'Text')?then('(from text)', '') }
 to remote path "${ (apiRequest.remoteFilePath??)?then(apiRequest.remoteFilePath, 'null') }"
 on FTP server "${ endpoint.constructedUrl }" with username "${ (endpoint.username??)?then(endpoint.username, 'null') }".