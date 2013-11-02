package org.fogbeam.neddick

class Channel implements Comparable {

	String uuid;
	String name;
	String description;
	Date dateCreated;
	boolean privateChannel = false;
	User owner;
	
	public Channel()
	{
		this.uuid = java.util.UUID.randomUUID().toString();
	}
	
	
	static hasMany = [ dataSources : DataSource, /* feeds : RssFeed,*/ aggregateChannels:Channel, userFavoriteChannels:UserFavoriteChannelLink];

	// static mappedBy = [userFavoriteChannels: "channel"];
	
	static constraints =
	{
		description( nullable:true, maxSize:2048 );
	}
	
   	@Override
	public int compareTo(Object o) 
   	{
   		Channel otherChannel = (Channel)o;
   		return ( this.name.compareToIgnoreCase( otherChannel.name ) );
	}                   

}
