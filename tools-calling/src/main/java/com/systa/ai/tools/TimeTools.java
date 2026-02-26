package com.systa.ai.tools;

import org.springframework.ai.tool.annotation.Tool;
import org.springframework.stereotype.Component;

import java.util.logging.Logger;

@Component
public class TimeTools {

    private final Logger logger = Logger.getLogger(TimeTools.class.getName());

    @Tool(name = "getLocalTime", description = "Get the local time based on user's timezone")
    public String getLocalTime(){
        logger.info("Getting local time based on user's timezone");
        return "The local time is: " + java.time.LocalTime.now().toString();
    }

    @Tool(name = "getLocalTimeByTimeZone", description = "Get the local time based on provided timezone")
    public String getLocalTimeByTimeZone(final String timeZone){
        logger.info("Getting local time based on provided timezone: " + timeZone);
        return java.time.ZonedDateTime.now(java.time.ZoneId.of(timeZone)).toLocalTime().toString();
    }
}
