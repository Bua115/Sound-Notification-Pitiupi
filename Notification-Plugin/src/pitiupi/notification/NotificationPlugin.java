package pitiupi.notification;

import pitiupi.api.plugin.ChatMessageEventBus;
import pitiupi.api.plugin.ChatMessageReceivedEvent;
import pitiupi.api.plugin.PluginWorkspace;
import pitiupi.control.ConnectionManager;
import pitiupi.models.Messages.Message;
import pitiupi.models.Plugin;

import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import java.awt.GridLayout;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public final class NotificationPlugin extends Plugin {

    private final AtomicBoolean publicNotificationsEnabled = new AtomicBoolean(true);
    private final AtomicBoolean privateNotificationsEnabled = new AtomicBoolean(true);
    private NotificationSoundPlayer soundPlayer;
    private ChatMessageEventBus.Subscription subscription;

    @Override
    public void createPlugin(ConnectionManager connectionManager, PluginWorkspace workspace) {
        soundPlayer = new NotificationSoundPlayer();
        subscription = connectionManager.getChatMessageEventBus().subscribe(this::onChatMessageReceived);

        JCheckBox publicNotifications = new JCheckBox("Sound for public messages", true);
        JCheckBox privateNotifications = new JCheckBox("Sound for private messages", true);
        publicNotifications.addItemListener(event ->
                publicNotificationsEnabled.set(publicNotifications.isSelected()));
        privateNotifications.addItemListener(event ->
                privateNotificationsEnabled.set(privateNotifications.isSelected()));

        JPanel panel = new JPanel(new GridLayout(0, 1));
        panel.add(new JLabel("Incoming chat sounds"));
        panel.add(publicNotifications);
        panel.add(privateNotifications);
        panel.add(new JLabel("Custom WAV files: src/pitiupi/notification/sounds/"));
        bindShellWorkspaceContent(panel);
    }

    private void onChatMessageReceived(ChatMessageReceivedEvent event) {
        if (event.channel() == ChatMessageReceivedEvent.Channel.PUBLIC
                && publicNotificationsEnabled.get()) {
            soundPlayer.play(event.channel());
        } else if (event.channel() == ChatMessageReceivedEvent.Channel.PRIVATE
                && privateNotificationsEnabled.get()) {
            soundPlayer.play(event.channel());
        }
    }

    @Override
    public List<Class<? extends Message>> getMessages() {
        return List.of();
    }

    @Override
    public void receiveMessage(Message message) {
    }

    @Override
    public String getName() {
        return "Notifications";
    }

    @Override
    public String getDisplayName() {
        return "Sound notifications";
    }

    @Override
    public String getAuthor() {
        return "Pitiupi";
    }

    @Override
    public String getVersion() {
        return "1";
    }

    @Override
    public String getIdentifier() {
        return "pitiupi.notification";
    }
}
