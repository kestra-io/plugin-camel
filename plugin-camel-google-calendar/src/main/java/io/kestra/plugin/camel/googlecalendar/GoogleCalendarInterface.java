package io.kestra.plugin.camel.googlecalendar;

import io.kestra.core.models.annotations.PluginProperty;
import io.swagger.v3.oas.annotations.media.Schema;

public interface GoogleCalendarInterface {
    @Schema(
        title = "Client ID.",
        description = "Either clientId, cleientSecret and refreshToken, or serviceAccount is required. Please refer [Apache Camel documentation](https://camel.apache.org/components/4.8.x/google-calendar-component.html) for more details."
    )
    @PluginProperty(dynamic = true)
    String getClientId();

    @Schema(
        title = "Client secret.",
        description = "Either clientId, cleientSecret and refreshToken, or serviceAccount is required. Please refer [Apache Camel documentation](https://camel.apache.org/components/4.8.x/google-calendar-component.html) for more details."
    )
    @PluginProperty(dynamic = true)
    String getClientSecret();

    @Schema(
        title = "Referesh Token.",
        description = "You can use [OAuth Playground](https://developers.google.com/oauthplayground) to generate referesh token."
    )
    @PluginProperty(dynamic = true)
    String getRefreshToken();

    @Schema(
        title = "The full service account JSON key to use to authenticate to gcloud.",
        description = "Either clientId, cleientSecret and refreshToken, or serviceAccount is required. Please refer [Apache Camel documentation](https://camel.apache.org/components/4.8.x/google-calendar-component.html) for more details."
    )
    @PluginProperty(dynamic = true)
    String getServiceAccount();

    @Schema(
        title = "Delegate email address."
    )
    @PluginProperty(dynamic = true)
    String getDelegate();
}
