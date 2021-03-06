package org.fogbeam.neddick

import org.fogbeam.neddick.triggers.BaseTrigger
import org.fogbeam.neddick.triggers.ChannelTrigger
import org.fogbeam.neddick.triggers.GlobalTrigger
import org.fogbeam.neddick.triggers.actions.BaseTriggerAction
import org.fogbeam.neddick.triggers.actions.EmailTriggerAction
import org.fogbeam.neddick.triggers.actions.QuoddyShareTriggerAction
import org.fogbeam.neddick.triggers.actions.XmppTriggerAction
import org.fogbeam.neddick.triggers.criteria.AboveScoreTriggerCriteria
import org.fogbeam.neddick.triggers.criteria.BaseTriggerCriteria
import org.fogbeam.neddick.triggers.criteria.BodyKeywordTriggerCriteria
import org.fogbeam.neddick.triggers.criteria.NewContentTriggerCriteria
import org.fogbeam.neddick.triggers.criteria.TagTriggerCriteria
import org.fogbeam.neddick.triggers.criteria.TitleKeywordTriggerCriteria

class TriggerController
{
	def triggerService;
	def userService;
	def channelService;
	
	def index =
	{
		
		// lookup Triggers for the current user and put them in the domain
		// for rendering
		
		List<GlobalTrigger> globalTriggers = triggerService.getGlobalTriggersForUser( session.user );
		List<ChannelTrigger> channelTriggers = triggerService.getChannelTriggersForUser( session.user );
		
		[globalTriggers:globalTriggers, channelTriggers:channelTriggers];
	}
	
	def create =
	{
		
	}
	
	def save =
	{
		// create a new Trigger from the received parameters and save it, then
		// redirect to the main triggers page
		
		String triggerType = params.triggerType;
		BaseTrigger newTrigger = null;
		switch( triggerType )
		{
			case "GlobalTrigger":
			
				println "Creating Global Trigger";
				newTrigger = new GlobalTrigger();
				newTrigger.name = params.triggerName;
				newTrigger.owner = userService.findUserByUserId( session.user.userId );
				
				break;
				
			case "ChannelTrigger":
			
				println "Creating Channel Trigger"
				newTrigger = new ChannelTrigger();
				newTrigger.name = params.triggerName;
				newTrigger.owner = userService.findUserByUserId( session.user.userId );
			
				Channel channel = channelService.findByName( params.triggerChannel );
				newTrigger.channel = channel;
				
				break;
				
			default:
			
				println "Remote sent bad triggerType value: ${triggerType}";
				break;
			
		}
		
		
		// finish building our trigger from the provided params...
		String criteriaType = params.get("criteriaType.1");
		BaseTriggerCriteria criteria = null;
		switch( criteriaType )
		{
			case "BodyKeywordTriggerCriteria":
				
				println "creating BodyKeywordTriggerCriteria";
				criteria = new BodyKeywordTriggerCriteria();
				criteria.bodyKeyword = params.get( "criteriaValue-1");
				
				break;
				
			case "TagTriggerCriteria":
			
				println "creating TagTriggerCriteria";
				criteria = new TagTriggerCriteria();
				String tag = params.get( "criteriaValue-1");
				if( tag != null && !tag.isEmpty())
				{
					tag = tag.trim().toLowerCase();
					criteria.tag = tag;
				} 
				else
				{
					throw new RuntimeException( "Empty tag name not allowed in Trigger Criteria" );
				}
				break;
				
			case "AboveScoreTriggerCriteria":
			
				println "creating AboveScoreTriggerCriteria";
				criteria = new AboveScoreTriggerCriteria();
				criteria.aboveScoreThreshold = Integer.parseInt( params.get( "criteriaValue-1") );
				criteria.scoreName = "raw";
				break;
				
			case "TitleKeywordTriggerCriteria":
			
				println "creating TitleKeywordTriggerCriteria";
				criteria = new TitleKeywordTriggerCriteria();
				criteria.titleKeyword = params.get( "criteriaValue-1");
				
			case "NewContentTriggerCriteria":
				println "creating NewContentTriggerCriteria";
				criteria = new NewContentTriggerCriteria();
				
				break;
				
			default:
				
				println "bad type";
				break;
		}
		
		// attach the criteria to the trigger!
		newTrigger.addToTriggerCriteria( criteria );
		
		
		
		/* create trigger action(s) */
		String actionType = params.get("actionType.1");
		BaseTriggerAction triggerAction = null;
		switch( actionType )
		{
			case "EmailAction":
				
				triggerAction = new EmailTriggerAction();
				triggerAction.destination = params.get( "actionValue-1");
				break;
				
			case "XmppAction":
			
				triggerAction = new XmppTriggerAction();
				triggerAction.destination = params.get( "actionValue-1");
				break;
				
			case "QuoddyAction":
				
				triggerAction = new QuoddyShareTriggerAction();
				triggerAction.destination = params.get( "actionValue-1");
				
				break;
				
			case "WorkflowAction":
				
				// TODO: implement WorkflowTriggerAction
				// break;
				
			case "HttpAction":
			
				// TODO: implement HttpTriggerAction
				// break;
				
			case "JMSAction":
				
				// TODO: implement JmsTriggerAction
				// break;
				
			case "ScriptAction":
			
				// TODO: implement ScriptTriggerAction
				// break;
				
			default:
			
				println "bad type";
				break;	
		}
		
		
		// attach the action to the trigger!
		newTrigger.addToTriggerActions( triggerAction );
		
		
		triggerService.saveTrigger( newTrigger );
		
		redirect( controller:'trigger', action:'index');
	}
	
