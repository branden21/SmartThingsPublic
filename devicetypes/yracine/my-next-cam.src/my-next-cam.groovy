/***
*  My Next Cam
*  Copyright 2018 Yves Racine
*  Version v1.1.5
*
*  Based mainly on Image Capture and Video Streaming - courtesy of Patrick Stuart
*  NST manager's Camera streaming (backend processing with dropcam server) was also used as a reference - authors: tonesto7 & E_sch (ST community handles)
*  LinkedIn profile: ca.linkedin.com/pub/yves-racine-m-sc-a/0/406/4b/
*  Refer to readme file for installation instructions.
* 
*  Licensed under the Apache License, Version 2.0 (the 'License'), you may not use this file except in compliance with the License.
*  You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0

*  Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on 
*  an 'AS IS' BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  
*  See the License for the specific language governing permissions and limitations under the License.
*  
*  Developer notice:Software Distribution is restricted and shall be done only with Developer's written approval.
***/

import java.text.SimpleDateFormat

include 'asynchttp_v1'

// for the UI

preferences {

//	Preferences are no longer required when created with the Service Manager (MyNextServiceMgr).

	input("structureId", "text", title: "StructureId", description:
		"The structureId of your location\n(not needed when using MyNextServiceMgr, leave it blank)")
	input("cameraId", "text", title: "internal Id", description:
		"internal Camera Id\n(not needed when using MyNextServiceMgr, leave it blank)")
	input("trace", "bool", title: "trace", description:
		"Set it to true to enable tracing (no spaces) or leave it empty (no tracing)")
	input("logFilter", "number",title: "(1=ERROR only,2=<1+WARNING>,3=<2+INFO>,4=<3+DEBUG>,5=<4+TRACE>)",  range: "1..5",
 		description: "optional" )        
}
metadata {
	definition(name: "My Next Cam", author: "Yves Racine", namespace: "yracine") {
		capability "Image Capture"
		capability "Sensor"
		capability "Switch"
		capability "Refresh"
		capability "Polling"
		capability "Video Camera"
		capability "Video Capture"
		capability "Health Check"
		capability "Presence Sensor"
        
/*
		strcuture attributes 
*/        

		attribute "structure_id","string"
		attribute "st_away","string"
		attribute "st_name","string"
		attribute "st_country_code","string"
		attribute "st_postal_code","string"
		attribute "st_peak_period_start_time","string"
		attribute "st_peak_period_end_time","string"
		attribute "st_time_zone","string"
		attribute "st_eta_begin","string"
		attribute "st_wwn_security_state","string"        

		attribute "cameraId","string"
 		attribute "cameraName","string"
 		attribute "camerasList","string"
		attribute "locale","string"
		attribute "software_version","string"
		attribute "where_id","string"
		attribute "where_name","string"
		attribute "label","string"
		attribute "name_long","string"
		attribute "is_online","string"
		attribute "last_connection","string"
		attribute "last_api_check","string"
		attribute "device_id","string"
		attribute "is_streaming","string"
		attribute "isStreaming","string"        
		attribute "is_audio_input_enabled","string"
		attribute "last_is_online_change","string"
		attribute "is_video_history_enabled","string"
		attribute "web_url","string"
		attribute "app_url","string"
		attribute "is_public_share_enabled","string"
		attribute "activity_zones","string"
		attribute "ZoneNames","string"
		attribute "public_share_url","string"
		attribute "snapshot_url","string"
		attribute "last_event","string"
		attribute "last_event_person","string"
		attribute "last_event_sound","string"
		attribute "last_event_motion","string"
		attribute "last_event_app_url","string"
		attribute "last_event_web_url","string"
		attribute "last_event_start_time","string"
		attribute "last_event_end_time","string"
		attribute "last_event_urls_expire_time","string"
		attribute "last_event_animated_image_url","string"
		attribute "last_event_activity_zone_ids","string"
		attribute "verboseTrace", "string"
		attribute "summaryReport", "string"
        
		command "getStructure"        
		command "setStructure"        
		command "setStructureHome"
		command "setStructureAway"
		command "away"
		command "home"
		command "present"
		command "getCameraInfo"        
		command "getCameraList"
		command "setCameraSettings"        
		command "produceSummaryReport" 

		command "start"
		command "stop"
		command "setStreaming"        
	}

	mappings {
		path("/getInHomeURL") {
			action: [GET: "getInHomeURL"]
		}
	}

	simulator {

	}

tiles(scale: 2) {
		multiAttributeTile(name: "videoPlayer", type: "videoPlayer", width: 6, height: 4) {
			tileAttribute("device.switch", key: "CAMERA_STATUS") {
				attributeState("on", label: "Active", icon: "st.camera.dlink-indoor", action: "vidOff", backgroundColor: "#79b821", defaultState: true)
				attributeState("off", label: "Inactive", icon: "st.camera.dlink-indoor", action: "vidOn", backgroundColor: "#ffffff")
				attributeState("restarting", label: "Connecting", icon: "st.camera.dlink-indoor", backgroundColor: "#53a7c0")
				attributeState("unavailable", label: "Unavailable", icon: "st.camera.dlink-indoor", action: "refresh.refresh", backgroundColor: "#F22000")
			}

			tileAttribute("device.errorMessage", key: "CAMERA_ERROR_MESSAGE") {
				attributeState("errorMessage", label: "", value: "", defaultState: true)
			}

			tileAttribute("device.camera", key: "PRIMARY_CONTROL") {
				attributeState("on", label: "Active", icon: "st.camera.dlink-indoor", backgroundColor: "#79b821", defaultState: true)
				attributeState("off", label: "Inactive", icon: "st.camera.dlink-indoor", backgroundColor: "#ffffff")
				attributeState("restarting", label: "Connecting", icon: "st.camera.dlink-indoor", backgroundColor: "#53a7c0")
				attributeState("unavailable", label: "Unavailable", icon: "st.camera.dlink-indoor", backgroundColor: "#F22000")
			}

			tileAttribute("device.startLive", key: "START_LIVE") {
				attributeState("live", action: "start", defaultState: true)
			}

			tileAttribute("device.stream", key: "STREAM_URL") {
				attributeState("activeURL", defaultState: true)
			}
		}
		standardTile("isStreaming", "device.isStreaming", width: 2, height: 2, decoration: "flat") {
			state("on", action: "setStreaming", nextState: "updating", icon: "${getCustomImagePath()}CamLiveStream.png")
			state("off", action: "setStreaming", nextState: "updating", icon: "${getCustomImagePath()}CamLiveOff.png")
			state("updating", icon: "${getCustomImagePath()}updateIcon.jpg")
			state("offline", icon: "${getCustomImagePath()}Offline.jpg")
			state("unavailable", icon: "${getCustomImagePath()}Unavailable.png")
		}
		carouselTile("cameraDetails", "device.image", width: 6, height: 4) {}
		standardTile("take", "device.image", width: 2, height: 2, canChangeIcon: false, inactiveLabel: true, canChangeBackground: false) {
			state "take", label: "Take", action: "Image Capture.take", icon: "st.camera.camera", backgroundColor: "#FFFFFF", nextState: "taking"
			state "taking", label: 'Taking', action: "", icon: "st.camera.take-photo", backgroundColor: "#53a7c0"
			state "image", label: "Take", action: "Image Capture.take", icon: "st.camera.camera", backgroundColor: "#FFFFFF", nextState: "taking"
		}

		standardTile("refresh", "command.refresh", inactiveLabel: false,width: 2, height: 2) {
			state "default", label: 'refresh', action: "refresh.refresh", icon: "st.secondary.refresh-icon"
		}
		standardTile("motion", "device.switch", width: 2, height: 2, canChangeIcon: false) {
			state "off", label: 'Motion\nOff',  icon: "st.motion.motion.inactive", backgroundColor: "#FFFFFF"
			state "on", label: 'Motion\nOn', icon: "st.motion.motion.active", backgroundColor: "#00a0dc"
		}
 		valueTile(	"lastConnection", "device.last_connection",width: 2, height: 2,canChangeIcon: false,decoration: "flat") {
			state("default",
				label:'LastConnect ${currentValue}',
				backgroundColor: "#ffffff"
			)
		}
		valueTile(	"lastAPICheck", "device.last_api_check",width: 2, height: 2,canChangeIcon: false,decoration: "flat") {
 			state("default",
				label:'LastAPICheck ${currentValue}',
				backgroundColor: "#ffffff"
			)
		}
		valueTile(	"lastEventStart", "device.last_event_start_time",width: 2, height: 2,canChangeIcon: false,decoration: "flat") {
 			state("default",
				label:'LastEvent\nStartTime ${currentValue}',
				backgroundColor: "#ffffff"
			)				                
	
		}
		valueTile(	"lastEventEnd", "device.last_event_end_time",width: 2, height: 2,canChangeIcon: false,decoration: "flat") {
 			state("default",
				label:'LastEvent\nEndTime ${currentValue}',
				backgroundColor: "#ffffff"
			)
		}
		standardTile("activityZones", "device.zoneNames",width: 6, height: 2,canChangeIcon: false,decoration: "flat") {
 			state("default",
				label:'Triggered Zone(s)\n ${currentValue}',
				backgroundColor: "#ffffff"
			)
		}
 
		main "motion"
		details(["videoPlayer",
			"isStreaming",
			"take",
			"cameraDetails",
			"motion", "lastEventStart", "lastEventEnd",
			"activityZones",            
			"lastAPICheck",            
			"refresh"])
	}
}
def getInHomeURL() { return [InHomeURL: getDropcamPlaylistURL().toString()] }





