<!DOCTYPE html>
<html>
    <head>
        <title>
          <g:layoutTitle default="Neddick" />
        </title>
        <nav:resources />
        <link rel="stylesheet" type="text/css" href="${createLinkTo(dir:'css', file:'main.css')}" />
        <link rel="stylesheet" type="text/css" href="${createLinkTo(dir:'css', file:'bootstrap.min.css')}" />
        <link rel="stylesheet" type="text/css" href="${createLinkTo(dir:'css/FontAwesome/css', file:'font-awesome.css')}">
        <link rel="stylesheet" type="text/css" href="${createLinkTo(dir:'css', file:'oagis.css')}" />
        <link rel="stylesheet" type="text/css" href="http://yui.yahooapis.com/3.3.0/build/cssgrids/grids-min.css" />
        
        <link rel="stylesheet" type="text/css"
 href="${createLinkTo(dir:'js/jquery-ui-1.10.3.custom/css/vader', file:'jquery-ui-1.10.3.custom.css') }" />
        
        
        <meta name="viewport" content="width=device-width, initial-scale=1.0">


		<g:javascript library="jquery-ui-1.10.3.custom/js/jquery-1.9.1" />
		<g:javascript library="jquery-ui-1.10.3.custom/js/jquery-ui-1.10.3.custom" />
        
        
        <g:javascript>
          var $j = jQuery.noConflict();
        </g:javascript>
        
        <g:javascript library="dropdown" />
        <g:javascript>
          $j('.dropdown-toggle').dropdown();
       </g:javascript>

        <g:javascript library="jquery.timers-1.2" />

        <g:javascript library="prototype" /> 
        <g:javascript library="scriptaculous" />
        <g:javascript library="effects" />
        <g:javascript library="application" />

		<script type="text/javascript" src="${createLinkTo(dir:'js', file:'bootstrap.js')}" >
		</script>

        <g:javascript>
           function addTag(id ) {
               toggleTagbox(id);
               clearTagbox(id);
           }               
           
           function clearTagbox(id) {
               var tagNameField = document.getElementById( 'tagNameField.' + id );
               tagNameField.value = '';
           }
               
          function toggleTagbox(e) {
          		// alert( e );
               var tagboxDisplay = document.getElementById('tagbox.' + e)
               var toggleText = document.getElementById( 'showHideTagbox.' + e );
               
               if( tagboxDisplay.style.display == 'none' ) {
                    new Effect.BlindDown( 'tagbox.' + e, {duration: 0.5} );
                    toggleText.textContent = "Hide";
               }
               else {    
                    new Effect.BlindUp( 'tagbox.' + e, {duration: 0.5} );
                    toggleText.textContent = "Tag";               
               }
               
               return false;
          }
               
          </g:javascript>        
        
	<script>
	$j(document).ready(function() {
		$j('.carousel').carousel({
			interval : false
		});
	});
