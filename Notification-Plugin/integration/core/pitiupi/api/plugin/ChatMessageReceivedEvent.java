package pitiupi.api.plugin;

import java.util.Objects;

public record ChatMessageReceivedEvent(Channel channel) {

    public ChatMessageReceivedEvent {
        Objects.requireNonNull(channel, "channel");
    }

    public enum Channel {
        PUBLIC,
        PRIVATE
    }
}