// cameraId		single cameraId 
// asyncValues		values from async poll
// RTeventFlag		Indicates whether or not RT events were sent by Nest
private def refresh_camera(cameraId="", asyncValues=null, RTEventFlag=false) {
	def structure
	cameraId=determine_camera_id(cameraId) 
    
	def scale = getTemperatureScale()
	state?.scale= scale    
	def todayDay = new Date().format("dd",location.timeZone)
  	if ((!state?.today) || (state?.today != todayDay))  {
//		structure=getStructure(structureId,true)    
		state?.today=todayDay        
	}  
	if (!data?.cameras) {
		data?.cameras=[:]
	}        

	if ((!asyncValues) && (!RTEventFlag)) {
		getCameraInfo(cameraId)
		String exceptionCheck = device.currentValue("verboseTrace")
		if ((exceptionCheck) && ((exceptionCheck?.contains("exception")) || (exceptionCheck?.contains("error")))) {
		// check if there is any exception or an error reported in the verboseTrace associated to the device 
			traceEvent(settings.logFilter, "refresh_camera>$exceptionCheck", true, get_LOG_ERROR()) 
			return    
		            
		}    
	}  else if (asyncValues) { 
		if (asyncValues instanceof Object[]) {
			data?.cameras=asyncValues
		} else {
			data?.cameras."$cameraId"=asyncValues
		}			        
	}
    
	String last_recorded_event_start_time = device.currentValue("last_event_start_time")   
	def last_recorded_event = device.currentValue("last_event")   
	def dataEvents = [
		cameraId:  data?.cameras?."$cameraId"?.device_id,
 		cameraName:data?.cameras?."$cameraId"?.name,
		"structure_id": data?.cameras?."$cameraId"?.structure_id,
		"locale": data?.cameras?."$cameraId"?.locale,
		"software_version": data?.cameras?."$cameraId"?.software_version,
		"where_id": data?.cameras?."$cameraId"?.where_id,
		"where_name": data?.cameras?."$cameraId"?.where_name,
		"label": data?.cameras?."$cameraId"?.label,
		"name_long":data?.cameras?."$cameraId"?.name_long,
		"is_online": data?.cameras?."$cameraId"?.is_online,
//		"last_connection": (data?.cameras?."$cameraId"?.last_connection)?formatDateInLocalTime(data?.cameras?."$cameraId"?.last_connection)?.substring(0,16):"",
		"last_api_check": formatTimeInTimezone(now())?.substring(0,16),
		"device_id": data?.cameras?."$cameraId"?.device_id,
		"is_streaming":data?.cameras?."$cameraId"?.is_streaming,
		"is_audio_input_enabled": data?.cameras?."$cameraId"?.is_audio_input_enabled,
		"last_is_online_change": (data?.cameras?."$cameraId"?.last_is_online_change)?formatDateInLocalTime(data?.cameras?."$cameraId"?.last_is_online_change)?.substring(0,16):"",
		"is_video_history_enabled":data?.cameras?."$cameraId"?.is_video_history_enabled,
		"web_url": data?.cameras?."$cameraId"?.web_url,
		"app_url": data?.cameras?."$cameraId"?.app_url,
		"is_public_share_enabled": data?.cameras?."$cameraId"?.is_public_share_enabled,
		"activity_zones": data?.cameras?."$cameraId"?.activity_zones,
		"public_share_url": data?.cameras?."$cameraId"?.public_share_url,
		"snapshot_url": data?.cameras?."$cameraId"?.snapshot_url
 	]
    
	String current_event_start_time= (data?.cameras?."$cameraId"?.last_event?.start_time)?  formatDateInLocalTime(data?.cameras?."$cameraId"?.last_event?.start_time)?.substring(0,16):"" 
	if ((dataEvents?.is_video_history_enabled?.toString()=='true') && (current_event_start_time != last_recorded_event_start_time)) {
		traceEvent(settings.logFilter,"refresh_camera> current_event_start_time: $current_event_start_time, last_event_start_time:$last_recorded_event_start_time",settings.trace)
		dataEvents.last_event= data?.cameras?."$cameraId"?.last_event
		dataEvents.last_event_person= data?.cameras?."$cameraId"?.last_event?.has_person 
		dataEvents.last_event_motion= data?.cameras?."$cameraId"?.last_event?.has_motion
		dataEvents.last_event_sound= data?.cameras?."$cameraId"?.last_event?.has_sound
		dataEvents.last_event_start_time= (data?.cameras?."$cameraId"?.last_event?.start_time) ? formatDateInLocalTime(data?.cameras?."$cameraId"?.last_event?.start_time)?.substring(0,16):""       
		dataEvents.last_event_end_time= (data?.cameras?."$cameraId"?.last_event?.end_time) ? formatDateInLocalTime(data?.cameras?."$cameraId"?.last_event?.end_time)?.substring(0,16):""      
		dataEvents.last_event_app_url= data?.cameras?."$cameraId"?.last_event?.app_url    
		dataEvents.last_event_web_url= data?.cameras?."$cameraId"?.last_event?.web_url    
		dataEvents.last_event_urls_expire_time= (data?.cameras?."$cameraId"?.last_event?.urls_expire_time)? formatDateInLocalTime(data?.cameras?."$cameraId"?.last_event?.urls_expire_time)?.substring(0,16):""        
		dataEvents.last_event_animated_image_url=data?.cameras?."$cameraId"?.last_event?.animated_image_url
		dataEvents.last_event_activity_zone_ids=data?.cameras?."$cameraId"?.last_event?.activity_zone_ids
		        
	}  
	if (dataEvents?.last_event_activity_zone_ids) {
		def zones="" 
		dataEvents?.last_event_activity_zone_ids.each { zone->
			def foundZone = dataEvents?.activity_zones?.find{it.id.toString()==zone.toString()}	
			zones=zones+ foundZone?.name + ','        
		}      
		zones=zones.substring(0,(zones.length()-1)) // remove the extra ','        
		dataEvents?.zoneNames=zones       
	} else {
		dataEvents?.zoneNames='None'       
	}    
    
	if (dataEvents?.last_event_animated_image_url) {
		snapPicture( dataEvents?.last_event_animated_image_url)   
	} else if ( dataEvents?.snapshot_url){
		snapPicture(dataEvents?.snapshot_url)   
	}    
	generateEvent(dataEvents)  
    
	initializeStreamingContext(dataEvents?.public_share_url)
	if (checkIfStreaming(dataEvents?.is_streaming)) {
    
		traceEvent(settings.logFilter, "refresh_camera>camera is able to stream now") 
	}    
	def structureId=determine_structure_id(dataEvents?.structure_id)
	traceEvent(settings.logFilter, "refresh_camera>about to call getStructure") 
	structure=getStructure(structureId,false)    
	if (structure) {
		traceEvent(settings.logFilter, "refresh_camera>structure name= $stucture?.name, values=$structure") 
		def list =""   
		structure?.cameras?.each {
			list=list + it + ','    
		}    
		dataEvents= [
			camerasList: list,      
			"st_away": structure?.away,
			"st_name":structure?.name,
			"st_country_code": structure?.country_code,
			"st_postal_code":structure?.postal_code,
			"st_peak_period_start_time": (structure?.peak_period_start_time)?formatDateInLocalTime(structure?.peak_period_start_time)?.substring(0,16):"",
			"st_peak_period_end_time":(structure?.peak_period_end_time) ?formatDateInLocalTime(structure?.peak_period_end_time)?.substring(0,16):"",
			"st_time_zone":structure?.time_zone,
			"st_eta_begin":(structure?.eta_begin)?formatDateInLocalTime(structure?.eta_begin)?.substring(0,16):"",
			"st_wwn_security_state": structure?.wwn_security_state       
		]
		if (dataEvents?.st_away == 'away') { 
			dataEvents?.presence= "not present"
		} else {        
			dataEvents?.presence= "present"
		}            
		generateEvent(dataEvents)        
	}    
		
	traceEvent(settings.logFilter,"refresh_camera>done for cameraId =${cameraId}", settings.trace)
}


// refresh() has a different polling interval as it is called by lastPollTimestampthe UI (contrary to poll).
void refresh() {
	def cameraId= determine_camera_id("") 	    
	def poll_interval=1  // set a 1 min. poll interval to avoid unecessary load on Nest servers
	def time_check_for_poll = (now() - (poll_interval * 60 * 1000))
	if ((state?.lastPollTimestamp) && (state?.lastPollTimestamp > time_check_for_poll)) {
		traceEvent(settings.logFilter,"refresh>cameraId = ${cameraId},time_check_for_poll (${time_check_for_poll} < state.lastPollTimestamp (${state.lastPollTimestamp}), not refreshing data...",
			settings.trace)	            
		return
	}
	state.lastPollTimestamp = now()
	refresh_camera(cameraId)
  
}



def getCameraInfoAsync(cameraId) {
	String URI_ROOT = "${get_API_URI_ROOT()}"

	cameraId=determine_camera_id(cameraId)

	if (isTokenExpired()) {
//		traceEvent(settings.logFilter,"getCameraInfoAsync>need to refresh tokens", settings.trace, get_LOG_WARN())
       
		if (!refresh_tokens()) {
//			traceEvent(settings.logFilter,"getCameraInfoAsync>not able to renew the refresh token", settings.trace, get_LOG_WARN())         
		} else {
        
			// Reset Exceptions counter as the refresh_tokens() call has been successful 
			state?.exceptionCount=0
		}            
        
	}
	traceEvent(settings.logFilter,"getCameraInfoAsync>about to call pollAsyncResponse with cameraId = ${cameraId}...", settings.trace)
	def request = [
			id: "$cameraId",
			method:"getCameraInfoAsync"            
		]
    
	def params = [
		uri: "${URI_ROOT}/devices/cameras/${cameraId}",
		headers: [
			'Authorization': "${data.auth.token_type} ${data.auth.access_token}",
			'Content-Type':  "application/json",
			'charset': "UTF-8"
		]
	]
	asynchttp_v1.get("pollAsyncResponse",params, request)


}
void poll() {
   
	def cameraId= determine_camera_id("") 	    

	def poll_interval=1  // set a minimum of 1min. poll interval to avoid unecessary load on Nest servers
	def time_check_for_poll = (now() - (poll_interval * 60 * 1000))
	if ((state?.lastPollTimestamp) && (state?.lastPollTimestamp > time_check_for_poll)) {
		traceEvent(settings.logFilter,"poll>cameraId = ${cameraId},time_check_for_poll (${time_check_for_poll} < state.lastPollTimestamp (${state.lastPollTimestamp}), not refreshing data...",
			settings.trace, get_LOG_INFO())            
		return
	}
	getCameraInfoAsync(cameraId)    
	traceEvent(settings.logFilter,"poll>done for cameraId =${cameraId}", settings.trace)

}

