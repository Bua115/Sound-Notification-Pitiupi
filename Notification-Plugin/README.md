# Pitiupi sound notifications

This folder is an optional plugin module. Copy it into a Pitiupi app clone as
`plugins/notification-plugin`. The plugin cannot work by itself in an
unmodified base app: public and private chat messages are delivered to the chat
plugin, so the base needs a small event API and chat integration.

## What is in this folder

- `src/` contains the notification plugin, SPI registration, and default WAVs.
- `build.xml` builds `dist/pitiupi.notification.jar`. Keep this file at the
  plugin folder root, beside `src/`.
- `integration/core/pitiupi/api/plugin/` contains two new core API source files.
- `integration/chat-event-hook.patch` adds the event bus to the core's
  `ConnectionManager` and publishes events from the existing chat plugin.
- `integration/plugins.registry-entry.txt` is the catalog entry to add to the
  base app's root `plugins.registry.json`.

## Install into a fresh base app

Start with a clone of the Pitiupi base app, then copy this entire
`notification-plugin` folder into that clone's `plugins/` directory:

```text
<app>/
  plugins/notification-plugin/
  pitiupi/src/pitiupi/
  plugins/chat/src/pitiupi/chat/plugin/ChatPlugin.java
  plugins.registry.json
```

Apply the following integration in the base app before building:

1. Copy both Java files from
   `plugins/notification-plugin/integration/core/pitiupi/api/plugin/` into
   `<app>/pitiupi/src/pitiupi/api/plugin/`.
2. From the base app's root, apply the chat/core changes:

   ```text
   git apply plugins/notification-plugin/integration/chat-event-hook.patch
   ```

   The patch updates the existing `ConnectionManager.java` and
   `ChatPlugin.java`; it does not replace either file. If `git apply` reports a
   conflict, the base app's code differs from the patch's expected version.
   Do not force it: port the same changes to that version instead.
3. Add the entry from
   `plugins/notification-plugin/integration/plugins.registry-entry.txt` to the
   `plugins` array in the base app's root `plugins.registry.json`. Keep the
   existing entries and add a comma where needed.
4. Build and launch from the base app's root. The launcher rebuilds the core
   and catalogued plugins, stages their JARs in `.pitiupi/plugins/`, then
   starts the application.

The build requires JDK 17 or newer and Apache Ant. The Linux/WSL launcher also
requires Python 3 to read the plugin registry; the Windows launcher uses
PowerShell instead.

   **Windows:**

   ```bat
   scripts\pitiupi.cmd run --all
   ```

   **Linux or WSL:**

   ```bash
   chmod +x scripts/pitiupi.sh
   ./scripts/pitiupi.sh run --all
   ```

5. In the app, open **Plugins** and select **Sound notifications**.

Use the core and chat plugin from the same base app clone you patched. Every
installation that should play sounds needs the updated core, updated chat JAR,
and notification JAR. The notification plugin is optional; without it, the
chat event bus has no notification subscriber.

## Protocol

This is a local, in-process event. `ChatPlugin` emits
`ChatMessageReceivedEvent` with `PUBLIC` or `PRIVATE` when it receives a
message from another user. The event carries only the channel, not message
text. The notification plugin subscribes and plays the matching local WAV.
Public echoes of the local user's own messages are filtered.

## Change the sounds

Replace `src/pitiupi/notification/sounds/public.wav` and/or `private.wav` with
PCM WAV files of the same names. From the base app root, rebuild and relaunch:

```text
scripts\pitiupi.cmd run --all
```

On Linux or WSL use `./scripts/pitiupi.sh run --all`. The rebuilt JAR packages
the new WAV resources.
