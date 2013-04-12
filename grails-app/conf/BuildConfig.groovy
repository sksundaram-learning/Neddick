grails.project.dependency.resolution = {
    // inherit Grails' default dependencies
    inherits("global") {
        // uncomment to disable ehcache
        excludes 'slf4j-api', 'slf4j-log4j12', 'jcl-over-slf4j', 'jul-to-slf4j'
    }
    log "warn" // log level of Ivy resolver, either 'error', …
    repositories {
        grailsPlugins()
        grailsHome()
        grailsCentral()
	// uncomment the below to enable remote dependency resolution 
	// from public Maven repositories 
	//mavenLocal() 
	//mavenCentral() 
	//mavenRepo "http://snapshots.repository.codehaus.org" 
	//mavenRepo "http://repository.codehaus.org" 
	//mavenRepo "http://download.java.net/maven/2/" 
	//mavenRepo "http://repository.jboss.com/maven2/"
 } 
 dependencies { 
	     // specify dependencies here under either 'build', 'compile', ...

	     // runtime 'mysql:mysql-connector-java:5.1.5' 
	     } 
}