</script>        
        
        
        <g:layoutHead />
    </head>
    <body>
    <!-- begin customizable header -->
    <div id="gbw" class="headerNavContainer navbar-top">
    	<div class="container">
    		<div class="headerNav row">
    			<ul class="customNav span6">
    				<!-- TODO: replace this with a template gsp -->
      				<li>
        				<h1><a href="${createLink(controller:'home', action:'index')}">Neddick</a></h1>
      				</li>
      
      
      				<shiro:authenticated>
	      			<li class="navigation_active navigation_first">
	        			<g:if test="${channelName == null }">
	          				<a href="${resource(dir:'', file:'')}"><i class="icon-asterisk"></i> New</a>
	        			</g:if>
	        			<g:else>
	          				<a href="${resource(dir:'r', file:channelName)}"><i class="icon-asterisk"></i> New</a>
	        			</g:else>
	      			</li>
			      	<li class="navigation_active">
			        	<g:if test="${channelName == null }">
			          		<a href="${resource(dir:'home', file:'hotEntries')}"><i class="icon-fire"></i> Hot</a>
			        	</g:if>
			        	<g:else>
			          		<a href="${resource(dir:'r/'+channelName, file:'hotEntries')}"><i class="icon-fire"></i> Hot</a>
			        	</g:else>
			      	</li>
			      	<li class="navigation_active">
			        	<g:if test="${channelName == null }">
			          		<a href="${resource(dir:'home', file:'topEntries')}"><i class="icon-star"></i> Top</a>
			        	</g:if>
			        	<g:else>
			        	  <a href="${resource(dir:'r/'+channelName, file:'topEntries')}"><i class="icon-star"></i> Top</a>
			        	</g:else>
			      	</li>
			      	<li class="navigation_active">
			        	<g:if test="${channelName == null }">
			          		<a href="${resource(dir:'home', file:'controversialEntries')}"><i class="icon-comments"></i>Controversial</a>
			        	</g:if>
			        	<g:else>
			          		<a href="${resource(dir:'r/'+channelName, file:'controversialEntries')}"><i class="icon-comments"></i> Controversial</a>
			        	</g:else>
			      	</li>
			      	<li>
			        	<g:link controller="entry" action="create">Submit</g:link>
			      	</li>
      				</shiro:authenticated>
      			
      				<!-- <li>
        				<!-- <g:link controller="entry" action="createQuestion">Ask a Question</g:link>
      				</li> -->
    			</ul>
    		
    			<div id="gbg" class="span6 settingsNav navbar">
    			<ul>
    
    				<shiro:authenticated>
	    			<li>
	      				<div class="dropdown">
	        				<a class="dropdown-toggle" data-toggle="dropdown" href="#">Settings <i class="icon-sort-down"></i></a>
	          				<ul class="dropdown-menu" role="menu" aria-labelledby="dLabel">
	            				<li>
	            					<g:link controller="channel" action="list">Channels</g:link>
	            				</li>
	            
					            <g:if test="${session.user}">
					            	<li>
					            		<a href="${resource(dir:'home', file:'savedEntries')}">Saved Entries</a></li>
					            	<li>
					            		<g:link controller="tag" action="list">Tags</g:link>
					            	</li>
					            	<li>
					            		<g:link controller="trigger" action="index">Triggers</g:link>
					            	</li>
					            	
					            	<li>
					            		<g:link controller="filter" action="index">Filters</g:link>
					            	</li>            	
					            	    
					            </g:if>
					            <g:if test="${session.user}">
					            	<li>
					              		<g:link controller="channel" action="create">Create New Channel</g:link>
					            	</li>
					            
					            	<li>
					              		<g:link controller="channel" action="edit" id="${channelName}">Edit Channel Properties</g:link>
					            	</li>
					            	
					            	<!--  TODO: hide this if user does not have "admin" role -->
					            	<li>
					            		<g:link controller="admin" action="index">Admin</g:link>
					            	</li>
					            	
					            </g:if>
	          				</ul>
	      				</div> <!-- end <div class="dropdown"> -->
	    			</li>
    				</shiro:authenticated>
    			</ul>
    			<ul class="user">
            		<shiro:authenticated>
              			<li>
              			<div class="dropdown">
	        				<a class="dropdown-toggle" data-toggle="dropdown" href="#">${session.user.userId} <i class="icon-sort-down"></i></a>
	          				<ul class="dropdown-menu" role="menu" aria-labelledby="dLabel">
    		          			<li>
	        		      			<g:link controller="userHome" action="index" id="${session.user.userId}">Profile</g:link>	
              					</li>
            	 				<li>
              						<g:link controller="login" action="logout">Logout</g:link>
              					</li>    
              				</ul>
              			</div>
              			</li>
            		</shiro:authenticated>
            
            		<!-- 
            		<shiro:notAuthenticated>
            		<li>
              			<g:link controller="login" action="index">Login</g:link>                                                                                                            
            		</li>
            		</shiro:notAuthenticated>
     				-->        
            		<!--
            		<li>
            			<g:link controller="user" action="create">Register</g:link>
            		</li> -->
            
		            <g:render template="/sidebar" />
            	</ul>
            	
  				</div> <!--  end <div class="span6 settingsNav navbar" -->
  			
  			</div> <!--  end <div class="headerNav row"> -->
  		
  		</div> <!-- end <div class="container"> -->
  	
  	</div>
  	
    <div class="row">   
    <div class="span8" style="margin-top:10px;">     
    	
    	<!-- layout main content area -->
        <div class="well" style="min-height:300px">
        	<g:layoutBody />
        </div>            
                                    
    </div>
	<div class="span4" style="margin-top:10px;">
	
		<div class="well" style="min-height:300px;">
			<h3>Recent Comments</h3>
			<hr />
			<ul>
				<g:each in="${recentComments}" var="recentComment">
					<li style="list-style-type:none;margin-top:7px;">
					<a href="#comment-${recentComment.uuid}">
					${recentComment.text}
					</a>
					</li>
				</g:each>
			</ul>
			
		</div>
		

		<div class="well" style="min-height:300px;">
			<h3>Related Links</h3>
			<hr />
			<ul>
				<g:each in="${recommendedEntries}" var="recommendedEntry">
					<li style="list-style-type:none;margin-top:7px;">
						<g:link controller="entry" action="e" id="${recommendedEntry.uuid}" >${recommendedEntry.title}</g:link>
					</li>
				</g:each>
			</ul>
		</div>
		
		
		<div class="well" style="min-height:300px;">
			<h3>Faceted Browser</h3>
			<hr />
		</div>				
		
		
		
	</div>                     
    </div>
    <div class="row">
    <div class="span12" id="ft">
         
         <!-- TODO: replace this with a template gsp -->
         
         <!-- footer -->
         <div>
              <center>Footer for Neddick</center>
         </div>
    </div> 
    </div>      
          
          		 <!--  hidden div, not shown until somebody clicks the "share" button -->
				 <div id="shareDialog" title="Share this Item">
					
					<g:formRemote name="shareItemForm" url="[controller: 'share', action:'shareItem']">
					
						<!--  we need a way to specify who/what you are sharing this item WITH -->
						<!--  a set of checkboxes could work, or a multi-select select box -->
						<!--  let's go with checkboxes for now, just because it's the easiest way -->
						<div>  
							<input type="checkbox" style="display:inline-block;overflow:hidden;" value="shareEmail" name="shareEmailCheck" id="shareEmailCheck" />
							<label style="display:inline-block;overflow:hidden;color:red" for="shareEmailCheck">Email</label> 
							<input type="checkbox" style="display:inline-block;overflow:hidden;" value="shareXmpp" name="shareXmppCheck" id="shareXmppCheck" />
							<label style="display:inline-block;overflow:hidden;color:red" for="shareXmppCheck">Xmpp</label>
							<input type="checkbox" style="display:inline-block;overflow:hidden;" value="shareQuoddy" name="shareQuoddyCheck" id="shareQuoddyCheck" />
							<label style="display:inline-block;overflow:hidden;color:red" for="shareQuoddyCheck">Quoddy</label>
						</div>
							
						<!--  the uuid of the thing being shared -->
						<input id="shareItemUuid" name="shareItemUuid" type="hidden" value="" />
						
						<!-- the permalink of the thing being shared -->
						<input id="permaLink" name="permaLink" type="hidden" value="" />
						
						<div style="margin-top:20px;margin-bottom:30px;">
							<!--  here we have inputs for the various shareTargets.  We'll need boxes for
							 "email", "xmpp" and "quoddy" and we'll want all three to start out hidden, then we can
							  reveal them if/when the user checks the appropriate checkbox above -->	
							<label id="forShareTargetEmail" name="forShareTargetEmail" style="display:none;color:red;" for="shareTargetEmail">Email:</label>
								<input style="display:none;" name="shareTargetEmail" id="shareTargetEmail" type="text" value="" />
							<label id="forShareTargetXmpp" name="forShareTargetXmpp" style="display:none;color:red;" for="shareTargetXmpp">Xmpp:</label>
								<input style="display:none;" name="shareTargetXmpp" id="shareTargetXmpp" type="text" value="" />
							<label id="forShareTargetQuoddy" name="forShareTargetQuoddy" style="display:none;color:red;" for="shareTargetQuoddy">Quoddy:</label>
								<input style="display:none;" name="shareTargetQuoddy" id="shareTargetQuoddy" type="text" value="" />
						
						</div>
						
						<!--  text of an (optional) comment -->
						<label style="color:red;" id="forShareItemComment" name="forShareItemComment" for="shareItemComment">Comment: </label>
							<input id="shareItemComment" name="shareItemComment" type="text" value="" />
						
						
					</g:formRemote>
				</div>         
          		<!-- end hidden div, not shown until somebody clicks the "share" button -->
                      
    </body>	
</html>