	def edit =
	{
		BaseTrigger triggerToEdit = triggerService.findTriggerById( Long.parseLong( params.id ));	
		
		[triggerToEdit: triggerToEdit];
	}
	
	def update =
	{		
		
		BaseTrigger triggerToEdit = triggerService.findTriggerById( Long.parseLong( params.id ) );
			
		triggerToEdit.name = params.triggerName;
		
		
		String criteriaType = params.get("criteriaType.1");
		BaseTriggerCriteria newCriteria = null;
		switch( criteriaType )
		{
			case "BodyKeywordTriggerCriteria":
				
				println "creating BodyKeywordTriggerCriteria";
				newCriteria = new BodyKeywordTriggerCriteria();
				newCriteria.bodyKeyword = params.get( "criteriaValue-1");
				
				break;
				
			case "TagTriggerCriteria":
			
				println "creating TagTriggerCriteria";
				newCriteria = new TagTriggerCriteria();
				String tag = params.get( "criteriaValue-1");
				if( tag != null && !tag.isEmpty())
				{
					tag = tag.trim().toLowerCase();
					newCriteria.tag = tag;
				}
				else
				{
					throw new RuntimeException( "Empty tag name not allowed in Trigger Criteria" );
				}
				break;
				
			case "AboveScoreTriggerCriteria":
			
				println "creating AboveScoreTriggerCriteria";
				newCriteria= new AboveScoreTriggerCriteria();
				newCriteria.aboveScoreThreshold = Integer.parseInt( params.get( "criteriaValue-1") );
				newCriteria.scoreName = "raw";
				break;
				
			case "TitleKeywordTriggerCriteria":
			
				println "creating TitleKeywordTriggerCriteria";
				newCriteria = new TitleKeywordTriggerCriteria();
				newCriteria.titleKeyword = params.get( "criteriaValue-1");
				
				break;
				
			case "NewContentTriggerCriteria":
				println "creating NewContentTriggerCriteria";
				newCriteria = new NewContentTriggerCriteria();
				break;
				
			default:
				
				println "bad type";
				break;
		}
		
		
		
		
		
		String actionType = params.get("actionType.1");
		BaseTriggerAction newTriggerAction = null;
		switch( actionType )
		{
			case "EmailAction":
				
				newTriggerAction = new EmailTriggerAction();
				newTriggerAction.destination = params.get( "actionValue-1");
				break;
				
			case "XmppAction":
			
				newTriggerAction = new XmppTriggerAction();
				newTriggerAction.destination = params.get( "actionValue-1");
				break;
				
			case "QuoddyAction":
				
				newTriggerAction = new QuoddyShareTriggerAction();
				newTriggerAction.destination = params.get( "actionValue-1");
				
				break;
				
			case "WorkflowAction":
				
				// TODO: implement WorkflowTriggerAction
				// break;
				
			case "HttpAction":
			
				// TODO: implement HttpTriggerAction
				// break;
				
			case "JMSAction":
				
				// TODO: implement JmsTriggerAction
				// break;
				
			case "ScriptAction":
			
				// TODO: implement ScriptTriggerAction
				// break;
				
			default:
			
				println "bad type";
				break;
		}
		
		
		triggerService.updateTrigger( triggerToEdit, newCriteria, newTriggerAction );
		
		redirect( controller:'trigger', action:'index');
		
	}
	

	def delete =
	{
		triggerService.deleteTrigger( Long.parseLong( params.id ));
		redirect( controller:'trigger', action:'index');
	}	
	
}
