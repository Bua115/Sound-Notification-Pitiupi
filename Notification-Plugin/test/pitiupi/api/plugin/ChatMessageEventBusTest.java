package pitiupi.api.plugin;

import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ChatMessageEventBusTest {

    @Test
    void publishesOnlyWhileSubscribed() {
        ChatMessageEventBus bus = new ChatMessageEventBus();
        AtomicInteger received = new AtomicInteger();
        ChatMessageEventBus.Subscription subscription = bus.subscribe(event -> received.incrementAndGet());

        bus.publish(new ChatMessageReceivedEvent(ChatMessageReceivedEvent.Channel.PUBLIC));
        subscription.close();
        bus.publish(new ChatMessageReceivedEvent(ChatMessageReceivedEvent.Channel.PRIVATE));

        assertEquals(1, received.get());
    }

    @Test
    void listenerFailureDoesNotBlockOtherSubscribers() {
        ChatMessageEventBus bus = new ChatMessageEventBus();
        AtomicInteger received = new AtomicInteger();
        bus.subscribe(event -> {
            throw new IllegalStateException("listener failure");
        });
        bus.subscribe(event -> received.incrementAndGet());

        bus.publish(new ChatMessageReceivedEvent(ChatMessageReceivedEvent.Channel.PUBLIC));

        assertEquals(1, received.get());
    }
}
