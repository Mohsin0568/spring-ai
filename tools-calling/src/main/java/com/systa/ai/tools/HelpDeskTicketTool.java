package com.systa.ai.tools;

import com.systa.ai.domain.TicketRequest;
import com.systa.ai.entity.HelpDeskTicket;
import com.systa.ai.service.HelpDeskTicketService;
import lombok.AllArgsConstructor;
import org.springframework.ai.chat.model.ToolContext;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.logging.Logger;

@Component
public class HelpDeskTicketTool {

    private final HelpDeskTicketService helpDeskTicketService;

    public HelpDeskTicketTool(final HelpDeskTicketService helpDeskTicketService) {
        this.helpDeskTicketService = helpDeskTicketService;
    }

    private final Logger logger = Logger.getLogger(HelpDeskTicketTool.class.getName());

    @Tool(name="creteHelpDeskTicket", description = "Tool to create a help desk ticket with the provided issue description")
    public String createHelpDeskTicket(
            @ToolParam(description = "Creates a help desk ticket with the provided issue description") final TicketRequest ticketRequest,
            final ToolContext toolContext) {
        // Logic to create a help desk ticket using the provided issue description
        logger.info("Creating help desk ticket with issue description: " + ticketRequest.issueDescription());
        final String userName = (String) toolContext.getContext().get("userName");
        final HelpDeskTicket helpDeskTicket = helpDeskTicketService.createHelpDeskTicket(ticketRequest, "testUser"); // Replace "testUser" with actual user name
        return "Help desk ticket created with ID: " + helpDeskTicket.id() + " for user: " + userName;
    }

    @Tool(name = "getHelpDeskTicketsByUserName", description = "Tool to retrieve help desk tickets for a specific user")
    public List<HelpDeskTicket> getHelpDeskTicketsByUserName(
            final ToolContext toolContext) {
        // Logic to retrieve help desk tickets for a specific user
        final String userName = (String) toolContext.getContext().get("userName");
        logger.info("Retrieving help desk tickets for user: " + userName);
        return helpDeskTicketService.getTicketsByUserName(userName);
    }

}
