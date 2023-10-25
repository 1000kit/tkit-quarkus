# Log extensions
This group of the extension contains several interceptors implementation and utilities for extending logging in the application.

* [cdi](cdi) - Quarkus extension for CDI method logging(business method start/stop logging).
* [rs](rs) - Quarkus extension for HTTP request logging (client & server).
* [json](json) - Custom JSON log formatter that provides additional features


Migration configration from old tkit library

| old quarkus.tkit config                 | new tkit config |
|-----------------------------------------|-----------------|
| quarkus.tkit.log.console.json           | tkit.log.json.enabled                |
| quarkus.tkit.log.console.json.keys.type | tkit.log.json.keys.type               |
| quarkus.tkit.log.console.json.keys.mdc  | tkit.log.json.keys.mdc                |
|quarkus.tkit.log.console.json.keys.ignore|	 tkit.log.json.keys.ignore|
|quarkus.tkit.log.console.json.keys.override|	 tkit.log.json.keys.override|
|quarkus.tkit.log.console.json.keys.env	|	 tkit.log.json.keys.env|
|quarkus.tkit.log.console.json.keys.group|	 tkit.log.json.keys.group|
|quarkus.tkit.log.customdata.prefix		|	 tkit.log.cdi.custom-data.prefix|
|quarkus.tkit.log.ignore.pattern	|	 		 tkit.log.cdi.auto-discovery.ignore.pattern|
|quarkus.tkit.log.mdc.errorKey			|	 tkit.log.cdi.mdc.errorKey|
```