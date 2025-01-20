package io.kestra.plugin.camel.googlecalendar;

import lombok.*;
import lombok.experimental.SuperBuilder;
import io.kestra.core.exceptions.IllegalVariableEvaluationException;
import io.kestra.core.models.tasks.Task;
import io.kestra.core.runners.RunContext;
import org.apache.camel.component.google.calendar.GoogleCalendarComponent;

import java.io.IOException;

@SuperBuilder
@ToString
@EqualsAndHashCode
@Getter
@NoArgsConstructor
public abstract class AbstractTask extends Task implements GoogleCalendarInterface {
    protected String clientId;
    protected String clientSecret;
    protected String refreshToken;
    protected String serviceAccount;
    protected String delegate;

    public GoogleCalendarComponent calendarComponent(RunContext runContext) throws IllegalVariableEvaluationException, IOException {
        return GoogleCalendarComponentService.component(runContext, this);
    }
}