def pollAsyncResponse(response, data) {	
	def TOKEN_EXPIRED=401
	def REDIRECT_ERROR=307    
	def BLOCKED=429    
    
	def cameraId = data?.id
	def method=data?.method
    
	state?.lastStatusCode=response.status				                
	if (response.hasError()) {
    
		if (response.status== REDIRECT_ERROR) {
			if (!process_redirectURL( response?.headers.Location)) {
				traceEvent(settings.logFilter,"pollAsyncResponse>Nest redirect: too many redirects, count =${state?.redirectURLcount}", true, get_LOG_ERROR())
				return                
			}
			getCameraInfoAsync(cameraId)           
		} else if (response.status ==BLOCKED) {
			state?.retriesCounter=(state?.retriesCounter?:0)+1                 
			traceEvent(settings.logFilter,"pollAsyncResponse>cameraId=${cameraId},Nest throttling in progress,error $response.status,retries counter=${state?.retriesCounter}", settings.trace, get_LOG_WARN())
			if ((!get_exception_interval_delay( state?.retriesCounter, "GETTER"))) {   
				traceEvent(settings.logFilter,"pollAsyncResponse>too many retries", true, get_LOG_ERROR())
				state?.retriesCounter=0            
				return        
			}        
			state.lastPollTimestamp = (now() + (1 * state?.retriesCounter * 60 * 1000))   // set a minimum of 1min. interval to avoid unecessary load on Nest servers
			runIn((state?.retriesCounter*60),"getCameraInfoAsync")
		} else if (response.status == TOKEN_EXPIRED) { // token is expired
			traceEvent(settings.logFilter,"pollAsyncResponse>Nest's Access token has expired for $data, need to re-login at Nest...", settings.trace, get_LOG_WARN())
		} else {         
			if (state?.redirectURLcount >1 && state?.exceptionCount>get_MAX_ERROR_WITH_REDIRECT_URL()) {
				save_redirectURL(null)  // remove redirection, doesn't seem to work
			}                 
			traceEvent(settings.logFilter,"pollAsyncResponse>Nest response error:  $response.status, $response.errorMessage for $data", true, get_LOG_ERROR())
			state?.exceptionCount=state?.exceptionCount +1        
		}        
	} else {
		state?.redirectURLcount=0
		state?.retriesCounter=0        
		def responseValues=null    
		try {
			// json response already parsed into JSONElement object
			responseValues = response.json    
		} catch (e) {
			traceEvent(settings.logFilter,"pollAsyncResponse>Nest - error parsing json from response: $e", settings.trace, get_LOG_ERROR())
			return            
		}
		if (responseValues) {
			if (!data?.cameras) {
				data?.cameras=[:]
			}        
			data?.cameras?."$cameraId"=responseValues			            
			def camName = data?.cameras?."$cameraId".name           
			def	locale=data?.cameras?."$cameraId".locale
			def where_id=data?.cameras?."$cameraId".where_id
			def	label=data?.cameras?."$cameraId".label
			def is_online=data?.cameras?."$cameraId".is_online.toString()
			def is_streaming=data?.cameras?."$cameraId".is_streaming.toString()
//			def last_connection=data?.cameras?."$cameraId".last_connection
			def last_event_start_time=data?.cameras?."$cameraId"?.last_event?.start_time
			def last_event_end_time=data?.cameras?."$cameraId"?.last_event?.end_time
			traceEvent(settings.logFilter,"getCameraInfoAsync> cameraId=${cameraId}, camName=$camName, last_event_start_time=$last_event_start_time,last_event_end_time=$last_event_end_time"  +
				" locale=$locale, where_id=$where_id, label=$label,is_online=$is_online,is_streaming=$is_streaming",     
				settings.trace)                        
				refresh_camera(cameraId, responseValues) 
			state.lastPollTimestamp = now()
			state?.exceptionCount=0                 
			       
		}                
                
	}        
}    


private void generateEvent(Map results) {
	traceEvent(settings.logFilter,"generateEvent>parsing data $results", settings.trace)
    
	state?.scale = getTemperatureScale() // make sure to display in the right scale
	def scale = state?.scale
	if (results) {
		results.each { name, value ->
			def isDisplayed = true

			String upperFieldName=name.toUpperCase()    

			if (upperFieldName?.contains("LAST_EVENT")) { // data variable names contain 'last_event' 

				sendEvent(name: name, value: value, displayed: (settings.trace?:false), isStateChange: true)                                     									 


			} else if (upperFieldName?.contains("DATA")) { // data variable names contain 'data'

				sendEvent(name: name, value: value, displayed: (settings.trace?:false))                                     									 

			} else if (value != null && value.toString() != 'null' && value.toString() != '[:]' && value.toString() != '[]') {           
				def isChange = isStateChange(device, name, value.toString())
				isDisplayed = isChange
				sendEvent(name: name, value: value.toString(), isStateChange: isChange, displayed: isDisplayed)       
			}
		}
	}
}


void take() {
	def cameraId=determine_camera_id("")
	snapPicture(data?.cameras."${cameraId}".snapshot_url)
}


// handle commands
def snapPicture(url) {
	traceEvent(settings.logFilter,"snapPicture>About to snap picture using URL $url", settings.trace)
	def params = [
		uri: url,
		headers: [
			'Authorization': "${data.auth.token_type} ${data.auth.access_token}",
			'Content-Type': "application/x-www-form-urlencoded"
		]
	]

	if((!isOnline()) || (!url?.startsWith("https://"))) {
		traceEvent(settings.logFilter,"snapPicture>not online or bad url", settings.trace, get_LOG_ERROR())
		return false      
	}    
	try {
		ByteArrayInputStream imageBytes    
		httpGet(params) { resp ->
			imageBytes = resp?.data
			if (imageBytes) {
				traceEvent(settings.logFilter,"snapPicture>found image", settings.trace)
				try {
					storeImage(getPictureName(), imageBytes)
				} catch (Exception e) {
					traceEvent(settings.logFilter,"snapPicture>exception $e while storing current image", settings.trace)
				}
                
				return true
			}                
		}			
	} catch (Exception e) {
			traceEvent(settings.logFilter,"snapPicture>Hit Exception $e while snapping picture",settings.trace)
	}
}

def setStreaming(value='off') {
	def cameraId=determine_camera_id("")
	boolean is_streaming_value=false
	def isStreaming = device.currentValue("isStreaming")
	if (isStreaming=='on' && value=='off') {
		is_streaming_value=false		    
	} else {
		is_streaming_value=true	
	}    
	setCameraSettings(cameraId,["is_streaming":is_streaming_value])
	sendEvent(name:"isStreaming", value: value, displayed:true) 
	runIn(30,"refresh_camera")    
}



// cameraId may only be a specific cameraId or "" (for current camera)
// To be called after a poll() or refresh() to have the latest status


private void api( method, id, args=null, success = {}) {
	def MAX_EXCEPTION_COUNT=20
	String URI_ROOT = "${get_API_URI_ROOT()}"
    
   
	if (isLoggedIn() && isTokenExpired()) {
//		traceEvent(settings.logFilter,"api>need to refresh tokens",settings.trace)
       
		if (!refresh_tokens()) {
			if ((exceptionCheck) && (state.exceptionCount >= MAX_EXCEPTION_COUNT) && (exceptionCheck?.contains("Unauthorized"))) {
//				traceEvent(settings.logFilter,"api>$exceptionCheck, not able to renew the refresh token;need to re-login to Nest via MyNestInit....", true, get_LOG_ERROR())         
			}
		} else {
        
			// Reset Exceptions counter as the refresh_tokens() call has been successful 
			state.exceptionCount=0
		}            
        
	}

	def methods = [
		'cameraList': 
			[uri:"${URI_ROOT}/structures/$id/cameras", 
      			type:'get'],
		'cameraInfo': 
			[uri:"${URI_ROOT}/devices/cameras/$id", 
          		type: 'get'],
		'setStructure':
			[uri: "${URI_ROOT}/structures/$id", type: 'put'],
		'setCameraSettings':
			[uri: "${URI_ROOT}/devices/cameras/$id", type: 'put']
		]
	def request = methods.getAt(method)
	traceEvent(settings.logFilter,"api> about to call doRequest with (unencoded) args = ${args}", settings.trace)
	doRequest(request.uri, args, request.type, success)
	if (request.type=="get" && args) {
		def args_encoded = java.net.URLEncoder.encode(args.toString(), "UTF-8")
		request.uri=request.uri + "?${args_encoded}"    
//		request.uri=request.uri + "?${args}"    
	}    
     
	if (state.exceptionCount >= MAX_EXCEPTION_COUNT) {
		def exceptionCheck=device.currentValue("verboseTrace")
		traceEvent(settings.logFilter,"api>error: found a high number of exceptions (${state.exceptionCount}), last exceptionCheck=${exceptionCheck}, about to reset counter",
			settings.trace, get_LOG_ERROR())  
		if (!exceptionCheck?.contains("Unauthorized")) {          
			state.exceptionCount = 0  // reset the counter as long it's not unauthorized exception
			sendEvent(name: "verboseTrace", value: "", displayed:(settings.trace?:false)) // reset verboseTrace            
		}            
	}        

}

// Need to be authenticated in before this is called.
private void doRequest(uri, args, type, success) {
	def params = [
		uri: uri,
		headers: [
			'Authorization': "${data.auth.token_type} ${data.auth.access_token}",
			'Content-Type': "application/json",
			'charset': "UTF-8"		
		],
		body:[]        
	]
	traceEvent(settings.logFilter,"doRequest>about to ${type} with params= ${params},", settings.trace)
	try {
		traceEvent(settings.logFilter,"doRequest>about to ${type} with uri ${params.uri}, (encoded)args= ${args}",settings.trace)
		if (type == 'put') {
			params?.body = args.toString()
			httpPutJson(params, success)

		} else if (type == 'get') {
			httpGet(params, success)
		}
		/* when success, reset the exception counter */
		state.exceptionCount=0
		traceEvent(settings.logFilter,"doRequest>done with ${type}", settings.trace)

	} catch (java.net.UnknownHostException e) {
		traceEvent(settings.logFilter,"doRequest> Unknown host ${params.uri}", settings.trace, get_LOG_ERROR())
	} catch (java.net.NoRouteToHostException e) {
		traceEvent(settings.logFilter,"doRequest>No route to host - check the URL ${params.uri} ", settings.trace, get_LOG_ERROR())
	} catch (e) {
		traceEvent(settings.logFilter,"doRequest>exception $e,error response=${e?.response?.data} for ${params.uri}", settings.trace, get_LOG_ERROR())
		state?.exceptionCount=state?.exceptionCount+1
		if (state?.redirectURL && state?.exceptionCount>get_MAX_ERROR_WITH_REDIRECT_URL()) {
			save_redirectURL(null) // remove redirection as it's not working        
		}        
	} 
}

