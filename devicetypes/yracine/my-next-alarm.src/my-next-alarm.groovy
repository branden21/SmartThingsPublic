/***
 *  My Next Alarm
 *  Copyright 2018 Yves Racine
 *  LinkedIn profile: ca.linkedin.com/pub/yves-racine-m-sc-a/0/406/4b/
 *  Version 1.1.5
 *  Refer to readme file for installation instructions.
 
 *  Developer retains all right, title, copyright, and interest, including all copyright, patent rights,
 *  trade secret in the Background technology. May be subject to consulting fees under an Agreement 
 *  between the Developer and the Customer. Developer grants a non exclusive perpetual license to use
 *  the Background technology in the Software developed for and delivered to Customer under this
 *  Agreement. However, the Customer shall make no commercial use of the Background technology without
 *  Developer's written consent.
 *
 *  Unless required by applicable law or agreed to in writing, software distributed under the License is distributed
 *  on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. 
 *  
 *  Software Distribution is restricted and shall be done only with Developer's written approval.
 */
import java.text.SimpleDateFormat

include 'asynchttp_v1'

// for the UI

preferences {

//	Preferences are no longer required when created with the Service Manager (MyNextServiceMgr).

	input("structureId", "text", title: "StructureId", description:
		"The structureId of your location\n(not needed when using MyNextServiceMgr, leave it blank)")
	input("protectId", "text", title: "internal Id", description:
		"The internal protectId\n(not needed when using MyNextServiceMgr, leave it blank)")
	input("trace", "bool", title: "trace", description:
		"Set it to true to enable tracing (no spaces) or leave it empty (no tracing)")
	input("logFilter", "number",title: "(1=ERROR only,2=<1+WARNING>,3=<2+INFO>,4=<3+DEBUG>,5=<4+TRACE>)",  range: "1..5",
 		description: "optional" )        
}
metadata {
	definition (name: "My Next Alarm", namespace: "yracine", mnmn: "SmartThings", vid: "generic-smoke-co", ocfDeviceType: "x.com.st.d.sensor.smoke", author: "yracine") {
		capability "Smoke Detector"
		capability "Carbon Monoxide Detector"
		capability "Sensor"
		capability "Battery"  // Not present as a percentage
		capability "Health Check"
		capability "Polling"
		capability "Refresh"
		capability "Presence Sensor"

		command "getStructure"        
		command "setStructure"        
		command "setStructureHome"
		command "setStructureAway"
		command "away"
		command "home"
		command "present"
		command "getProtectInfo"        
		command "getProtectList"
		command "setProtectSettings"        
		command "produceSummaryReport"        
/*
		strcuture attributes 
*/        

		attribute "st_away","string"
		attribute "st_name","string"
		attribute "st_country_code","string"
		attribute "st_postal_code","string"
		attribute "st_peak_period_start_time","string"
		attribute "st_peak_period_end_time","string"
		attribute "st_time_zone","string"
		attribute "st_eta_trip_id","string"
		attribute "st_estimated_arrival_window_begin","string"
		attribute "st_estimated_arrival_window_end","string"
		attribute "st_eta_begin","string"
		attribute "st_wwn_security_state","string"        

		attribute "structure_id","string"
		attribute "structureData", "string"        
		attribute "protectsList","string" 
		attribute "protectId","string"
		attribute "NestAlarmState", "string"
		attribute "alarmState", "string"
		attribute "locale", "string"
		attribute "battery_state","string"        
		attribute "software_version","string"
		attribute "where_id","string"
		attribute "where_name","string"
		attribute "label","string"
		attribute "name_long","string"
		attribute "is_online","string"
		attribute "onlineState","string"
		attribute "last_connection","string"
		attribute "last_api_check","string"
		attribute "ui_color_state","string"
		attribute "co_alarm_state","string"
		attribute "smoke_alarm_state","string"
		attribute "last_manual_test_time","string"
		attribute "is_manual_test_active","string"
		attribute "verboseTrace", "string"
		attribute "summaryReport", "string"
	}


	tiles (scale: 2){
		multiAttributeTile(name:"smoke", type: "generic", width: 6, height: 4) {
			tileAttribute ("device.alarmState", key: "PRIMARY_CONTROL", backgroundColor:getBackgroundColors()) {
				attributeState("clear", label:"clear", icon:"st.alarm.smoke.clear", backgroundColor:"#44b621",defaultState: true)  
				attributeState("smoke", label:"SMOKE", icon:"st.alarm.smoke.smoke", backgroundColor:"#e86d13")
				attributeState("carbonMonoxide", label:"MONOXIDE", icon:"st.alarm.carbon-monoxide.carbon-monoxide", backgroundColor:"#e86d13")
				attributeState("tested", label:"TEST", icon:"st.alarm.smoke.test", backgroundColor:"#e86d13")
			}
			tileAttribute("device.onlineState", key: "SECONDARY_CONTROL") {
				attributeState("default", label:'${currentValue}')
			}
            
		}
		standardTile("refresh", "device.battery_state", inactiveLabel: false, canChangeIcon: false,
			decoration: "flat",width: 2, height: 2) {
			state "default", label: 'Refresh',action: "refresh", icon:"st.secondary.refresh", 			
			backgroundColor: "#ffffff"
		}
		standardTile("NestAlarmState", "device.NestAlarmState", inactiveLabel: false, canChangeIcon: false,
			decoration: "flat",width: 2, height: 2) {        
			state("clear", label:"clear", backgroundColor:"#44b621", icon:"st.alarm.smoke.clear")
			state("warning_smoke", label:  "Smoke\nWARNING", backgroundColor:"#f1d801",icon:"st.alarm.smoke.smoke")
			state("emergency_smoke", label:"Smoke\nURGENT", backgroundColor:"#bc2323",icon:"st.alarm.smoke.smoke")
			state("warning_co", label:  "CO\nWARNING", backgroundColor:"#f1d801",icon:"st.alarm.carbon-monoxide.carbon-monoxide")
			state("emergency_co", label:"CO\nURGENT", backgroundColor:"#bc2323", icon:"st.alarm.carbon-monoxide.carbon-monoxide")
			backgroundColor: "#ffffff"
		}
		valueTile("battery", "device.battery_state", inactiveLabel: false, decoration: "flat", width: 2, height: 2) {
			state "battery", label:'Battery ${currentValue}'
		}
		valueTile(	"lastManualTest", "device.last_manual_test_time",width: 2, height: 2,canChangeIcon: false,decoration: "flat") {
				state("default",
				label:'Manual Test ${currentValue}',
				backgroundColor: "#ffffff",
			)
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
  		valueTile(	"swVersion", "device.software_version",width: 2, height: 2,canChangeIcon: false,decoration: "flat") {
			state("default",
				label:'swVersion ${currentValue}',
				backgroundColor: "#ffffff",
			)
		}
	
		main "smoke"
		details(["smoke",
			"NestAlarmState",        
			"battery",            
			"lastManualTest",
			"lastConnection",
			"lastAPICheck",
			"swVersion",            
			"refresh"
			])
	}
}
def getBackgroundColors() {
	if (data?.protects?."$protectId"?.ui_color_state) {
		return data?.protects?."$protectId"?.ui_color_state    
	} else {
		return "#ffffff"    
	}    
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

// handle commands



void away() {
	setStructureAway()
}
void present() {
	home()
}
void home() {
    
	setStructureHome()
    
}

// parse events into attributes
def parse(String description) {

}

// protectId		single protectId 
// asyncValues		values from async poll
// RTeventFlag		Indicates whether or not RT events were sent by Nest
private def refresh_protect(protectId="", asyncValues=null, RTEventFlag=false) {
	def todayDay = new Date().format("dd",location.timeZone)
	def structure
	protectId=determine_protect_id(protectId)    
	def scale = getTemperatureScale()
	state?.scale= scale    

	if (!data?.protects) {
		data?.protects=[:]
	}        
	if ((!state?.today) || (state?.today != todayDay))  {
		state?.today=todayDay        
	}  

	if ((!asyncValues) && (!RTEventFlag)) {
		getProtectInfo(protectId)
		String exceptionCheck = device.currentValue("verboseTrace")
		if ((exceptionCheck) && ((exceptionCheck?.contains("exception")) || (exceptionCheck?.contains("error")))) {
		// check if there is any exception or an error reported in the verboseTrace associated to the device 
			traceEvent(settings.logFilter, "poll>$exceptionCheck", true, get_LOG_ERROR()) 
			return    
		}    
	}  else if (asyncValues) { 
		if (asyncValues instanceof Object[]) { 
			data?.protects=asyncValues
		} else {
			data?.protects?."$protectId"=asyncValues
		}                
	}    
    
   
	def dataEvents = [
		protectId:  data?.protects?."$protectId"?.device_id,
 		protectName:data?.protects?."$protectId"?.name,
		alarmState:((data?.protects?."$protectId"?.smoke_alarm_state in ['warning','emergency']) ? 'smoke': 
			(data?.protects?."$protectId"?.co_alarm_state in ['warning','emergency'])? 'carbonMonoxide' : 'clear'),
		NestAlarmState:((data?.protects?."$protectId"?.smoke_alarm_state in ['warning','emergency']) ? data?.protects?."$protectId"?.smoke_alarm_state + '_smoke': 
			(data?.protects?."$protectId"?.co_alarm_state in ['warning','emergency'])? data?.protects?."$protectId"?.co_alarm_state + '_co' : 'clear'),
		onlineState:(data?.protects?."$protectId"?.is_online?.toString()=='true')?'Online' : 'Offline',
		"structure_id": data?.protects?."$protectId"?.structure_id,
		"battery_state":data?.protects?."$protectId"?.battery_health,        
		"is_manual_test_active": data?.protects?."$protectId"?.is_manual_test_active,
		"last_manual_test_time": (data?.protects?."$protectId"?.last_manual_test_time)? formatDateInLocalTime(data?.protects?."$protectId"?.last_manual_test_time)?.substring(0,16):"",
		"locale": data?.protects?."$protectId"?.locale,
		"software_version": data?.protects?."$protectId"?.software_version,
		"where_id": data?.protects?."$protectId"?.where_id,
		"where_name": data?.protects?."$protectId"?.where_name,
		"label": data?.protects?."$protectId"?.label,
		"name_long":data?.protects?."$protectId"?.name_long,
		"is_online": data?.protects?."$protectId"?.is_online,
		"last_connection": (data?.protects?."$protectId"?.last_connection)? formatDateInLocalTime(data?.protects?."$protectId"?.last_connection)?.substring(0,16):"",
		"last_api_check": formatTimeInTimezone(now())?.substring(0,16),
		"co_alarm_state": data?.protects?."$protectId"?.co_alarm_state,
		"smoke_alarm_state": data?.protects?."$protectId"?.smoke_alarm_state,
		"ui_color_state":data?.protects?."$protectId"?.ui_color_state
 	]
	if (dataEvents.alarmState=='clear' && dataEvents.is_manual_test_active.toString()=='true') {
		dataEvents.alarmState='tested'    
	}    
	generateEvent(dataEvents)    
	def structureId=determine_structure_id(dataEvents?.structure_id)
	traceEvent(settings.logFilter, "refresh_protect>about to call getStructure") 
	structure=getStructure(structureId,false)    
	if (structure) {
		traceEvent(settings.logFilter, "refresh_protect>structure name= $stucture?.name, values=$structure") 
		def list =""   
		structure?.smoke_co_alarms?.each {
			list=list + it + ','    
		}    
		dataEvents= [
			protectsList: list,      
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

	traceEvent(settings.logFilter,"refresh_protect>done for protectId =${protectId}", settings.trace)
    
}

// refresh() has a different polling interval as it is called by lastPollTimestampthe UI (contrary to poll).
void refresh() {
	def protectId= determine_protect_id("") 	    
	def poll_interval=1  // set a 1 min. poll interval to avoid unecessary load on Nest servers
	def time_check_for_poll = (now() - (poll_interval * 60 * 1000))
	if ((state?.lastPollTimestamp) && (state?.lastPollTimestamp > time_check_for_poll)) {
		traceEvent(settings.logFilter,"refresh>protectId = ${protectId},time_check_for_poll (${time_check_for_poll} < state.lastPollTimestamp (${state.lastPollTimestamp}), not refreshing data...",
			settings.trace)	            
		return
	}
	state.lastPollTimestamp = now()
	refresh_protect(protectId)
  
}


def getProtectInfoAsync(protectId) {
	String URI_ROOT = "${get_API_URI_ROOT()}"

	protectId=determine_protect_id(protectId)

	if (isTokenExpired()) {
//		traceEvent(settings.logFilter,"getProtectInfoAsync>need to refresh tokens", settings.trace, get_LOG_WARN())
       
		if (!refresh_tokens()) {
//			traceEvent(settings.logFilter,"getProtectInfoAsync>not able to renew the refresh token", settings.trace, get_LOG_WARN())         
		} else {
        
			// Reset Exceptions counter as the refresh_tokens() call has been successful 
			state?.exceptionCount=0
		}            
        
	}
	traceEvent(settings.logFilter,"getProtectInfoAsync>about to call pollAsyncResponse with protectId = ${protectId}...", settings.trace)
	def request = [
			id: "$protectId",
			method:"getProtectInfoAsync"            
		]
    
	def params = [
		uri: "${URI_ROOT}/devices/smoke_co_alarms/${protectId}",
		headers: [
			'Authorization': "${data.auth.token_type} ${data.auth.access_token}",
			'Content-Type':  "application/json",
			'charset': "UTF-8"
		]
	]
	asynchttp_v1.get("pollAsyncResponse",params, request)


}
void poll() {
   
	def protectId= determine_protect_id("") 	    

	def poll_interval=1   // set a minimum of 1min. poll interval to avoid unecessary load on Nest servers
	def time_check_for_poll = (now() - (poll_interval * 60 * 1000))
	if ((state?.lastPollTimestamp) && (state?.lastPollTimestamp > time_check_for_poll)) {
		traceEvent(settings.logFilter,"poll>protectId = ${protectId},time_check_for_poll (${time_check_for_poll} < state.lastPollTimestamp (${state.lastPollTimestamp}), not refreshing data...",
			settings.trace, get_LOG_INFO())            
		return
	}
	getProtectInfoAsync(protectId)    
	traceEvent(settings.logFilter,"poll>done for protectId =${protectId}", settings.trace)

}

def pollAsyncResponse(response, data) {	
	def TOKEN_EXPIRED=401
	def REDIRECT_ERROR=307    
	def BLOCKED=429    
    
	def protectId = data?.id
	def method=data?.method
    
	state?.lastStatusCode=response.status				                
	if (response.hasError()) {
    
		if (response.status== REDIRECT_ERROR) {
			if (!process_redirectURL( response?.headers.Location)) {
				traceEvent(settings.logFilter,"pollAsyncResponse>Nest redirect: too many redirects, count =${state?.redirectURLcount}", true, get_LOG_ERROR())
				return                
			}
			getProtectInfoAsync(protectId)           
		} else if (response.status ==BLOCKED) {
			state?.retriesCounter=(state?.retriesCounter?:0)+1                 
			traceEvent(settings.logFilter,"pollAsyncResponse>protectId=${protectId},Nest throttling in progress,error $response.status,retries counter=${state?.retriesCounter}", settings.trace, get_LOG_WARN())
			if ((!get_exception_interval_delay( state?.retriesCounter, "GETTER"))) {   
				traceEvent(settings.logFilter,"pollAsyncResponse>too many retries", true, get_LOG_ERROR())
				state?.retriesCounter=0            
				return        
			}        
			state.lastPollTimestamp = (now() + (1 * state?.retriesCounter * 60 * 1000))   // set a minimum of 1min. interval to avoid unecessary load on Nest servers
			runIn((state?.retriesCounter*60),"getProtectInfoAsync")
		} else if (response.status == TOKEN_EXPIRED) { // token is expired
			traceEvent(settings.logFilter,"pollAsyncResponse>Nest's Access token has expired for $data, need to re-login at Nest...", settings.trace, get_LOG_WARN())
		} else {         
			if (state?.redirectURL && state?.exceptionCount> get_MAX_ERROR_WITH_REDIRECT_URL()) {
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
			if (!data?.protects) {
				data?.protects=[:]
			}        
			data?.protects?."$protectId"=responseValues			            
			def protectName = data?.protects?."$protectId".name           
			def	locale=data?.protects?."$protectId".locale
			def where_id=data?.protects?."$protectId".where_id
			def	label=data?.protects?."$protectId".label
			def is_online=data?.protects?."$protectId".is_online
			def last_connection=data?.protects?."$protectId".last_connection
			def ui_color_state=data?.protects?."$protectId".ui_color_state
			def co_alarm_state=data?.protects?."$protectId".co_alarm_state
			traceEvent(settings.logFilter,"pollAsyncResponse> protectId=${protectId}, protectName=$protectName,co_alarm_state=$co_alarm_state,ui_color_state=$ui_color_state" +
				" locale=$locale, where_id=$where_id, label=$label,is_online=$is_online, last_connection=$last_connection",       
				settings.trace)                        
			refresh_protect(protectId, responseValues) 
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

// 			Temperature variable names for display contain 'display'            

			if (upperFieldName?.contains("DATA")) { // data variable names contain 'data'

				sendEvent(name: name, value: value, displayed: (settings.trace?:false))                                     									 

			} else if (value != null && value.toString() != 'null' && value.toString() != '[:]' && value.toString() != '[]') {           
				def isChange = isStateChange(device, name, value.toString())
				isDisplayed = isChange
				sendEvent(name: name, value: value.toString(), isStateChange: isChange, displayed: isDisplayed)       
			}
		}
	}
}


private def getTemperatureValue(value) {
	value = (value!=null)? value:0
	if (state?.scale == "C") {
		return fToC(value)
	} else {
		return value
	}
}


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
		'protectList': 
			[uri:"${URI_ROOT}/structures/$id/smoke_co_alarms", 
      			type:'get'],
		'protectInfo': 
			[uri:"${URI_ROOT}/devices/smoke_co_alarms/$id", 
          		type: 'get'],
		'setStructure':
			[uri: "${URI_ROOT}/structures/$id", type: 'put'],
		'setProtectSettings':
			[uri: "${URI_ROOT}/devices/smoke_co_alarms/$id", type: 'put']
		]
	def request = methods.getAt(method)
	if (request.type=="get" && args) {
		def args_encoded = java.net.URLEncoder.encode(args.toString(), "UTF-8")
		request.uri=request.uri + "?${args_encoded}"    
//		request.uri=request.uri + "?${args}"    
	}    
     
	traceEvent(settings.logFilter,"api> about to call doRequest with (unencoded) args = ${args}", settings.trace)
	doRequest(request.uri, args, request.type, success)
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

// Need to be authenticated in before this is called. So don't call this. Call api.
private void doRequest(uri, args, type, success) {
	def params = [
		uri: uri,
		headers: [
			'Authorization': "${data.auth.token_type} ${data.auth.access_token}",
			'Content-Type':  "application/json",
			'charset': "UTF-8"
		]
	]
	traceEvent(settings.logFilter,"doRequest>about to ${type} with uri ${params.uri}, (encoded)args= ${args}", settings.trace)
	try {
		traceEvent(settings.logFilter,"doRequest>about to ${type} with uri ${params.uri}, (encoded)args= ${args}",settings.trace)
		if (type == 'put') {
			params?.body = args
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
		state.exceptionCount = state.exceptionCount +1     
		if (state?.redirectURL && state?.exceptionCount>get_MAX_ERROR_WITH_REDIRECT_URL()) {
			save_redirectURL(null) // remove redirection as it's not working        
		}        
	} 
}

void produceSummaryReport(pastDaysCount) {
	traceEvent(settings.logFilter,"produceSummaryReport>begin",settings.trace, get_LOG_TRACE())
	def countEvents, countTested, countSmokeWarnings,countCoWarnings,countCoEmergencies,countSmokeEmergencies,countBatteryEvents
	boolean found_values=false
	Date todayDate = new Date()
	Date startOfPeriod = todayDate - pastDaysCount
	long min_timestamp,max_timestamp

	def events = device.statesSince("NestAlarmState", startOfPeriod, [max:200])
	def testEvents = device.statesSince("is_manual_test_active", startOfPeriod, [max:200])
	def batteryEvents = device.statesSince("battery_state", startOfPeriod, [max:200])
	def currentBatteryState= device.currentValue("battery_state")
	def event_with_min_timestamp, event_with_max_timestamp    
	if (events) {    
		countEvents =  events.count{it}
		countSmokeWarnings =  events.count{it?.value.toString()=='warning_smoke'}
		countCoWarnings =  events.count{it?.value.toString()=='warning_co'}
		countSmokeEmergencies =  events.count{it?.value.toString()=='emergency_smoke'}
		countCoEmergencies =  events.count{it?.value.toString()=='emergency_co'}
		event_with_min_timestamp=events.min{it?.date.getTime()}  		        
		event_with_max_timestamp=events.max{it?.date.getTime()}		        
		max_timestamp= (event_with_max_timestamp) ? event_with_max_timestamp.date.getTime() : null
		min_timestamp= (event_with_min_timestamp) ? event_with_min_timestamp.date.getTime() : null
        
		found_values=true        
	}
	if (testEvents) {    
		countTested =  testEvents.count{it?.value=='true'}
		found_values=true        
	}    
	if (batteryEvents) {
		countBatteryEvents = batteryEvents.count{it} 
		found_values=true        
	}    
    
 
	if (!found_values) {
		traceEvent(settings.logFilter,"produceSummaryReport>found no values for report,exiting",settings.trace)
		sendEvent(name: "summaryReport", value: "")
		return        
	}    
	String roomName =device.currentValue("where_name")
	String scale=getTemperatureScale(), unitScale='Farenheit',timePeriod="In the past ${pastDaysCount} days"
	def struct_HomeAwayMode= device.currentValue("st_away")
	if (scale=='C') { 
		unitScale='Celsius'    
	}    
	if (pastDaysCount <2) {
		timePeriod="In the past day"    
	}    
	String stName=device.currentValue("st_name")    
	String summary_report = "At your home, your ${stName} structure is currently in ${struct_HomeAwayMode} mode." 
	summary_report=summary_report + "${timePeriod}"    
	if (roomName) {	
		summary_report= summary_report + ",in the room ${roomName} where the Nest Protect ${device.displayName} is located"
	} else {
    
		summary_report= summary_report + ",at the Nest Protect ${device.displayName},"
	}    
	if (countEvents) {
		summary_report= summary_report + ",there were $countEvents event(s) triggered by the Nest Protect, which include the following" 
	}
	if (countTested) {
		summary_report= summary_report + ",$countTested test event(s) were recorded" 
	}
	if (countCoWarnings) {
		summary_report= summary_report + ",$countCoWarnings carbon monoxide warning event(s)" 
	}
	if (countCoEmergencies) {
		summary_report= summary_report + ",$countCoEmergencies carbon monoxide emergency event(s)" 
	}
	if (countSmokeWarnings) {
		summary_report= summary_report + ",$countSmokeWarnings smoke warning event(s)" 
	}
	if (countSmokeEmergencies) {
		summary_report= summary_report + ",$countSmokeEmergencies smoke emergency event(s)" 
	}
    
	if (countBatteryEvents) {
		summary_report= summary_report + ",$countBatteryEvents battery event(s).The current battery state is ${currentBatteryState}" 
	}
    
	if ((min_timestamp != max_timestamp)) {
		def timeInLocalTime= formatTimeInTimezone(min_timestamp)					    
		summary_report= summary_report + ".The Protect's earliest event recorded (${event_with_min_timestamp.value}) was on ${timeInLocalTime.substring(0,16)}" 
	}
	if ((min_timestamp != max_timestamp)) {
		def timeInLocalTime= formatTimeInTimezone(max_timestamp)					    
		summary_report= summary_report + ".The Protect's last event recorded (${event_with_max_timestamp.value}) was on ${timeInLocalTime.substring(0,16)}" 
	}
    

	sendEvent(name: "summaryReport", value: summary_report, isStateChange: true)
    
	traceEvent(settings.logFilter,"produceSummaryReport>end",settings.trace, get_LOG_TRACE())

}



void setStructureAway() {

	setStructure("", [away: "away"])
}

void setStructureHome() {

	setStructure("", [away: "home"])
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
			log.debug "setStructure>Nest redirect: about to call setStructure again with args=$bodyReq, count =${state?.redirectURLcount}"
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
		reset_replay_data('Structure')                
		state?.retriesStructureCounter=0
	} 
    
}    
    
//	@id				Id of the structure, by default the current one
//	@forceUpdate	forceUpdate of the local cache, by default false
//	@postData		flag used to post the corresponding room data as json, by default false

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

		parent.getObject("structures",id,"", true)    	
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
//	if no protectId is provided, it is defaulted to the current protectId 
void getProtectInfo(protectId) {
	def NEST_SUCCESS=200
	def TOKEN_EXPIRED=401 
	def REDIRECT_ERROR=307    
	def BLOCKED=429
	def interval=1*60    
    
	if (!data?.protects) {
		data?.protects=[:]
	}        
	if (state?.lastStatusCode==BLOCKED) {    
		def poll_interval=1  // set a minimum of 1 min interval to avoid unecessary load on Nest servers
		def time_check_for_poll = (now() - (poll_interval * 60 * 1000))
		if ((state?.lastPollTimestamp) && (state?.lastPollTimestamp > time_check_for_poll)) {
			traceEvent(settings.logFilter,"getProtectInfo>protectId = ${protectId},time_check_for_poll (${time_check_for_poll} < state.lastPollTimestamp (${state.lastPollTimestamp}), throttling in progress, command not being processed",
				settings.trace, get_LOG_ERROR())            
			return
		}
	}
	traceEvent(settings.logFilter,"getProtectInfo> about to call api with protectId = ${protectId}...",settings.trace)
	int statusCode
	def exceptionCheck
	api('protectInfo', protectId, "") {resp ->
		statusCode = resp?.status
		state?.lastStatusCode=statusCode				                
		if (statusCode== REDIRECT_ERROR) {
			if (!process_redirectURL( resp?.headers.Location)) {
				traceEvent(settings.logFilter,"getProtectInfo>Nest redirect: too many redirects, count =${state?.redirectURLcount}", true, get_LOG_ERROR())
				return                
			}
			traceEvent(settings.logFilter,"getProtectInfo>Nest redirect: about to call getProtectInfo again, count =${state?.redirectURLcount}")
			getProtectInfo(protectId)    
			return                
		}		    
		if (statusCode==BLOCKED) {
			traceEvent(settings.logFilter,"getProtectInfo>protectId=${protectId},Nest throttling in progress,error $statusCode", settings.trace, get_LOG_ERROR())
			interval=1 * 60   // set a minimum of 1min. interval to avoid unecessary load on Nest servers
		}
		if (statusCode==TOKEN_EXPIRED) {
			traceEvent(settings.logFilter,"getProtectInfo>protectId=${protectId},error $statusCode, need to re-login at Nest", settings.trace, get_LOG_WARN())
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
				data?.protects=resp.data
			} else {
				data?.protects?."$protectId"=resp.data
			}			        
			def protectName = data?.protects?."$protectId".name           
			def	locale=data?.protects?."$protectId".locale
			def where_id=data?.protects?."$protectId".where_id
			def	label=data?.protects?."$protectId".label
			def is_online=data?.protects?."$protectId".is_online
			def last_connection=data?.protects?."$protectId".last_connection
			def ui_color_state=data?.protects?."$protectId".ui_color_state
			def co_alarm_state=data?.protects?."$protectId".co_alarm_state
			traceEvent(settings.logFilter,"getProtectInfo> protectId=${protectId}, protectName=$protectName,co_alarm_state=$co_alarm_state,ui_color_state=$ui_color_state" +
				" locale=$locale, where_id=$where_id, label=$label,is_online=$is_online, last_connection=$last_connection",       
				settings.trace)                        
			traceEvent(settings.logFilter,"getProtectInfo>done for ${protectId}",settings.trace)
		} else {
			traceEvent(settings.logFilter,"getProtectInfo>error=${statusCode} for ${protectId}",true, get_LOG_ERROR())			
		} /* end if statusCode */                 
	} /* end api call */
	if (exceptionCheck?.contains("exception")) {
		sendEvent(name: "verboseTrace", value: "", displayed:(settings.trace?:false)) // reset verboseTrace            
		traceEvent(settings.logFilter,"getProtectInfo> exception=${exceptionCheck}",true, get_LOG_ERROR())
	}                
	if ((statusCode == BLOCKED) ||
		(exceptionCheck?.contains("ConnectTimeoutException"))) {
		state?.retriesCounter=(state?.retriesCounter?:0)+1            
		if (!(interval=get_exception_interval_delay( state?.retriesCounter, "GETTER"))) {   
			traceEvent(settings.logFilter,"getProtectInfo>too many retries", true, get_LOG_ERROR())
			state?.retriesCounter=0			            
			return        
		}        
		state.lastPollTimestamp = (now() + (interval * 1000))
		data?.replayId=protectId
		runIn(interval, "getProtectInfoReplay", [overwrite:true])              
	}    
}


void getProtectInfoReplay() {
	def id = data?.replayId
	def poll_interval=1 
	state?.lastPollTimestamp= (now() - (poll_interval * 60 * 1000)) // reset the lastPollTimeStamp to pass through
	traceEvent(settings.logFilter,"getProtectInfoReplay>about to call getProtectInfo for ${id}",settings.trace, get_LOG_INFO())
	getProtectInfo(id) 
}    
 

// protectId may be a list of serial# separated by ",", no spaces (ex. '123456789012,123456789013') 
//	if no protectId is provided, it is defaulted to the current protectId 
// settings can be anything supported by Nest at https://developers.nest.com/documentation/cloud/api-Protect
void setProtectSettings(protectId,protectSettings = []) {
	def TOKEN_EXPIRED=401    
	def REDIRECT_ERROR=307    
	def BLOCKED=429
	
	def interval=1 * 60
    
   	protectId= determine_protect_id(protectId) 	    
	if (state?.lastStatusCodeForSettings==BLOCKED) {    
		def poll_interval=1  // set a minimum of 1 min interval to avoid unecessary load on Nest servers
		def time_check_for_poll = (now() - (poll_interval * 60 * 1000))
		if ((state?.lastPollTimestamp) && (state?.lastPollTimestamp > time_check_for_poll)) {
			traceEvent(settings.logFilter,"setProtectSettings>protectId = ${protectId},time_check_for_poll (${time_check_for_poll} < state.lastPollTimestamp (${state.lastPollTimestamp}), throttling in progress, command not being processed",
				settings.trace, get_LOG_ERROR())            
			return
		}
	}
	traceEvent(settings.logFilter,"setProtectSettings>called with values ${tstatSettings} for ${protectId}",settings.trace)
	if (protectSettings == null || protectSettings == "" || protectSettings == [] ) {
		traceEvent(settings.logFilter, "setProtectSettings>protectSettings set is empty, exiting", settings.trace)
		return        
	}
	def bodyReq =new groovy.json.JsonBuilder(protectSettings) 
	int statusCode
	def exceptionCheck    
	api('setProtectSettings', protectId, bodyReq) {resp ->
		statusCode = resp?.status
		state?.lastStatusCodeForSettings=statusCode				                
		if (statusCode== REDIRECT_ERROR) {
			if (!process_redirectURL( resp?.headers.Location)) {
				traceEvent(settings.logFilter,"setStructure>Nest redirect: too many redirects, count =${state?.redirectURLcount}", true, get_LOG_ERROR())
				return                
			}
			traceEvent(settings.logFilter,"setProtectSettings>Nest redirect: about to call setProtectSettings again, count =${state?.redirectURLcount}", true)
			doRequest( resp?.headers.Location, bodyReq, 'put') {redirectResp->
				statusCode=redirectResp?.status            
			}            
			return                
		}		    
		if (statusCode==BLOCKED) {
			traceEvent(settings.logFilter,"setProtectSettings>protectId=${protectId},Nest throttling in progress, error=$statusCode", settings.trace, get_LOG_ERROR())
			interval=1 * 60   // set a minimum of 1min. interval to avoid unecessary load on Nest servers
		}			
		if (statusCode==TOKEN_EXPIRED) {
			traceEvent(settings.logFilter,"setProtectSettings>protectId=${protectId},error $statusCode, need to re-login at Nest", settings.trace, get_LOG_WARN())
			return            
		}
		exceptionCheck=device.currentValue("verboseTrace")
		if (statusCode == NEST_SUCCESS) {
			/* when success, reset the exception counter */
			state.exceptionCount=0
			state?.redirectURLcount=0   
			if ((data?."replaySettingsId${state?.retriesSettingsCounter}" == null) ||
				(state?.retriesSettingsCounter > get_MAX_SETTER_RETRIES())) {          // reset the counter if last entry is null
				reset_replay_data('Settings')                
				state?.retriesSettingsCounter=0
			}            
			traceEvent(settings.logFilter,"setProtectSettings>done for ${protectId}", settings.trace)
		} else {
			traceEvent(settings.logFilter,"setProtectSettings> error=${statusCode.toString()} for ${protectId}", true, get_LOG_ERROR())
		} /* end if statusCode */
	} /* end api call */                
	if (exceptionCheck?.contains("exception")) {
		sendEvent(name: "verboseTrace", value: "", displayed:(settings.trace?:false)) // reset verboseTrace            
		traceEvent(settings.logFilter,"setProtectSettings>exception=${exceptionCheck}", true, get_LOG_ERROR())
	}                
	if ((statusCode == BLOCKED) ||
		(exceptionCheck?.contains("ConnectTimeoutException"))) {
		state?.retriesSettingsCounter=(state?.retriesSettingsCounter?:0)+1            
		interval=1*60 * state?.retriesSettingsCounter // the interval delay will increase if multiple retries have already been made
		if (!(interval= get_exception_interval_delay(state?.retriesSettingsCounter))) {   
			traceEvent(settings.logFilter,"setProtectSettings>too many retries", true, get_LOG_ERROR())
			reset_replay_data('Settings')
			return        
		}        
		state.lastPollTimestamp = (statusCode==BLOCKED) ? (now() + (interval * 1000)):(now() + (1 * 60 * 1000)) 
		data?."replaySettingsId${state?.retriesSettingsCounter}"=protectId
		data?."replaySettings${state?.retriesSettingsCounter}"=protectSettings    
		traceEvent(settings.logFilter,"setProtectSettings>about to call setProtectSettingsReplay,retries counter=${state?.retriesSettingsCounter}", true, get_LOG_INFO())
		runIn(interval, "setProtectSettingsReplay", [overwrite: true])          
	}    
    
}

void setProtectSettingsReplay() {
	def exceptionCheck=""

	for (int i=1; (i<= get_MAX_SETTER_RETRIES()); i++) {
		def protectId = data?."replaySettingsId$i"
		if (id == null) continue  // already processed        
		def protectSettings = data?."replaySettings$i"
		def poll_interval=1 
		state?.lastPollTimestamp= (now() - (poll_interval * 60 * 1000)) // reset the lastPollTimeStamp to pass through
		setProtectSettings(protectId,protectSettings) 
		exceptionCheck=device.currentValue("verboseTrace")
		if (exceptionCheck?.contains("done")) {
			data?."replaySettingsId$i"=null        
		} /* end if */
	} /* end for */
	if (exceptionCheck?.contains("done")) { // if last command executed OK, then reset the counter
		reset_replay_data('Settings')                
		state?.retriesSettingsCounter=0
	}     
}    
    


void getProtectList() {
	def NEST_SUCCESS=200
	def TOKEN_EXPIRED=401    
	def REDIRECT_ERROR=307
	def BLOCKED=429
	def alarmsList=""
	def interval=1*60    
    
	def structureId = determine_structure_id("")
	if (state?.lastStatusCode==BLOCKED) {    
		def poll_interval=1  // set a minimum of 1 min interval to avoid unecessary load on Nest servers
		def time_check_for_poll = (now() - (poll_interval * 60 * 1000))
		if ((state?.lastPollTimestamp) && (state?.lastPollTimestamp > time_check_for_poll)) {
			traceEvent(settings.logFilter,"getProtectList>structureId = ${structureId},time_check_for_poll (${time_check_for_poll} < state.lastPollTimestamp (${state.lastPollTimestamp}), throttling in progress, command not being processed",
				settings.trace, get_LOG_ERROR())            
			return
		}
	}
	traceEvent(settings.logFilter,"getProtectList> about to call api with body = ${bodyReq}",settings.trace)
	int statusCode
	def exceptionCheck
	api('protectList', structureId, "") {resp ->
		statusCode = resp?.status
		state?.lastStatusCode=statusCode				                
		if (statusCode== REDIRECT_ERROR) {
			if (!process_redirectURL( resp?.headers.Location)) {
				traceEvent(settings.logFilter,"getProtectList>Nest redirect: too many redirects, count =${state?.redirectURLcount}", true, get_LOG_ERROR())
				return                
			}
			traceEvent(settings.logFilter,"getProtectList>Nest redirect: about to call getProtectList again, count =${state?.redirectURLcount}")
			getProtectList()  
			return                
		}		    
		if (statusCode==BLOCKED) {
			traceEvent(settings.logFilter,"setProtectSettings>protectId=${protectId},Nest throttling in progress,error $statusCode", settings.trace, get_LOG_ERROR())
			interval=1 * 60   // set a minimum of 1min. interval to avoid unecessary load on Nest servers
		}			
		if (statusCode==TOKEN_EXPIRED) {
			traceEvent(settings.logFilter,"getProtectList>error $statusCode, need to re-login at Nest",true, get_LOG_WARN())
			return            
		}
		exceptionCheck=device.currentValue("verboseTrace")
		if (statusCode == NEST_SUCCESS) {
			/* when success, reset the exception counter */
			state.exceptionCount=0
			state?.redirectURLcount=0  
			state?.retriesCounter=0           
			data?.protectsList = resp.data
			data?.protectCount = (data?.protectsList) ? data?.protectsList.size() :0
			for (i in 0..data.protectCount - 1) {
				traceEvent(settings.logFilter,"getProtectList>found protectId=${data?.protectsList[i]}",settings.trace)
				alarmsList = alarmsList + data?.protectsList[i] + ','
			} /* end for */                        
			sendEvent(name:"protectsList", value: alarmsList, displayed: (settings.trace?:false))    
			traceEvent(settings.logFilter,"getProtectList>done",settings.trace)
		} else {
			traceEvent(settings.logFilter,"getProtectList> error= ${statusCode.toString()}",true, get_LOG_ERROR())
		} /* end if statusCode */
	}  /* end api call */              
			            
	if (exceptionCheck?.contains("exception")) {
		sendEvent(name: "verboseTrace", value: "", displayed:(settings.trace?:false)) // reset verboseTrace            
		traceEvent(settings.logFilter,"getProtectList>exception=${exceptionCheck}",true, get_LOG_ERROR())
	}                
	if ((statusCode == BLOCKED) ||
		(exceptionCheck?.contains("ConnectTimeoutException"))) {
		state?.retriesCounter=(state?.retriesCounter?:0)+1        
		if (!(interval=get_exception_interval_delay( state?.retriesCounter, "GETTER"))) {    
			traceEvent(settings.logFilter,"getProtectList>too many retries", true, get_LOG_ERROR())
			state?.retriesCounter=0            
			return        
		}        
		state.lastPollTimestamp = (now() + (interval * 1000))
		traceEvent(settings.logFilter,"getProtectList>about to call getProtectListReplay,retries counter=${state.retriesCounter}", true, get_LOG_INFO())
		runIn(interval, "getProtectListReplay",[overwrite:true])        
	}    
}


void getProtectListReplay() {
	def poll_interval=1 
	state?.lastPollTimestamp= (now() - (poll_interval * 60 * 1000)) // reset the lastPollTimeStamp to pass through
	traceEvent(settings.logFilter,"getProtectListReplay>about to recall getProtectList,retries counter=${state.retriesCounter}",
		true, get_LOG_INFO())
	getProtectList()
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
private def determine_protect_id(protect_id) {
	def protectId=device.currentValue("protectId")
    
	if ((protect_id != null) && (protect_id != "")) {
		protectId = protect_id
	} else if ((settings.protectId != null) && (settings.protectId  != "")) {
		protectId = settings.protectId.trim()
		traceEvent(settings.logFilter,"determine_protect_id>protectId = ${settings.protectId}", settings.trace)
	} else if (data?.auth?.protectId) {
		settings.appKey = data.auth.appKey
		settings.protectId = data.auth.protectId
		protectId=data.auth.protectId
		traceEvent(settings.logFilter,"determine_protect_id>protectId from data.auth= ${data.auth.protectId}",settings.trace)
	} else if ((protectId !=null) && (protectId != "")) {
		settings.protectId = protectId
		traceEvent(settings.logFilter,"determine_protect_id> protectId from device= ${protectId}",settings.trace)
	}
    
	if ((protect_id != "") && (protectId && protect_id != protectId)) {
		sendEvent(name: "protectId", displayed: (settings.trace?:false),value: protectdId)    
	}
	return protectId
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
void initialSetup(device_client_id, private_key_id,auth_data, structure_id, device_protect_id) {
	settings.trace=true
	settings?.logFilter=5
    
	traceEvent(settings.logFilter,"initialSetup>begin",settings.trace)
	log.debug "initialSetup>begin"
	log.debug "initialSetup> structure_id = ${structure_id}"
	log.debug "initialSetup> device_protect_id = ${device_protect_id}"
	log.debug "initialSetup> device_client_id = ${device_client_id}"
	log.debug "initialSetup> private_key_id = ${private_key_id}"

	traceEvent(settings.logFilter,"initialSetup> structure_id = ${structure_id}",settings.trace)
	traceEvent(settings.logFilter,"initialSetup> device_protect_id = ${device_protect_id}",settings.trace)
	traceEvent(settings.logFilter,"initialSetup> device_client_id = ${device_client_id}",settings.trace)
	traceEvent(settings.logFilter,"initialSetup> private_key_id = ${private_key_id}",settings.trace)
	settings?.structureId = structure_id
	settings?.protectId = device_protect_id
	sendEvent(name: "protectId", value:device_protect_id,  displayed: (settings.trace?: false))    
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

	runIn(1*60,"refresh_protect")
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