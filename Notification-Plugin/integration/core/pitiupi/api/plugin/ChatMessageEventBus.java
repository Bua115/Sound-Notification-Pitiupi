package pitiupi.api.plugin;

import java.util.Objects;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;

public final class ChatMessageEventBus {

    private static final Logger LOGGER = Logger.getLogger(ChatMessageEventBus.class.getName());

    private final CopyOnWriteArrayList<Listener> listeners = new CopyOnWriteArrayList<>();

    public Subscription subscribe(Consumer<ChatMessageReceivedEvent> listener) {
        Listener entry = new Listener(Objects.requireNonNull(listener, "listener"));
        listeners.add(entry);
        AtomicBoolean subscribed = new AtomicBoolean(true);
        return () -> {
            if (subscribed.compareAndSet(true, false)) {
                listeners.remove(entry);
            }
        };
    }

    public void publish(ChatMessageReceivedEvent event) {
        Objects.requireNonNull(event, "event");
        for (Listener listener : listeners) {
            try {
                listener.consumer().accept(event);
            } catch (RuntimeException ex) {
                LOGGER.log(Level.WARNING, "Chat message event listener failed", ex);
            }
        }
    }

    @FunctionalInterface
    public interface Subscription {
        void close();
    }

    private record Listener(Consumer<ChatMessageReceivedEvent> consumer) {
    }
}