void produceSummaryReport(pastDaysCount) {
	traceEvent(settings.logFilter,"produceSummaryReport>begin",settings.trace, get_LOG_TRACE())
	def countEvents, countPersonEvents, countSoundEvents,countMotionEvents   
	boolean found_values=false
	Date todayDate = new Date()
	Date startOfPeriod = todayDate - pastDaysCount
	long min_cam_timestamp,max_cam_timestamp

	def last_event_start_time = device.currentValue("last_event_start_time")
	def last_event_end_time = device.currentValue("last_event_end_time")
	def last_event_person = device.currentValue("last_event_person")
	def last_event_motion = device.currentValue("last_event_motion")
	def last_event_sound = device.currentValue("last_event_sound")
	def events = device.statesSince("last_event", startOfPeriod, [max:1000])
	def personEvents = device.statesSince("last_event_person", startOfPeriod, [max:1000])
	def motionEvents = device.statesSince("last_event_motion", startOfPeriod, [max:1000])
	def soundEvents = device.statesSince("last_event_sound", startOfPeriod, [max:1000])
	def event_with_min_cam_timestamp,event_with_max_cam_timestamp
	if (events) {    
		countEvents =  events.count{it}
		countPersonEvents =  personEvents.count{it.value.toString()=='true'}
		countMotionEvents =  motionEvents.count{it.value.toString()=='true'}
		countSoundEvents =  soundEvents.count{it.value.toString()=='true'}
		event_with_min_cam_timestamp=events.min{it?.date.getTime()}  		        
		event_with_max_cam_timestamp=events.max{it?.date.getTime()}		        
		max_cam_timestamp= (event_with_max_cam_timestamp) ? event_with_max_cam_timestamp.date.getTime() : null
		min_cam_timestamp= (event_with_min_cam_timestamp) ? event_with_min_cam_timestamp.date.getTime() : null
		found_values=true        
	} 
    
	if (!found_values) {
		traceEvent(settings.logFilter,"produceSummaryReport>found no values for report,exiting",settings.trace)
		sendEvent(name: "summaryReport", value: "")
		return        
	}    
	String roomName =device.currentValue("where_name")
	String timePeriod="In the past ${pastDaysCount} days"
	def struct_HomeAwayMode= device.currentValue("st_away")
	if (pastDaysCount <2) {
		timePeriod="In the past day"    
	}    
	String stName=device.currentValue("st_name")    
	String summary_report = "At your home, your ${stName} structure is currently in ${struct_HomeAwayMode} mode." 
	summary_report=summary_report + "${timePeriod}"    
	if (roomName) {	
		summary_report= summary_report + ",in the room ${roomName} where the Nest cam ${device.displayName} is located"
	} else {
		summary_report= summary_report + ",at the Nest Cam ${device.displayName}"
	}    
	if (countEvents) {
		summary_report= summary_report + ",there were $countEvents total event(s), which include the following" 
	}
	if (countPersonEvents) {
		summary_report= summary_report + ",$countPersonEvents person event(s)" 
	}
	if (countMotionEvents) {
		summary_report= summary_report + ",$countMotionEvents motion event(s)" 
	}
	if (countSoundEvents) {
		summary_report= summary_report + ",$countSoundEvents sound event(s)" 
	}
	if ((min_cam_timestamp != max_cam_timestamp)) {
		def timeInLocalTime= formatTimeInTimezone(min_cam_timestamp)					    
		summary_report= summary_report + ".The earliest event was on ${timeInLocalTime.substring(0,16)}" 
	}
	if ((min_cam_timestamp != max_cam_timestamp)) {
		def timeInLocalTime= formatTimeInTimezone(max_cam_timestamp)					    
		summary_report= summary_report + ".The latest event was on ${timeInLocalTime.substring(0,16)}" 
	}
	if (last_event_start_time) {
		summary_report= summary_report + ".The last event started at ${last_event_start_time}, and ended at ${last_event_end_time}" 
		if (last_event_motion.toString()=='true') {
        	summary_report= summary_report + ".During the last event, motion was detected" 
		}    
		if (last_event_person.toString()=='true') {
        	summary_report= summary_report + ",and a person was detected" 
		}    
		if (last_event_sound.toString()=='true') {
        	summary_report= summary_report + ",and sound as well." 
		}    
	}
	sendEvent(name: "summaryReport", value: summary_report, isStateChange: true)
    
	traceEvent(settings.logFilter,"produceSummaryReport>end",settings.trace, get_LOG_TRACE())

}


private boolean isOnline() {
	return (device.currentValue("is_online").toString()=='true')
}

	
private boolean isStreaming() {
	return (device.currentValue("is_streaming").toString()=='true')
}

private boolean isVideoHistoryEnabled() {
	return (device.currentValue("is_video_history_enabled").toString()=='true')
}

private boolean isPublicSharedEnabled() {
	return (device.currentValue("is_public_share_enabled").toString()=='true')
}


private def getCamSerial(publicSharedId) {
	def cameraId=determine_camera_id("")
    
	boolean isStreaming=isStreaming()
	try {
		if (publicSharedId) {
			if (!isStreaming) {
				traceEvent("getCamSerial: Your Camera's currently not streaming or the code is unable to get the required public shared ID. Please to activate streaming and public sharing under your Cameras settings in the Nest Mobile App or at the portal", settings.trace)
				return null
			} else if ((isStreaming) && ((state?.lastPublicURLId == null) || (state?.camSerial == null) || (state?.lastPublicURLId != publicSharedId))) {
				def params = [
					uri: "https://video.nest.com/live/${publicSharedId}",
					requestContentType: 'text/html'
				]
				asynchttp_v1.get('getCameraSerialAsync', params)
			} else {
				traceEvent(settings.logFilter,"getCamSerial>state?.camSerial=${state?.camSerial}", settings.trace)        
				return state?.camSerial
			}
		} else { 
			traceEvent(settings.logFilter,"getCamSerial> your Camera's public share id was not found. Please make sure you have public video sharing enabled under your Cameras settings in the Nest Mobile App or at the portal...", settings.trace) 
   		}
	} catch (e) {
		traceEvent(settings.logFilter, "getCamSerial>Exception: $e", settings.trace)
	}
}

def getCameraSerialAsync(response, data) {
	settings.trace=true
	settings.logFilter=4
	String search_tag='<meta itemprop="image" content='
	String search_uuid='?uuid='    
	String search_uuid_2='&uuid='    

	traceEvent(settings.logFilter,"getCameraSerialAsync> ${response?.status}", settings.trace)
	if (response.hasError()) {
			traceEvent(settings.logFilter,"getCameraSerialAsync>not able to get Camera Serial :${response.status}", settings.trace, get_LOG_WARN())
	} else {
		def responseValues = response?.data
		traceEvent(settings.logFilter,"getCameraSerialAsync>getting camera serial: ${response.status}, data=$responseValues",settings.trace)    
		def pos_begin_url = find_delimited_substring(responseValues, search_tag)
		if (pos_begin_url == -1) {
			traceEvent(settings.logFilter, "getCameraSerialAsync>url not found based on $search_tag", settings.trace, get_LOG_ERROR())
			return            
		}        
	  	int pos_extract_url= pos_begin_url + search_tag.length()        
		def pos_end_url = find_delimited_substring(responseValues, '>', pos_extract_url)
		def url= responseValues.substring(pos_extract_url,pos_end_url)         
		traceEvent(settings.logFilter,"getCameraSerialAsync>url: $url", settings.trace)
		int pos_begin_serial = find_delimited_substring(url, search_uuid)  
		pos_begin_serial= (pos_begin_serial!= -1) ? pos_begin_serial : find_delimited_substring(url, search_uuid_2)          
		if (pos_begin_serial == -1) {
			traceEvent(settings.logFilter, "getCameraSerialAsync>serial not found based on $search_uuid & $search_uuid_2", settings.trace, get_LOG_ERROR())
			return            
		}        
		int pos_extract_serial=pos_begin_serial + search_uuid.length()        
		int pos_end_serial = find_delimited_substring(url,'&', pos_extract_serial)        
		def serial = url.substring(pos_extract_serial, pos_end_serial)
		traceEvent(settings.logFilter, "getCameraSerialAsync>serial: ${serial}, pos_begin_serial=$pos_begin_serial, pos_end_serial=$pos_end_serial", settings.trace)
		state?.camSerial = serial
	}
}
private int find_delimited_substring(String stringVar, String substrVar, int posOrigin=0) {
	int position=stringVar.indexOf(substrVar, posOrigin)
	position= (position != -1) ? position : stringVar.length()
	return position
}


private def getPublicShareURL() {
	def id = null
	def cameraId=device.currentValue("cameraId")
	def public_share_url=device.currentValue("public_share_url")    
	if ((!state?.publicURLId) && (public_share_url)) {
		id = public_share_url.tokenize('/').last().toString()
		state?.publicURLId = id
		traceEvent (settings.logFilter,"getPublicShareURL>id=$id,state?.publicURLId=${state?.publicURLId}", settings.trace)        
	} else {
		id = state?.publicURLId
	}
	traceEvent (settings.logFilter,"getPublicShareURL>end state?.publicURLId=${state?.publicURLId}", settings.trace)       
	return id
}


private def getPictureName() {
	def pictureUuid = java.util.UUID.randomUUID().toString().replaceAll('-', '')
    
	return pictureUuid
}



def initializeStreamingContext(url) {
	traceEvent(settings.logFilter,"initializeStreamingContext>begin with $url", settings.trace)
	if (url) {
		def publicSharedURLId = getPublicShareURL()
		def lastPublicSharedId = state?.lastPublicURLId
		traceEvent(settings.logFilter,"initializeStreamingContext> Url: $url,publicSharedURLId: $publicSharedURLId,lastPublicSharedId: $lastPublicSharedId" +
			",camSerial: ${state?.camSerial}", settings.trace)
		if( (lastPublicSharedId == null ) ||  ((lastPublicSharedId) && (lastPublicSharedId?.toString() != publicSharedURLId?.toString()))) {
			state?.lastPublicURLId = publicSharedURLId
		}
		if (!state?.camSerial) {
			getCamSerial(publicSharedURLId)
		} else {
			getDropcamServerData(state?.camSerial)
		}
	} else {
		traceEvent(settings.logFilter,"initializeStreamingContext>Url: $url,publicSharedURLId: ${state.publicSharedURLId},lastPublicSharedURLId: ${state.lastPubVidId}, camSerial: ${state?.camSerial}" +
			"dropcamServerData ${data?.dropcamServerData}", settings.trace)

		if ((state?.publicURLId) || (state?.lastPublicURLId) || (state?.camSerial) || (data?.dropcamServerData)) {
			state?.publicURLId = null
			state?.lastPublicURLId = null
			state?.camSerial = null
			data?.dropcamServerData = null
			traceEvent(settings.logFilter,"initializeStreamingContext>>reset streaming context because sharing has been disabled or the public share url is no longer available...", settings.trace, get_LOG_WARN())
		}
	}
}

