package io.kestra.plugin.camel.googlecalendar;

import java.io.IOException;
import java.nio.file.Path;

import org.apache.camel.component.google.calendar.GoogleCalendarComponent;
import org.apache.camel.component.google.calendar.GoogleCalendarConfiguration;

import io.kestra.core.exceptions.IllegalVariableEvaluationException;
import io.kestra.core.runners.RunContext;

public final class GoogleCalendarComponentService {
    private GoogleCalendarComponentService() {
    }

    public static GoogleCalendarComponent component(RunContext runContext, GoogleCalendarInterface googleCalendarInterface) throws IllegalVariableEvaluationException, IOException {
        if((googleCalendarInterface.getClientId() == null || googleCalendarInterface.getClientSecret() == null || googleCalendarInterface.getRefreshToken() == null) && (googleCalendarInterface.getServiceAccount() == null)) {
            throw new IllegalVariableEvaluationException("Either clientId, clientSecret and refreshToken or serviceAccount should be present. Refer https://camel.apache.org/components/4.8.x/google-calendar-component.html for nore details.");
        }
        // Configure Google Calendar Component
        GoogleCalendarConfiguration configuration = new GoogleCalendarConfiguration();
        if(googleCalendarInterface.getClientId() != null) {
            configuration.setClientId(googleCalendarInterface.getClientId());
            configuration.setClientSecret(googleCalendarInterface.getClientSecret());
            configuration.setRefreshToken(googleCalendarInterface.getRefreshToken()); // You can get this using OAuth 2.0 Playground
        }
        else {
            Path serviceAccountPath = runContext.workingDir().createTempFile(runContext.render(googleCalendarInterface.getServiceAccount()).getBytes());
            configuration.setServiceAccountKey(serviceAccountPath.toString());
            if (googleCalendarInterface.getDelegate() != null) {
                configuration.setDelegate(googleCalendarInterface.getDelegate());
            }
        }

        GoogleCalendarComponent googleCalendarComponent = new GoogleCalendarComponent();
        googleCalendarComponent.setConfiguration(configuration);
        return googleCalendarComponent;
    }
}