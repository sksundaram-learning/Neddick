package org.fogbeam.neddick

import com.sun.syndication.feed.synd.SyndEntry
import com.sun.syndication.feed.synd.SyndFeed
import com.sun.syndication.io.SyndFeedInput
import com.sun.syndication.io.XmlReader


class ChannelService {

	def entryService;
	
	public Channel findByName( final String channelName )
	{
		Channel channel = Channel.findByName( channelName );
		return channel;
	}

	public Channel findById( final Long id )
	{
		Channel channel = Channel.findById( id );
		return channel;
	}
	
	public List<Channel> getAllChannels()
	{
		List<Channel> allChannels = new ArrayList<Channel>();
		allChannels.addAll( Channel.findAll() );
		
		allChannels.sort();
		
		return allChannels;
	}

	public List<Channel> getEligibleAggregateChannels( final User user )
	{
		List<Channel> eligibleChannels = new ArrayList<Channel>();
	
		List<Channel> queryResults = 
			Channel.
				executeQuery( 	"select channel from Channel as channel where " + 
								"channel.name <> :defaultName and " + 
								"(channel.privateChannel = false OR " + 
								"(channel.privateChannel = true and channel.owner = :owner ))", [defaultName:"default", owner:user] );
				
		if( queryResults != null )
		{
			eligibleChannels.addAll( queryResults );
		}
		
		
		return eligibleChannels;
	}

	
	public List<Channel> getEligibleAggregateChannels( final User user, final Channel theChannel )
	{
		List<Channel> eligibleChannels = new ArrayList<Channel>();
	
		/* if aggregateChannels is empty in the query below, the HQL blows up due to
		 * the empty in () expression.  So we have to do it slightly differently in this case
		 */
		List<Channel> queryResults = null;
		
		Set<Channel> selectedChannels = theChannel.aggregateChannels;
		if( selectedChannels == null || selectedChannels.isEmpty())
		{

			queryResults = Channel.
			executeQuery( 	"select channel from Channel as channel where " +
							"channel.name <> :defaultName and " +
							"channel <> :theChannel and " +
							// "channel not in :selectedChannels and " +
							"(channel.privateChannel = false OR " +
							"(channel.privateChannel = true and channel.owner = :owner ))",
								[defaultName:"default", owner:user, theChannel:theChannel] );
							

		}
		else
		{
		
			queryResults = Channel.
				executeQuery( 	"select channel from Channel as channel where " +
								"channel.name <> :defaultName and " +
								"channel <> :theChannel and " +
								"channel not in :selectedChannels and " + 
								"(channel.privateChannel = false OR " +
								"(channel.privateChannel = true and channel.owner = :owner ))", 
									[defaultName:"default", owner:user, theChannel:theChannel, selectedChannels:selectedChannels] );
								
		}
										
		if( queryResults != null )
		{
			eligibleChannels.addAll( queryResults );
		}
		
		
		return eligibleChannels;
	}
	
		
	public void updateFromDatasource( Channel channel )
	{
	
		println( "Updating from DataSource for channel: ${channel.name}" );	
		User anonymous = User.findByUserId( "anonymous" );
		
		// if the specified channel has an RssFeed associated with it...
		Set<RssFeed> feeds = channel.feeds;
		
		if( feeds != null && feeds.size() > 0 )
		{
			println( "There are feeds!" );
			
			for( RssFeed rssFeed in feeds )
			{
				// lookup the feed, and get the FeedUrl
				String url = rssFeed.feedUrl;
				println( "Loading from url: ${url}, description: ${rssFeed.description}" );
				
				// load the feed, and create an Entry for each link in the RssFeed
				/*
				URL dummyUrl = new URL(url);
				InputStream dummyInStream = dummyUrl.getContent();
				BufferedReader dummyReader = new BufferedReader(new InputStreamReader(dummyInStream));
				String dummyResult;
				String dummyLine = dummyReader.readLine();
				dummyResult = dummyLine;
				while((dummyLine=dummyReader.readLine())!=null){
					dummyResult+=dummyLine;
				}
				
				println ("url content: " + dummyResult );
				*/
				
				URL feedUrl = new URL(url);
				SyndFeedInput input = new SyndFeedInput();
				SyndFeed feed = null;
				XmlReader reader = null;
				
				
				try
				{
					reader = new XmlReader(feedUrl)
					feed = input.build(reader);
					log.debug( "Feed: ${feed.getTitle()}" );
					
					List<SyndEntry> entries = feed.getEntries();
					
					log.debug( "processing ${entries.size()} entries!" );
					int good = 0;
					int bad = 0;
					
					for( SyndEntry entry in entries )
					{
						String linkUrl = entry.getLink();
						String linkTitle = entry.getTitle();
						
						List<Entry> testForExisting = entryService.findByUrlAndChannel( linkUrl, channel );
						if( testForExisting != null && testForExisting.size() > 0 )
						{
							log.debug( "An Entry for this link already exists. Skipping" );							
							continue;
						}
						else
						{	
							
							// does this link exist elsewhere in the system (eg, linked to another channel)?
							List<Entry> e2 = entryService.findByUrl( linkUrl );
							if( e2 != null && e2.size() > 0 )
							{
								// we already have this Entry, so instead of creating a new Entry object, we just
								// need to link this one to this Channel.
								Entry existingEntry = e2.get(0);
								existingEntry.addToChannels( channel );
								existingEntry.save();
							}
							else
							{
							
								log.debug( "creating and adding entry for link: ${linkUrl} with title: ${linkTitle}" );
					
								Entry newEntry = new Entry( url: linkUrl, title: linkTitle, submitter: anonymous );
								entryService.saveEntry( newEntry );
								newEntry.addToChannels( channel );
								
								if( newEntry )
								{
									good++;
									log.debug( "saved new Entry with id: ${newEntry.id}" );
									// send JMS message saying "new entry submitted"
									def newEntryMessage = [msgType:"NEW_ENTRY", id:newEntry.id, uuid:newEntry.uuid, url:newEntry.url, title:newEntry.title ];
					
									log.debug( "sending new entry message to JMS entryQueue");
									// send a JMS message to our entryQueue
									sendJMSMessage("entryQueue", newEntryMessage );
							
									log.debug( "sending new entry message to JMS searchQueue" );
									// send a JMS message to our searchQueue
									sendJMSMessage("searchQueue", newEntryMessage );
								
								}
								else
								{
									bad++;
									// failed to save newEntry
									log.debug( "Failed to save newEntry!" );
								}
							}
						}
					}
					
					log.debug( "Good entries: ${good}, bad entries:${bad}" );
					
				}
				catch( Exception e )
				{
					println "Caught Exception in Feed Processing Loop!";
					
					e.printStackTrace();
					
					println "Continuing to next Feed";
					continue;
				}
				finally 
				{
					if( reader != null ) 
					{
						reader.close();	
					}	
				}
			}
		}
	}

	public List<Channel> findChannelsWithDatasource()
	{
		List<Channel> channels = new ArrayList<Channel>();
		
		List subList = Channel.executeQuery( "select channel from Channel as channel where channel.feeds is not empty" );
		
		channels.addAll( subList );
		
		return channels;	
	}
		
}