private boolean checkIfStreaming(isStreaming) {
	traceEvent(settings.logFilter,"checkIfStreaming ($isStreaming)...", settings.trace)
	def lastStreamingValue = device.currentValue("isStreaming")
	def isOnline = device.currentValue("is_online")
	if ((data?.dropcamServerData) && (data?.dropcamServerData?.items?.is_streaming)) { 
 		isStreaming = data?.dropcamServerData?.items?.is_streaming[0] 
	} else {
		traceEvent(settings.logFilter,"checkIfStreaming>no streaming, as value of data?.dropcamServerData?.items=${data?.dropcamServerData?.items}",settings.trace)    
	}    
	def currentStreamingValue = (isStreaming.toString() == "true") ? "on" : (isOnline.toString() != "true" ? "offline" : "off")
	if (isStateChange(device, "isStreaming",  currentStreamingValue)) {
		traceEvent(settings.logFilter,"checkIfStreaming>updated new camera live value is: ${currentStreamingValue},last value: ${lastStreamingValue}", settings.trace)
		sendEvent(name: "isStreaming", value: currentStreamingValue, displayed:(settings.trace?:false))
		sendEvent(name: "switch", value: (currentStreamingValue == "on" ? currentStreamingValue : "off"), displayed:(settings.trace?:false))
		return true        
	} else { 
		traceEvent(settings.logFilter,"checkIfStreaming>No change, Camera Live Video Streaming is: ${currentStreamingValue}",settings.trace) 
		return false        
 	}        
}

private def getDropcamServerData(camSerial) {
	try {
		if (camSerial) {
			def params = [
				uri: "https://www.dropcam.com/api/v1/cameras.get?id=${camSerial}"
			]
			httpGet(params)  { resp ->

				traceEvent(settings.logFilter, "getDropcamServerData>resp: (status: ${resp?.status}), data: ${resp?.data}", settings.trace)
				if (resp?.status==200) {				
					if ((resp?.data) && (resp?.data != data?.dropcamServerData)) {
						data?.dropcamServerData= resp?.data
					}                         
					return resp?.data ?: null
				}                    
			}
		} else { 
			traceEvent(settings.logFilter,"getDropcamServerData camSerial is missing....", settings.trace, get_LOG_ERROR()) 	
		}
	}
	catch (e) {
		traceEvent(settings.logFilter,"getDropcamServerData Exception $e", settings.trace, get_LOG_ERROR())
	}
}




void on() {
	setStreaming('on')
	sendEvent(name: "switch", value: "on", displayed: true, isStateChange:true)
}

void off() {
	setStreaming('off')
	sendEvent(name: "switch", value: "off", displayed: true, isStateChange:true)
}

private def getLiveStreamHost() {
	if (!data?.dropcamServerData) { 
		return null 
	}
	def host = data?.dropcamServerData?.items?.live_stream_host
	traceEvent(settings.logFilter, "getLiveStreamHost: $data", settings.trace)
	def data = host?.toString()?.replaceAll("\\[|\\]", "")
	traceEvent(settings.logFilter, "getLiveStreamHost: $data after replaceAll", settings.trace)
	return (data ?: null)
}

def getDropcamPlaylistURL() {
	def DROPCAM_PLAYLIST="playlist.m3u8"
	def hostUrl = getLiveStreamHost()
	if (hostUrl && state?.camSerial) { 
		return "https://${hostUrl}/nexus_aac/${state?.camSerial}/${DROPCAM_PLAYLIST}" 
	}
	return null
}
void start() {

	def url = getDropcamPlaylistURL().toString()

//	def url=device.currentValue("public_share_url")
	traceEvent(settings.logFilter,"start>live video url=$url", settings.trace)
	def dataLiveVideo = [
		OutHomeURL: url,
		InHomeURL: url,
		ThumbnailURL: "http://cdn.device-icons.smartthings.com/camera/dlink-indoor@2x.png",
		cookie: [key: "key", value: "value"]
	]

	def event = [
		name: "stream",
		value: groovy.json.JsonOutput.toJson(dataLiveVideo).toString(),
		data: groovy.json.JsonOutput.toJson(dataLiveVideo),
		descriptionText: "Starting the livestream",
		eventType: "VIDEO",
		displayed: false,
		isStateChange: true
	]
	sendEvent(event)
	traceEvent(settings.logFilter,"start>end", settings.trace)
}


void installed() {
	def HEALTH_TIMEOUT= (60 * 60)
	sendEvent(name: "checkInterval", value: HEALTH_TIMEOUT, data: [protocol: "cloud", displayed:(settings.trace?:false)])
	state?.scale=getTemperatureScale() 
	if (settings.trace) { 
		log.debug("installed>$device.displayName installed with settings: ${settings.inspect()} and state variables= ${state.inspect()}")
	}
	state?.redirectURL=null
	state?.retriesCounter=0        
	state?.redirectURLcount=0            
    
}  

/* Ping is used by Device-Watch in attempt to reach the device
*/
def ping() {
	poll()
}

def updated() {
	def HEALTH_TIMEOUT= (60 * 60)
	sendEvent(name: "checkInterval", value: HEALTH_TIMEOUT, data: [protocol: "cloud", displayed:(settings.trace?:false)])
	state?.scale=getTemperatureScale() 
	state?.retriesCounter=0       
	state?.redirectURLcount=0            
	state?.redirectURL=null
	state?.scale=getTemperatureScale() 
	traceEvent(settings.logFilter,"updated>$device.displayName updated with settings: ${settings.inspect()} and state variables= ${state.inspect()}", settings.trace)
      
}


//remove from the selected devices list in Service Manager
void uninstalled() {
	traceEvent(settings.logFilter, "executing uninstalled for ${this.device.displayName}", settings.trace)
	parent.purgeChildDevice(this)    
}


void stop() {
	traceEvent(settings.logFilter,"stop", settings.trace)
}

void vidOn() {
	traceEvent(settings.logFilter,"on", settings.trace)
	// no-op
}

void vidOff() {
	traceEvent(settings.logFilter,"off", settings.trace)
	// no-op
}
//  structureId 
void setStructure(structureId,attributes = []) {
	def NEST_SUCCESS=200
	def TOKEN_EXPIRED=401    
	def REDIRECT_ERROR=307    
	def BLOCKED=429
	def interval=1*60    
	
   	structureId= determine_structure_id(structureId) 	    
	if (state?.lastStatusCodeForStructure==BLOCKED) {    
		def poll_interval=1  // set a minimum of 1 min interval to avoid unecessary load on Nest servers
		def time_check_for_poll = (now() - (poll_interval * 60 * 1000))
		if ((state?.lastPollTimestamp) && (state?.lastPollTimestamp > time_check_for_poll)) {
			traceEvent(settings.logFilter,"setStructure>structureId = ${structureId},time_check_for_poll (${time_check_for_poll} < state.lastPollTimestamp (${state.lastPollTimestamp}), throttling in progress, command not being processed",
				settings.trace, get_LOG_ERROR())            
			return
		}
	}
	traceEvent(settings.logFilter,"setStructure>called with values ${attributes} for ${structureId}",settings.trace)
	if (attributes == null || attributes == "" || attributes == [] ) {
		traceEvent(settings.logFilter, "setStructure>attribute set is empty, exiting", settings.trace)
		return        
	}
	def bodyReq =new groovy.json.JsonBuilder(attributes) 
	int statusCode
	def exceptionCheck  
	api('setStructure', structureId, bodyReq) {resp ->
		statusCode = resp?.status
		state?.lastStatusCodeForStructure=statusCode				                
		if (statusCode== REDIRECT_ERROR) {
			if (!process_redirectURL( resp?.headers.Location)) {
				traceEvent(settings.logFilter,"setStructure>Nest redirect: too many redirects, count =${state?.redirectURLcount}", true, get_LOG_ERROR())
				return                
			}
			traceEvent(settings.logFilter,"setStructure>Nest redirect: about to call setStructure again with args=$bodyReq, count =${state?.redirectURLcount}", true)
			doRequest( resp?.headers.Location, bodyReq, 'put') {redirectResp->
				statusCode=redirectResp?.status            
			}            
		}		    
		if (statusCode==BLOCKED) {
			traceEvent(settings.logFilter,"setStructure>structureId=${structureId},Nest throttling in progress,error $statusCode, retries={state?.retriesStructureCounter}", settings.trace, get_LOG_ERROR())
			interval=1 * 60   // set a minimum of 1min. interval to avoid unecessary load on Nest servers
		}			
		if (statusCode==TOKEN_EXPIRED) {
			traceEvent(settings.logFilter,"setStructure>error $statusCode, need to re-login at Nest",true, get_LOG_WARN())
			return            
		}
		exceptionCheck=device.currentValue("verboseTrace")
		if (statusCode == NEST_SUCCESS) {
			/* when success, reset the exception counter */
			state.exceptionCount=0
			state?.redirectURLcount=0  
			if ((data?."replayStructureId${state?.retriesStructureCounter}" == null) ||
				(state?.retriesStructureCounter > get_MAX_SETTER_RETRIES())) {          // reset the counter if last entry is null
				reset_replay_data('Structure')                
				state?.retriesStructureCounter=0
			}            
			traceEvent(settings.logFilter,"setStructure>done for ${structureId}", settings.trace)
			runIn(1*60, "refresh_structure_async", [overwrite:true])				                
		} else {
			traceEvent(settings.logFilter,"setStructure> error=${statusCode.toString()} for ${structureId}", true, get_LOG_ERROR())
		} /* end if statusCode */
	} /* end api call */                
	if (exceptionCheck?.contains("exception")) {
		sendEvent(name: "verboseTrace", value: "", displayed:(settings.trace?:false)) // reset verboseTrace            
		traceEvent(settings.logFilter,"setStructure>exception=${exceptionCheck}", true, get_LOG_ERROR())
	}                
	if ((statusCode == BLOCKED) ||
		(exceptionCheck?.contains("ConnectTimeoutException"))) {
		state?.retriesStructureCounter=(state?.retriesStructureCounter?:0)+1        
		if (!(interval=get_exception_interval_delay( state?.retriesStructureCounter))) {  
			traceEvent(settings.logFilter,"setStructure>too many retries", true, get_LOG_ERROR())
			reset_replay_data('Structure')                
			state?.retriesStructureCounter=0            
			return    
		}            
		state.lastPollTimestamp = (statusCode==BLOCKED) ? (now() + (interval * 1000)):(now() + (1 * 60 * 1000)) 
		data?."replayStructureAttributes${state?.retriesStructureCounter}"=attributes
		data?."replayStructureId${state?.retriesStructureCounter}"=structureId        
		traceEvent(settings.logFilter,"setStructure>about to call setStructureReplay,retries counter=${state?.retriesStructureCounter}", true, get_LOG_INFO())
		runIn(interval, "setStructureReplay", [overwrite:true])              
	}    
}

