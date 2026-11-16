# Sound notification plugin

This optional plugin plays different sounds for incoming public and private chat messages. The notification checkboxes are enabled by default and apply for the current application session.

## Protocol

This plugin uses an **in-process core event**, not a new network message or wire protocol:

- The core owns a `ChatMessageEventBus`, shared by loaded plugins through
  `ConnectionManager`.
- The chat plugin publishes `ChatMessageReceivedEvent` with channel `PUBLIC` or
  `PRIVATE` when it receives a message from another user. Public local echoes
  are filtered out.
- Events contain only the channel; they do not expose the message text.
- The notification plugin subscribes during startup and queues the matching
  sound for playback. Public and private notifications can be enabled or
  disabled independently in the plugin tab.

Private messages arrive over the chat plugin's private TCP connection, so both
the updated core and updated chat plugin are required for private notifications.
If the notification plugin is not installed, the event bus has no subscriber
and no sound is played.

## Install by cloning the project

Clone the Pitiupi repository version that includes this plugin and its entry in
`plugins.registry.json` and replace the base P2P `test` folder at `pitiupi/test` with the one that comes with the plugin (for a fork, use that fork's clone URL):

```bash
git clone <https://github.com/Bua115/Sound-Notification-Pitiupi>
```

Install a JDK 17 or newer and Apache Ant, then run the project script from the
cloned project directory. It builds the core and catalogued plugins, places
their JARs in `.pitiupi/plugins`, and starts the application.
Linux and WSL also need `python3` for the project script.

**Windows (Command Prompt or PowerShell):**

```bat
scripts\pitiupi.cmd run --all
```

**Linux or WSL:**

```bash
chmod +x scripts/pitiupi.sh
./scripts/pitiupi.sh run --all
```

After startup, open the **Plugins** tab and select **Sound notifications**.
Sound plays on the local machine; each installation that needs notifications
must use the updated core and chat plugin. The notification plugin itself is
optional.

## Customize sounds

Replace `src/pitiupi/notification/sounds/public.wav` and/or
`src/pitiupi/notification/sounds/private.wav` with PCM WAV files. Rebuild the
project so the script packages the updated resources, then restart the app:

```text
scripts\pitiupi.cmd run --all
```

On Linux or WSL, use `./scripts/pitiupi.sh build --all` instead.
