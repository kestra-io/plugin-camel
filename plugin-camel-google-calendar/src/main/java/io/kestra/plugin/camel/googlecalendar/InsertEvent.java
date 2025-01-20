package io.kestra.plugin.camel.googlecalendar;

import org.apache.camel.CamelContext;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.google.calendar.GoogleCalendarComponent;
import org.apache.camel.component.google.calendar.GoogleCalendarConfiguration;
import org.apache.camel.impl.DefaultCamelContext;

import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.EventDateTime;
import com.google.api.services.calendar.model.Event.Creator;

import lombok.*;
import io.kestra.core.models.annotations.Example;
import io.kestra.core.models.annotations.Plugin;
import io.kestra.core.models.annotations.PluginProperty;
import io.kestra.core.models.tasks.RunnableTask;
import io.kestra.core.models.tasks.Task;
import io.kestra.core.runners.RunContext;
import lombok.experimental.SuperBuilder;
import io.swagger.v3.oas.annotations.media.Schema;

import org.slf4j.Logger;
import java.nio.file.Path;
import java.util.Map;
import java.util.HashMap;

@SuperBuilder
@ToString
@EqualsAndHashCode
@Getter
@NoArgsConstructor
@Schema(
    title = "Insert a Google Calendar event."
)
@Plugin(
    examples = {
        @Example(
            full = true,
            title = "Insert a Google Calendar event using clientId, clientSecret and refreshToken.",
            code = """
                   id: insert_google_calendar_event
                   namespace: company.team
                   
                   tasks:
                     - id: insert_event
                       type: io.kestra.plugin.camel.googlecalendar.InsertEvent
                       calendarId: my_calendar_id
                       clientId: oauth_client_id
                       clientSecret: oauth_client_secret
                       refreshToken: refresh_token
                       summary: "Sample Calendar Event"
                       description: "This is a sample calendar event generated from Kestra."
                       creator:
                         email: my_email@gmail.com
                       location: "Bengaluru, India"
                       startDateTime: "2024-12-01T10:00:00+05:30"
                       starttimeZone: "Asia/Calcutta"
                       endDateTime: "2024-12-01T11:00:00+05:30"
                       endTimeZone: "Asia/Calcutta"
                   """
        )
    }
)
public class InsertEvent extends AbstractTask implements RunnableTask<InsertEvent.Output> {

    @Schema(
        title = "Calendar ID on which the event should be inserted."
    )
    private String calendarId;

    @Schema(
        title = "Description of the event.",
        description = "This can contain HTML."
    )
    private String description;

    @Schema(
        title = "Title of the event."
    )
    private String summary;

    @Schema(
        title = "Start date time of the event.",
        description = "Should be in the format `2024-12-01T10:00:00+05:30`."
    )
    private String startDateTime;

    @Schema(
        title = "Time zone of the start date time of the event.",
        description = "Example `Asia/Calcutta`."
    )
    private String startTimeZone;

    @Schema(
        title = "End date time of the event.",
        description = "Should be in the format `2024-12-01T10:00:00+05:30`."
    )
    private String endDateTime;

    @Schema(
        title = "Time zone of the end date time of the event.",
        description = "Example `Asia/Calcutta`."
    )
    private String endTimeZone;

    @Schema(
        title = "Location of the event."
    )
    private String location;

    @Schema(
        title = "Creator of the event."
    )
    private Creator creator;
    
    @Override
    public InsertEvent.Output run(RunContext runContext) throws Exception {
        Logger logger = runContext.logger();
        
         // Initialize Camel Context
        CamelContext camelContext = new DefaultCamelContext();

        // Add the Google Calendar component to the Camel context
        camelContext.addComponent("google-calendar", this.calendarComponent(runContext));

        // Create Camel route for inserting event
        camelContext.addRoutes(new RouteBuilder() {
            @Override
            public void configure() throws Exception {

                from("direct:createEvent")
                    .to("google-calendar://events/insert")
                    .log("Event created with ID: ${body.id}")
                    .process(exchange -> {
                        //If the event is created successfully, return a success message
                        Event createdEvent = exchange.getIn().getBody(Event.class);
                        exchange.getIn().setBody(createdEvent.getId());
                    });
            }
        });

        camelContext.start();
        camelContext.setTracing(true);
        Event event = new Event()
            .setSummary(summary)
            .setLocation(location)
            .setDescription(description)
            .setCreator(creator)
            .setStart(new EventDateTime().setDateTime(new DateTime(startDateTime)).setTimeZone(startTimeZone))
            .setEnd(new EventDateTime().setDateTime(new DateTime(endDateTime)).setTimeZone(endTimeZone));

        // Send the event creation request
        final Map<String, Object> headers = new HashMap<>();
        // parameter type is String
        headers.put("CamelGoogleCalendar.calendarId", calendarId);
        // parameter type is com.google.api.services.calendar.model.Event
        headers.put("CamelGoogleCalendar.content", event);
        
        String response = (String)camelContext.createProducerTemplate().requestBodyAndHeaders("direct:createEvent", null, headers);

        System.out.println("Response: " + response);
        // Stop the Camel context
        camelContext.stop();
        return Output.builder().eventId(response).build();
    }

    /**
     * Input or Output can be nested as you need
     */
    @SuperBuilder
    @Getter
    public static class Output implements io.kestra.core.models.tasks.Output {
        @Schema(
            title = "Event ID of the generated event."
        )
        private final String eventId;
    }
}