void setStructureReplay() {
	def exceptionCheck=""

	for (int i=1; (i<= get_MAX_SETTER_RETRIES()); i++) {
		def structureId = data?."replayStructureId$i"
		if (structureId == null) continue  // already processed        
		def attributes = data?."replayStructureAttributes$i"
		def poll_interval=1 
		state?.lastPollTimestamp= (now() - (poll_interval * 60 * 1000)) // reset the lastPollTimeStamp to pass through
		traceEvent(settings.logFilter,"setStructureReplay>about to call setStructure,retries counter=$i", true, get_LOG_INFO())
		setStructure(structureId,attributes) 
		exceptionCheck=device.currentValue("verboseTrace")
		if (exceptionCheck?.contains("done")) {
			data?."replayStructureId$i"=null        
		} /* end if */
	} /* end for */
	if (exceptionCheck?.contains("done")) { // if last command executed OK, then reset the counter
		state?.retriesStructureCounter=0
		reset_replay_data('Structure')                
	} 
    
}    
    
    


def getStructure(id,forceUpdate=false,postData=false) {
	def results=null
	if (!id) {
		id= device.currentValue("structureId")    
		if (!id) {
			traceEvent(settings.logFilter,"getStructure>id is null,exiting", settings.trace)
			return null        
		}        
	}    
	if (data?.structures) {    
		traceEvent(settings.logFilter,"name=${data?.structures[0]?.name}, structureId=${data?.structures[0]?.structure_id}, data?.structures=${data?.structures}", settings.trace)
		results = data?.structures?.find{it.structure_id==id}
	}        
	if ((!results) || (forceUpdate)) { // get the object from parent

		parent.getObject("structures",id, "", true)    	
		parent.updateStructures(this)  
		if (data?.structures) {        
			results = data?.structures?.find{it.structure_id==id}
		}            
	}
	if ((results) && (postData)) {

		def structureDataJson = new groovy.json.JsonBuilder(results)
		/*	
		traceEvent(settings.logFilter,"getStructure>structureDataJson=${structureDataJson}", settings.trace)
		*/
		def structureEvents = [
				structureData: "${structureDataJson.toString()}"
			]
		/*    
		traceEvent(settings.logFilter,"getStructure>structureEvents to be sent= ${structureEvents}", settings.trace)
		*/
		generateEvent(structureEvents)
    
	}    
	traceEvent(settings.logFilter,"getStructure>results =${results} for id=$id", settings.trace)
	return results    
}

void refresh_structure_async() {
	getStructure("",true) 	// force update of the local cache            
}

void updateStructures(structures) {
	traceEvent(settings.logFilter,"updateStructures>structures from parent=$structures",settings.trace,get_LOG_TRACE())        
	if (!data?.structures) {
		data?.structures=[:]    
	}    
	data?.structures=structures
	traceEvent(settings.logFilter,"updateStructures>data.structures=${data.structures}",settings.trace,get_LOG_TRACE())        
}


 //
//	if no cameraId is provided, it is defaulted to the current cameraId 
void getCameraInfo(cameraId) {
	def NEST_SUCCESS=200
	def TOKEN_EXPIRED=401 
	def REDIRECT_ERROR=307    
	def BLOCKED=429
	def interval=1*60    
    
	if (!data?.cameras) {
		data?.cameras=[:]
	}        
	if (state?.lastStatusCode==BLOCKED) {    
		def poll_interval=1  // set a minimum of 1 min interval to avoid unecessary load on Nest servers
		def time_check_for_poll = (now() - (poll_interval * 60 * 1000))
		if ((state?.lastPollTimestamp) && (state?.lastPollTimestamp > time_check_for_poll)) {
			traceEvent(settings.logFilter,"getCameraInfo>cameraId = ${cameraId},time_check_for_poll (${time_check_for_poll} < state.lastPollTimestamp (${state.lastPollTimestamp}), throttling in progress, command not being processed",
				settings.trace, get_LOG_ERROR())            
			return
		}
	}
	traceEvent(settings.logFilter,"getCameraInfo> about to call api with cameraId = ${cameraId}...",settings.trace)
	int statusCode
	def exceptionCheck
	api('cameraInfo', cameraId, "") {resp ->
		statusCode = resp?.status
		state?.lastStatusCode=statusCode				                
		if (statusCode== REDIRECT_ERROR) {
			if (!process_redirectURL( resp?.headers.Location)) {
				traceEvent(settings.logFilter,"getCameraInfo>Nest redirect: too many redirects, count =${state?.redirectURLcount}", true, get_LOG_ERROR())
				return                
			}
			traceEvent(settings.logFilter,"getCameraInfo>Nest redirect: about to call getCameraInfo again, count =${state?.redirectURLcount}")
			getCameraInfo(cameraId)    
			return                
		}		    
		if (statusCode==BLOCKED) {
			traceEvent(settings.logFilter,"getCameraInfo>cameraId=${cameraId},Nest throttling in progress,error $statusCode", settings.trace, get_LOG_ERROR())
			interval=1 * 60   // set a minimum of 1min. interval to avoid unecessary load on Nest servers
		}
		if (statusCode==TOKEN_EXPIRED) {
			traceEvent(settings.logFilter,"getCameraInfo>cameraId=${cameraId},error $statusCode, need to re-login at Nest", settings.trace, get_LOG_WARN())
			return            
		}
		exceptionCheck=device.currentValue("verboseTrace")
		if (statusCode==NEST_SUCCESS) {
			state?.redirectURLcount=0  
			/* when success, reset the exception counter */
			state.exceptionCount=0
			state?.redirectURLcount=0  
			state?.retriesCounter=0            
			if (resp.data instanceof Object[]) {
				data?.cameras=resp.data
			} else {
				data?.cameras?."$cameraId"=resp.data
			}			        
			def camName = data?.cameras?."$cameraId".name           
			def	locale=data?.cameras?."$cameraId".locale
			def where_id=data?.cameras?."$cameraId".where_id
			def	label=data?.cameras?."$cameraId".label
			def is_streaming=data?.cameras?."$cameraId".is_streaming.toString()
			def is_online=data?.cameras?."$cameraId".is_online.toString()
			def last_event_start_time=data?.cameras?."$cameraId"?.last_event?.start_time
			def last_event_end_time=data?.cameras?."$cameraId"?.last_event?.end_time
			traceEvent(settings.logFilter,"getCameraInfo> cameraId=${cameraId}, camName=$camName, last_event_start_time=$last_event_start_time,last_event_end_time=$last_event_end_time"  +
				" locale=$locale, where_id=$where_id, label=$label,is_online=$is_online,is_streaming=$is_streaming",     
				settings.trace)                        
			traceEvent(settings.logFilter,"getCameraInfo>done for ${cameraId}",settings.trace)
		} else {
			traceEvent(settings.logFilter,"getCameraInfo>error=${statusCode} for ${cameraId}",true, get_LOG_ERROR())			
		} /* end if statusCode */                 
	} /* end api call */
	if (exceptionCheck?.contains("exception")) {
		sendEvent(name: "verboseTrace", value: "", displayed:(settings.trace?:false)) // reset verboseTrace            
		traceEvent(settings.logFilter,"getCameraInfo> exception=${exceptionCheck}",true, get_LOG_ERROR())
	}                
	if ((statusCode == BLOCKED) ||
		(exceptionCheck?.contains("ConnectTimeoutException"))) {
		state?.retriesCounter=(state?.retriesCounter?:0)+1            
		if (!(interval=get_exception_interval_delay( state?.retriesCounter, "GETTER"))) {   
			traceEvent(settings.logFilter,"getCameraInfo>too many retries", true, get_LOG_ERROR())
			state?.retriesCounter=0            
			return        
		}        
		state.lastPollTimestamp = (now() + (interval * 1000))
		data?.replayId=cameraId
		runIn(interval, "getCameraInfoReplay", [overwrite:true])              
	}    
}


void getCameraInfoReplay() {
	def id = data?.replayId
	def poll_interval=1 
	state?.lastPollTimestamp= (now() - (poll_interval * 60 * 1000)) // reset the lastPollTimeStamp to pass through
	traceEvent(settings.logFilter,"getCameraInfoReplay>about to call getCameraInfo for ${id}",settings.trace, get_LOG_INFO())
	getCameraInfo(id) 
}    
 

