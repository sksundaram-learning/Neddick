package org.fogbeam.neddick



class TagController {

	def tagService;
	def entryService;
	def userService;
	def siteConfigService;
	
	def scaffold = true;

	int defaultItemsPerPage = 25;	
	
	def list = {
		
		String strItemsPerPage = siteConfigService.getSiteConfigEntry( "itemsPerPage" );
		
    	int itemsPerPage = defaultItemsPerPage;
		if( strItemsPerPage != null )
    	{
    		itemsPerPage = Integer.parseInt( strItemsPerPage );
    	}

		String requestedPageNumber = params.pageNumber;
    	int pageNumber = 1;
    	int availablePages = -1;
    	if( requestedPageNumber != null )
    	{
    		try
    		{
    			pageNumber = Integer.parseInt( requestedPageNumber );
    		}
    		catch( NumberFormatException nfe )
    		{
    			flash.message = "Invalid Pagenumber requested";
    			pageNumber = 1;
    		}
    	}
    	
    	log.debug( "requested pageNumber: ${pageNumber}" );	
			
			
		List<Tag> allTags = new ArrayList<Tag>();	
		allTags.addAll( tagService.getAllTags());
		
    	int dataSize = allTags.size();
    	log.debug( "dataSize: ${dataSize}" );
    	int pages = dataSize / itemsPerPage;
		log.debug( "dataSize / itemsPerPage = ${pages}" );
    	pages = Math.max( pages, 1 );
		
		log.debug( "pages: ${pages}" );
		
		if( dataSize > (pages*itemsPerPage) )
		{
			pages += 1;
		}
		
		availablePages = pages;
    	log.debug( "availablePages: ${availablePages}" );
    	
    	if( pageNumber < 1 )
    	{
    		flash.message = "Invalid Pagenumber ${requestedPageNumber} requested";
    		pageNumber = 1;
    	}
    	if( pageNumber > availablePages )
    	{
    		flash.message = "Invalid Pagenumber ${requestedPageNumber} requested";
    		pageNumber = availablePages;
    	}    	
    	
    	
    	// get the requested page of entry UUIDs
    	int beginIndex = ( pageNumber * itemsPerPage ) - itemsPerPage;
    	int endIndex = Math.min( dataSize -1, ((pageNumber * itemsPerPage ) - 1));
    	
		if( pageNumber == pages )
		{
			endIndex = Math.min( dataSize -1, endIndex);
		}        	
    	
    	List<Tag> subList = null;
		
		if( dataSize > 0 )
		{
			subList = allTags[ beginIndex .. endIndex ];		
		}
		else
		{
			subList = new ArrayList<Tag>();	
		}
		
		[allTags:subList, currentPageNumber: pageNumber, availablePages: availablePages ];
	}
	
	def addTag = {
			
		log.debug( "addTag called with tagName: ${params.tagName}, uuid: ${params.entryUuid}" );
		
		// check for logged in user
		if( null != session.user )
		{
			User user = userService.findUserByUserId( session.user.userId );
			
			String tagName = params.tagName;
			if( null != tagName )
			{
				tagName = tagName.trim().toLowerCase()
				
				Tag tag = tagService.findTagByName( tagName );
				if( tag == null )
				{
					// nobody's used this tag before, create it
					// TODO: make tagService.createTag call. Note the race condition inherent in this.
					tag = new Tag( name: tagName );
				
					// set creator
					if( !tag.save() )
					{
						log.error( "Creating tag: ${tagName} FAILED");
						// tag.errors.allErrors.each { p rintln it };
					}
					else
					{
						log.debug( "Created tag ${tagName} OK" );	
					}
				}
				else
				{
					log.debug( "Tag: ${tagName} already exists" );	
				}
			
				// lookup our entry by the uuid
				Entry entry = entryService.findById( params.entryId );
			
				// add the tag to the tags collection
				entry.addToTags( tag, user );
			
				// save
				entryService.saveEntry( entry );
			
				String newTagMessage = "ADDTAG|${entry.uuid}|${tagName}";
		    	// send a JMS message to our testQueue
				sendJMSMessage("searchQueue", newTagMessage );
				
				
				render("done");
			}
			else
			{
				// no tag to add...
			}
		}
		else
		{
			// can't add a tag if not logged in....
		}
	}

	def listEntriesByTag = {
			
		/* list all the Entry's that have been tagged with a given tag. */
		def taggedEntries = new ArrayList();
		String tagName = params.tagName;
		if( null != tagName )
		{
			tagName = tagName.trim().toLowerCase();
			
			Tag tag = tagService.findTagByName(tagName);
			if( null != tag )
			{
				taggedEntries = tag.entries;
			}
		}
		
		render(view:"listEntriesByTag", model:[allEntries:taggedEntries]); 
	
	}

	def myTags = {
		
		log.debug( "myTags called" );	
			
		def tagList = new ArrayList();
		if( session.user ) 
		{
			log.debug( "found user, getting taglist" );
			
			User user = userService.findUserByUserId( session.user.userId );
			// get a list of the distinct tags that I have used
			tagList = tagService.getTagListForUser( user );
		}
	
		log.debug( "found ${tagList.size()} tags for user ${session.user.userId}" );
		
		[tagList: tagList];
	}
}
