package pitiupi.notification;

import pitiupi.api.plugin.ChatMessageReceivedEvent;

import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineEvent;
import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.IOException;
import java.net.URL;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;

final class NotificationSoundPlayer implements AutoCloseable {

    private static final Logger LOGGER = Logger.getLogger(NotificationSoundPlayer.class.getName());
    private static final String PUBLIC_SOUND = "/pitiupi/notification/sounds/public.wav";
    private static final String PRIVATE_SOUND = "/pitiupi/notification/sounds/private.wav";

    private final ExecutorService playbackExecutor = Executors.newSingleThreadExecutor(runnable -> {
        Thread thread = new Thread(runnable, "pitiupi-notification-sound");
        thread.setDaemon(true);
        return thread;
    });

    void play(ChatMessageReceivedEvent.Channel channel) {
        String resource = channel == ChatMessageReceivedEvent.Channel.PUBLIC
                ? PUBLIC_SOUND
                : PRIVATE_SOUND;
        playbackExecutor.execute(() -> playResource(resource));
    }

    private void playResource(String resourcePath) {
        URL resource = NotificationSoundPlayer.class.getResource(resourcePath);
        if (resource == null) {
            LOGGER.warning("Notification sound resource is missing: " + resourcePath);
            return;
        }

        Clip clip = null;
        try (var audio = AudioSystem.getAudioInputStream(resource)) {
            clip = AudioSystem.getClip();
            Clip playingClip = clip;
            clip.addLineListener(event -> {
                if (event.getType() == LineEvent.Type.STOP) {
                    playingClip.close();
                }
            });
            clip.open(audio);
            clip.start();
        } catch (IOException | UnsupportedAudioFileException | javax.sound.sampled.LineUnavailableException ex) {
            if (clip != null) {
                clip.close();
            }
            LOGGER.log(Level.WARNING, "Could not play notification sound " + resourcePath, ex);
        }
    }

    @Override
    public void close() {
        playbackExecutor.shutdownNow();
    }
}