// cameraId may be a list of serial# separated by ",", no spaces (ex. '123456789012,123456789013') 
//	if no cameraId is provided, it is defaulted to the current cameraId 
// settings can be anything supported by Nest at https://developers.nest.com/documentation/cloud/api-Camera
void setCameraSettings(cameraId,cameraSettings = []) {
	def NEST_SUCCESS=200    
	def TOKEN_EXPIRED=401    
	def REDIRECT_ERROR=307    
	def BLOCKED=429
	
	def interval=1 * 60
    
   	cameraId= determine_camera_id(cameraId) 	    
	if (state?.lastStatusCodeForSettings==BLOCKED) {    
		def poll_interval=1  // set a minimum of 1 min interval to avoid unecessary load on Nest servers
		def time_check_for_poll = (now() - (poll_interval * 60 * 1000))
		if ((state?.lastPollTimestamp) && (state?.lastPollTimestamp > time_check_for_poll)) {
			traceEvent(settings.logFilter,"setCameraSettings>cameraId = ${cameraId},time_check_for_poll (${time_check_for_poll} < state.lastPollTimestamp (${state.lastPollTimestamp}), throttling in progress, command not being processed",
				settings.trace, get_LOG_ERROR())            
			return
		}
	}
	traceEvent(settings.logFilter,"setCameraSettings>called with values ${cameraSettings} for ${cameraId}",settings.trace)
	if (cameraSettings == null || cameraSettings == "" || cameraSettings == [] ) {
		traceEvent(settings.logFilter, "setCameraSettings>cameraSettings set is empty, exiting", settings.trace)
		return        
	}
	def bodyReq =new groovy.json.JsonBuilder(cameraSettings) 
	int statusCode
	def exceptionCheck    
	api('setCameraSettings', cameraId, bodyReq) {resp ->
		statusCode = resp?.status
		state?.lastStatusCodeForSettings=statusCode				                
		if (statusCode== REDIRECT_ERROR) {
			if (!process_redirectURL( resp?.headers.Location)) {
				traceEvent(settings.logFilter,"setCameraSettings>Nest redirect: too many redirects, count =${state?.redirectURLcount}", true, get_LOG_ERROR())
				return                
			}
			traceEvent(settings.logFilter,"setCameraSettings>Nest redirect: about to call setCameraSettings again, count =${state?.redirectURLcount}", true)
			doRequest( resp?.headers.Location, bodyReq, 'put') {redirectResp->
				statusCode=redirectResp?.status            
			}            
			return                
		}		    
		if (statusCode==BLOCKED) {
			traceEvent(settings.logFilter,"setCameraSettings>cameraId=${cameraId},Nest throttling in progress, error=$statusCode", settings.trace, get_LOG_ERROR())
			interval=1 * 60   // set a minimum of 1min. interval to avoid unecessary load on Nest servers
		}			
		if (statusCode==TOKEN_EXPIRED) {
			traceEvent(settings.logFilter,"setCameraSettings>cameraId=${cameraId},error $statusCode, need to re-login at Nest", settings.trace, get_LOG_WARN())
			return            
		}
		exceptionCheck=device.currentValue("verboseTrace")
		if (statusCode == NEST_SUCCESS) {
			/* when success, reset the exception counter */
			state.exceptionCount=0
			state?.redirectURLcount=0   
			if ((data?."replaySettingsId${state?.retriesSettingsCounter}" == null) ||
				(state?.retriesSettingsCounter > get_MAX_SETTER_RETRIES())) {          // reset the counter if last entry is null
				state?.retriesSettingsCounter=0
				reset_replay_data('Settings')                
			}            
			traceEvent(settings.logFilter,"setCameraSettings>done for ${cameraId}", settings.trace)
		} else {
			traceEvent(settings.logFilter,"setCameraSettings> error=${statusCode.toString()} for ${cameraId}", true, get_LOG_ERROR())
		} /* end if statusCode */
	} /* end api call */                
	if (exceptionCheck?.contains("exception")) {
		sendEvent(name: "verboseTrace", value: "", displayed:(settings.trace?:false)) // reset verboseTrace            
		traceEvent(settings.logFilter,"setCameraSettings>exception=${exceptionCheck}", true, get_LOG_ERROR())
	}                
	if ((statusCode == BLOCKED) ||
		(exceptionCheck?.contains("ConnectTimeoutException"))) {
		state?.retriesSettingsCounter=(state?.retriesSettingsCounter?:0)+1            
		interval=1*60 * state?.retriesSettingsCounter // the interval delay will increase if multiple retries have already been made
		if (!(interval= get_exception_interval_delay(state?.retriesSettingsCounter))) {   
			traceEvent(settings.logFilter,"setCameraSettings>too many retries", true, get_LOG_ERROR())
			reset_replay_data('Settings')                
			return        
		}        
		state.lastPollTimestamp = (statusCode==BLOCKED) ? (now() + (interval * 1000)):(now() + (1 * 60 * 1000)) 
		data?."replaySettingsId${state?.retriesSettingsCounter}"=cameraId
		data?."replaySettings${state?.retriesSettingsCounter}"=cameraSettings    
		traceEvent(settings.logFilter,"setCameraSettings>about to call setCameraSettingsReplay,retries counter=${state?.retriesSettingsCounter}", true, get_LOG_INFO())
		runIn(interval, "setCameraSettingsReplay", [overwrite: true])          
	}    
    
}

void setCameraSettingsReplay() {
	def exceptionCheck=""

	for (int i=1; (i<= get_MAX_SETTER_RETRIES()); i++) {
		def cameraId = data?."replaySettingsId$i"
		if (id == null) continue  // already processed        
		def cameraSettings = data?."replaySettings$i"
		def poll_interval=1 
		state?.lastPollTimestamp= (now() - (poll_interval * 60 * 1000)) // reset the lastPollTimeStamp to pass through
		setCameraSettings(cameraId,cameraSettings) 
		exceptionCheck=device.currentValue("verboseTrace")
		if (exceptionCheck?.contains("done")) {
			data?."replaySettingsId$i"=null        
		} /* end if */
	} /* end for */
	if (exceptionCheck?.contains("done")) { // if last command executed OK, then reset the counter
		state?.retriesSettingsCounter=0
		reset_replay_data('Settings')                
	}     
}    
    


void getCameraList() {
	def NEST_SUCCESS=200
	def TOKEN_EXPIRED=401    
	def REDIRECT_ERROR=307
	def BLOCKED=429
	def cameraList=""
	def interval=1*60    
    
	def structureId = determine_structure_id("")
	if (state?.lastStatusCode==BLOCKED) {    
		def poll_interval=1  // set a minimum of 1 min interval to avoid unecessary load on Nest servers
		def time_check_for_poll = (now() - (poll_interval * 60 * 1000))
		if ((state?.lastPollTimestamp) && (state?.lastPollTimestamp > time_check_for_poll)) {
			traceEvent(settings.logFilter,"getCameraList>structureId = ${structureId},time_check_for_poll (${time_check_for_poll} < state.lastPollTimestamp (${state.lastPollTimestamp}), throttling in progress, command not being processed",
				settings.trace, get_LOG_ERROR())            
			return
		}
	}
	traceEvent(settings.logFilter,"getCameraList> about to call api with body = ${bodyReq}",settings.trace)
	int statusCode
	def exceptionCheck
	api('cameraList', structureId, "") {resp ->
		statusCode = resp?.status
		state?.lastStatusCode=statusCode				                
		if (statusCode== REDIRECT_ERROR) {
			if (!process_redirectURL( resp?.headers.Location)) {
				traceEvent(settings.logFilter,"getCameraList>Nest redirect: too many redirects, count =${state?.redirectURLcount}", true, get_LOG_ERROR())
				return                
			}
			traceEvent(settings.logFilter,"getCameraList>Nest redirect: about to call getCameraList again, count =${state?.redirectURLcount}")
			getCameraList()  
			return                
		}		    
		if (statusCode==BLOCKED) {
			traceEvent(settings.logFilter,"setCameraSettings>cameraId=${cameraId},Nest throttling in progress,error $statusCode", settings.trace, get_LOG_ERROR())
			interval=1 * 60   // set a minimum of 1min. interval to avoid unecessary load on Nest servers
		}			
		if (statusCode==TOKEN_EXPIRED) {
			traceEvent(settings.logFilter,"getCameraList>error $statusCode, need to re-login at Nest",true, get_LOG_WARN())
			return            
		}
		exceptionCheck=device.currentValue("verboseTrace")
		if (statusCode == NEST_SUCCESS) {
			/* when success, reset the exception counter */
			state.exceptionCount=0
			state?.redirectURLcount=0  
			state?.retriesCounter=0           
			data?.camerasList = resp.data
			data?.cameraCount = (data?.camerasList) ? data?.camerasList.size() :0
			for (i in 0..data.cameraCount - 1) {
				traceEvent(settings.logFilter,"getCameraList>found cameraId=${data?.camerasList[i]}",settings.trace)
				cameraList = cameraList + data?.camerasList[i] + ','
			} /* end for */                        
			sendEvent(name:"camerasList", value: cameraList, displayed: (settings.trace?:false))    
			traceEvent(settings.logFilter,"getCameraList>done",settings.trace)
		} else {
			traceEvent(settings.logFilter,"getCameraList> error= ${statusCode.toString()}",true, get_LOG_ERROR())
		} /* end if statusCode */
	}  /* end api call */              
			            
	if (exceptionCheck?.contains("exception")) {
		sendEvent(name: "verboseTrace", value: "", displayed:(settings.trace?:false)) // reset verboseTrace            
		traceEvent(settings.logFilter,"getCameraList>exception=${exceptionCheck}",true, get_LOG_ERROR())
	}                
	if ((statusCode == BLOCKED) ||
		(exceptionCheck?.contains("ConnectTimeoutException"))) {
		state?.retriesCounter=(state?.retriesCounter?:0)+1        
		if (!(interval=get_exception_interval_delay( state?.retriesCounter, "GETTER"))) {    
			traceEvent(settings.logFilter,"getCameraList>too many retries", true, get_LOG_ERROR())
			state?.retriesCounter=0            
			return        
		}        
		state.lastPollTimestamp = (now() + (interval * 1000))
		traceEvent(settings.logFilter,"getCameraList>about to call getCameraListReplay,retries counter=${state.retriesCounter}", true, get_LOG_INFO())
		runIn(interval, "getCameraListReplay",[overwrite:true])        
	}    
}


void getCameraListReplay() {
	def poll_interval=1 
	state?.lastPollTimestamp= (now() - (poll_interval * 60 * 1000)) // reset the lastPollTimeStamp to pass through
	traceEvent(settings.logFilter,"getCameraListReplay>about to recall getCameraList,retries counter=${state.retriesCounter}",
		true, get_LOG_INFO())
	getCameraList()
}    

private void reset_replay_data(replayBuffer) { 
	for (int i=1; (i<= get_MAX_SETTER_RETRIES()); i++) 
		data?."replay${replayBuffer}Id${i}"= null
}

private int get_exception_interval_delay(counter,method="SETTER") {

	int interval
    
	interval=1*60 * (counter as int) // the interval delay will increase if multiple retries have already been made

	int max_retries=(method=="SETTER")? get_MAX_SETTER_RETRIES() :get_MAX_GETTER_RETRIES()
	if (counter > max_retries) {
		traceEvent(settings.logFilter,"get_new_interval_delay>error max retries ($max_retries), counter=${counter}, exiting", true, get_LOG_WARN())
		return 0
	}        
	if (counter>=5 && method=="SETTER") {
		interval=(get_RETRY_DELAY_FACTOR() *  counter * 60)  // increase delay even more when number of retries >5            
	}            
	if (counter>=7 && method=="SETTER") {
		interval=interval + (get_RETRY_DELAY_FACTOR() * counter * 60)  // increase delay even more when number of retries >7           
	}            
	return interval    
}


private def process_redirectURL(redirect) {

	if (!redirect) {
		return true        
	}    
	def redirectURL= redirect.minus('https://')
	redirectURL= 'https://' + redirectURL.substring(0,(redirectURL?.indexOf('/',0)))             
	traceEvent(settings.logFilter,"process_redirectURL>orignal redirection= ${redirect}, redirectURL=$redirectURL")           
	state?.redirectURLcount=(state?.redirectURLcount?:0)+1            
	save_redirectURL(redirectURL)    
	if (state?.redirectURLcount > get_MAX_REDIRECT()) {
		return false    
	}		        
	return true    
}



