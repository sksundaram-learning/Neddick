package com.fogbeam.neddick


import java.text.SimpleDateFormat 


class RebuildCacheJob {

	def jmsService;
	
	static triggers = {
		simple name: 'mySimpleTrigger', startDelay: 240000, repeatInterval: 240000
	  }
	
    def execute() 
	{
     	
		Date now = new Date();
		SimpleDateFormat sdf = SimpleDateFormat.getDateTimeInstance();
		
		println "TRIGGER: sending rebuild cache message: ${sdf.format( now )}";
		if( jmsService != null )
		{
			println "using JMS Service";
			jmsService.send( queue: "cacheQueue", "REBUILD_CACHE", "standard", null );
		}
		else
		{
			println "no JMS Service!";	
		}
			
	}
}