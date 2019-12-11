
// hyperexport.groovy v1.00 (11th December 2019)
//  Barfs sensor data out via HTTP. Yes, just HTTP.

definition(
    name: "Hyper Export",
    namespace: "andydvsn",
    author: "Andrew Davison",
    description: "Exports sensor data in a hyper way.",
    category: "Convenience",
    iconUrl: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience.png",
    iconX2Url: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience@2x.png",
    iconX3Url: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience@2x.png")

preferences {

	section ("Webhook") {
		input "url", "text", title: "HTTP URL"
		input "key", "text", title: "'x-api-key' header"
	}

	section("Accelerometers:") {
		input "accelerometers", "capability.accelerationSensor", multiple: true, required: false
	}

	section("Alarms:") {
		input "alarms", "capability.alarm", multiple: true, required: false
	}

	section("Carbon Monoxide Detectors:") {
		input "codetectors", "capability.carbonMonoxideDetector", multiple: true, required: false
	}

	section("Contact Sensors:") {
		input "contacts", "capability.contactSensor", multiple: true, required: false
	}

	section("Energy Meters:") {
		input "energymeters", "capability.energyMeter", multiple: true, required: false
	}

	section("Humidity Sensors:") {
		input "humidities", "capability.relativeHumidityMeasurement", multiple: true, required: false
	}

	section("Indicators:") {
		input "indicators", "capability.indicator", multiple: true, required: false
	}

	section("Motion Sensors:") {
		input "motions", "capability.motionSensor", multiple: true, required: false
	}

	section("Power Meters:") {
		input "powermeters", "capability.powerMeter", multiple: true, required: false
	}

	section("Presence Sensors:") {
		input "presences", "capability.presenceSensor", multiple: true, required: false
	}

	section("Smoke Detectors:") {
		input "smokedetectors", "capability.smokeDetector", multiple: true, required: false
	}

	section("Switches:") {
		input "switches", "capability.switch", multiple: true, required: false
	}

	section("Switch Levels:") {
		input "switchlevels", "capability.switchLevel", multiple: true, required: false
	}

	section("Temperature Sensors:") {
		input "temperatures", "capability.temperatureMeasurement", multiple: true, required: false
	}

	section("Water Detectors:") {
		input "waterdetectors", "capability.waterSensor", multiple: true, required: false
	}

}

def installed() {
	log.debug "Installed with settings: ${settings}"
	initialize()
}

def updated() {
	log.debug "Updated with settings: ${settings}"
	unsubscribe()
	initialize()
}

def initialize() {
	subscribeSensors()
}

def subscribeSensors() {
	subscribe(accelerometers, "acceleration", handler)
	subscribe(alarms, "alarm", handler)
	subscribe(codetectors, "carbonMonoxideDetector", handler)
	subscribe(contacts, "contact", handler)
	subscribe(energymeters, "energy", handler)
	subscribe(energymeters, "gasMeter", handler)
	subscribe(humidities, "humidity", handler)
	subscribe(indicators, "indicator", handler)
	subscribe(location, "location", handler)
	subscribe(modes, "locationMode", handler)
	subscribe(motions, "motion", handler)
	subscribe(powermeters, "power", handler)
	subscribe(presences, "presence", handler)
	subscribe(relays, "relaySwitch", handler)
	subscribe(smokedetectors, "smokeDetector", handler)
	subscribe(switches, "switch", handler)
	subscribe(switchlevels, "switchlevels", handler)
	subscribe(temperatures, "temperature", handler)
	subscribe(waterdetectors, "water", handler)
}

def jsonPost(evt) {

	def json = "{"
		json += "\"device\":\"${evt.device}\","
		json += "\"deviceId\":\"${evt.deviceId}\","
		json += "\"${evt.name}\":\"${evt.value}\","
		json += "\"location\":\"${evt.location}\","
		json += "\"source\":\"smartthings\""
		json += "}"
	log.debug("hyperexport: ${json}")

	def params = [
		uri: url,
		headers: [
			"x-api-key": key,
			"content-type": "application/json"
		],
		body: json
	]

	try {
		httpPostJson(params)
	} catch (groovyx.net.http.HttpResponseException ex) {
		if (ex.statusCode != 200) {
			log.error "hyperexport: POST error: ${ex} (${ex.statusCode})"
        }
	}

}

def handler(evt) {
    jsonPost(evt)
}