private boolean refresh_tokens() {
	// Nest logic for refresh_tokens not available for the moment.
	return false
}

private void save_data_auth(auth) {

	data?.auth?.access_token = auth.access_token 
//	data?.auth?.refresh_token = auth.refresh_token
	data?.auth?.expires_in = auth.expires_in
	data?.auth?.token_type =   "Bearer" 
//	data?.auth?.token_type =   auth.token_type 
//	data?.auth?.scope = auth?.scope
	data?.auth?.authexptime = auth.authexptime
	traceEvent(settings.logFilter,"save_data_auth>saved data.auth=$data.auth")
}

private void save_redirectURL(redirectURL) {
	state?.redirectURL=redirectURL  // save the redirect location for further call purposes
	traceEvent(settings.logFilter,"save_redirectURL>${state?.redirectURL}",settings.trace)  
}


private def isLoggedIn() {
	if (data?.auth?.access_token == null) {
		traceEvent(settings.logFilter,"isLoggedIn> no data auth", settings.trace,get_LOG_TRACE())
		return false
	} 
	return true
}

private def isTokenExpired() {
	def buffer_time_expiration=10  // set a 10 min. buffer time 
	def time_check_for_exp = now() + (buffer_time_expiration * 60 * 1000)
/*    
	if (!data?.auth?.authexptime) {
		login()    
	}    
*/    
	double authExpTimeInMin= ((data?.auth?.authexptime - time_check_for_exp)/60000).toDouble().round(0)  
	traceEvent(settings.logFilter,"isTokenExpired>expiresIn timestamp: ${data.auth.authexptime} > timestamp check for exp: ${time_check_for_exp}?",settings.trace)
	traceEvent(settings.logFilter,"isTokenExpired>expires in ${authExpTimeInMin.intValue()} minutes",settings.trace)
	traceEvent(settings.logFilter,"isTokenExpired>data.auth= $data.auth",settings.trace)
	if (authExpTimeInMin <0) {
//		traceEvent(settings.logFilter,"isTokenExpired>auth token buffer time  expired (${buffer_time_expiration} min.), countdown is ${authExpTimeInMin.intValue()} minutes, need to refresh tokens now!",
//			settings.trace, get_LOG_WARN())        
	}    
	if (authExpTimeInMin < (0-buffer_time_expiration)) {
//		traceEvent(settings.logFilter,"isTokenExpired>refreshing tokens is more at risk (${authExpTimeInMin} min.),exception count may increase if tokens not refreshed!", settings.trace, get_LOG_WARN())
	}    
	if (data.auth.authexptime > time_check_for_exp) {
//		traceEvent(settings.logFilter,"isTokenExpired> not expired...", settings.trace)
		return false
	}
//	traceEvent(settings.logFilter,"isTokenExpired> expired...", settings.trace)
	return true
}

// Determine id from settings or initalSetup
private def determine_structure_id(structure_id) {
	def structureId = device.currentValue("structureId") 

	if ((structure_id != null) && (structure_id != "")) {
		structureId = structure_id
	} else if ((settings.structureId != null) && (settings.structureId != "")) {
		structureId = settings.structureId.trim()
		traceEvent(settings.logFilter,"determine_structure_id>structureId from settings= ${settings.structureId}", settings.trace)
	} else if ((settings.structureId == null) || (settings.structureId == "")) {
		settings?.structureId = structureId
		traceEvent(settings.logFilter,"determine_structure_id>structureId from device= ${structureId}", settings.trace)
	}
	if ((structure_id != "") && (structure_id != structureId)) {
		sendEvent(name: "structureId", displayed: (settings.trace?: false), value: structureId)
	}
	return structureId
}


// Determine id from settings or initalSetup
private def determine_camera_id(camera_id) {
	def cameraId=device.currentValue("cameraId")
    
	if ((camera_id != null) && (camera_id != "")) {
		cameraId = camera_id
	} else if ((settings.cameraId != null) && (settings.cameraId  != "")) {
		cameraId = settings.cameraId.trim()
		traceEvent(settings.logFilter,"determine_camera_id>cameraId = ${settings.cameraId}", settings.trace)
	} else if (data?.auth?.cameraId) {
		settings.appKey = data.auth.appKey
		settings.cameraId = data.auth.cameraId
		cameraId=data.auth.cameraId
		traceEvent(settings.logFilter,"determine_camera_id>cameraId from data.auth= ${data.auth.cameraId}",settings.trace)
	} else if ((cameraId !=null) && (cameraId != "")) {
		settings.cameraId = cameraId
		traceEvent(settings.logFilter,"determine_camera_id> cameraId from device= ${cameraId}",settings.trace)
	}
    
	if ((camera_id != "") && (cameraId && camera_id != cameraId)) {
		sendEvent(name: "cameraId", displayed: (settings.trace?:false),value: cameradId)    
	}
	return cameraId
}

// Get the appKey for authentication
private def get_appKey() {
	return data?.auth?.appKey    
    
}    

// @Get the privateKey for authentication
private def get_privateKey() {
	
	return data?.auth?.privateKey
}    
   

private def get_API_VERSION() {
	return "1"
}

private def get_API_URI_ROOT() {
	def root
	if (state?.redirectURL) {
		root=state?.redirectURL     
	} else {
		root="https://developer-api.nest.com"
	}
	return root
}

// Maximum URL redirect
private def  get_MAX_REDIRECT() {
	return 10
}

private def get_MAX_ERROR_WITH_REDIRECT_URL() {
	return 15
}


// Maximum number of command retries for setters
private def get_MAX_SETTER_RETRIES() {
	return 10
}


// Maximum number of command retries for getters
private def get_MAX_GETTER_RETRIES() {
	return 2
}

private def get_RETRY_DELAY_FACTOR() {
	return 3.1
}


private def get_MAX_TIPS() {
	return 5
}

private def getCustomImagePath() {
	return "https://raw.githubusercontent.com/yracine/device-type-myNext/master/icons/"
}    


private def formatDate(dateString) {
	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm zzz")
	Date aDate = sdf.parse(dateString)
	return aDate
}


private def formatTimeInTimezone(dateTime, timezone='') {
	def myTimezone=(timezone)?TimeZone.getTimeZone(timezone):location.timeZone 
	String dateInLocalTime =new Date(dateTime).format("yyyy-MM-dd HH:mm:ss zzz", myTimezone)
	return dateInLocalTime
}


private String formatDateInLocalTime(dateInString, timezone='') {
	def myTimezone=(timezone)?TimeZone.getTimeZone(timezone):location.timeZone 
	if ((dateInString==null) || (dateInString.trim()=="")) {
		return (new Date().format("yyyy-MM-dd HH:mm:ss", myTimezone))
	}    
	SimpleDateFormat ISODateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'")
	Date ISODate = ISODateFormat.parse(dateInString.substring(0,19) + 'Z')
	String dateInLocalTime =new Date(ISODate.getTime()).format("yyyy-MM-dd HH:mm:ss zzz", myTimezone)
//	log.debug("formatDateInLocalTime>dateInString=$dateInString, dateInLocalTime=$dateInLocalTime")    
	return dateInLocalTime
}


// Called by MyNextServiceMgr for initial creation of a child Device
void initialSetup(device_client_id, private_key_id,auth_data, structure_id, device_camera_id) {
	settings.trace=true
	settings?.logFilter=5
    
	traceEvent(settings.logFilter,"initialSetup>begin",settings.trace)
	log.debug "initialSetup>begin"
	log.debug "initialSetup> structure_id = ${structure_id}"
	log.debug "initialSetup> device_camera_id = ${device_camera_id}"
	log.debug "initialSetup> device_camera_id = ${device_client_id}"
	log.debug "initialSetup> private_key_id = ${private_key_id}"

	traceEvent(settings.logFilter,"initialSetup> structure_id = ${structure_id}",settings.trace)
	traceEvent(settings.logFilter,"initialSetup> device_camera_id = ${device_camera_id}",settings.trace)
	traceEvent(settings.logFilter,"initialSetup> device_client_id = ${device_client_id}",settings.trace)
	traceEvent(settings.logFilter,"initialSetup> private_key_id = ${private_key_id}",settings.trace)
	settings?.structureId = structure_id
	settings?.cameraId = device_camera_id
	sendEvent(name: "cameraId", value:device_camera_id,  displayed: (settings.trace?: false))    
	sendEvent(name: "structureId", value: structure_id,  displayed: (settings.trace?: false))
	data?.auth=settings
	data?.auth?.appKey=device_client_id
	data?.auth?.privateKey=private_key_id
    
	save_data_auth(auth_data)    
	log.debug "initialSetup> settings = $settings"
	log.debug "initialSetup> data_auth = $data.auth"
	log.debug "initialSetup>end"
	traceEvent(settings.logFilter,"initialSetup> settings = $settings",settings.trace)
	traceEvent(settings.logFilter,"initialSetup> data_auth = $data.auth",settings.trace)
	traceEvent(settings.logFilter,"initialSetup>end",settings.trace)

	runIn(1*60,"refresh_camera")
	state?.exceptionCount=0    
	state?.scale = getTemperatureScale()
    
}


private int get_LOG_ERROR() {return 1}
private int get_LOG_WARN()  {return 2}
private int get_LOG_INFO()  {return 3}
private int get_LOG_DEBUG() {return 4}
private int get_LOG_TRACE() {return 5}

def traceEvent(logFilter,message, displayEvent=false, traceLevel=4, sendMessage=true) {
	int LOG_ERROR= get_LOG_ERROR()
	int LOG_WARN=  get_LOG_WARN()
	int LOG_INFO=  get_LOG_INFO()
	int LOG_DEBUG= get_LOG_DEBUG()
	int LOG_TRACE= get_LOG_TRACE()
	int filterLevel=(logFilter)?logFilter.toInteger():get_LOG_WARN()

	if ((displayEvent) || (sendMessage)) {
		def results = [
			name: "verboseTrace",
			value: message,
			displayed: ((displayEvent)?: false)
		]	

		if ((displayEvent) && (filterLevel >= traceLevel)) {
			switch (traceLevel) {
				case LOG_ERROR:
					log.error "${message}"
				break
				case LOG_WARN:
					log.warn "${message}"
				break
				case LOG_INFO:
					log.info  "${message}"
				break
				case LOG_TRACE:
					log.trace "${message}"
				break
				case LOG_DEBUG:
				default:
					log.debug "${message}"
				break
			}  /* end switch*/              
		} /* end if displayEvent*/
		if (sendMessage) sendEvent (results)
	}
}