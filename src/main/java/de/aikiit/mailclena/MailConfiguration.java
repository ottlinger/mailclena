package de.aikiit.mailclena;

import lombok.Builder;
import lombok.Data;
import lombok.NonNull;

@Data
@Builder
class MailConfiguration {
    //@NonNull
    private String host;
    //@NonNull
    private String username;
    //@NonNull
    private String password;
}
