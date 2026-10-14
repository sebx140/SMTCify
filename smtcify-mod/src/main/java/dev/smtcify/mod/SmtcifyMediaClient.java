
package dev.smtcify.mod;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public final class SmtcifyMediaClient {
    private static final String STATUS_URL =
            "http://127.0.0.1:17643/status";

    private static final ScheduledExecutorService EXECUTOR =
            Executors.newSingleThreadScheduledExecutor(runnable -> {
                Thread thread = new Thread(runnable, "SMTCify-Media-Poll");
                thread.setDaemon(true);
                return thread;
            });

    private static volatile MediaInfo current = MediaInfo.disconnected();
    private static boolean started;

    private SmtcifyMediaClient() {
    }

    public static synchronized void start() {
        if (started) {
            return;
        }

        started = true;
        EXECUTOR.scheduleWithFixedDelay(
                SmtcifyMediaClient::update,
                0,
                1,
                TimeUnit.SECONDS
        );
    }

    public static MediaInfo getCurrent() {
        return current;
    }

    private static void update() {
        HttpURLConnection connection = null;

        try {
            connection = (HttpURLConnection) URI.create(STATUS_URL)
                    .toURL()
                    .openConnection();

            connection.setRequestMethod("GET");
            connection.setConnectTimeout(700);
            connection.setReadTimeout(700);
            connection.setUseCaches(false);

            if (connection.getResponseCode() != 200) {
                current = MediaInfo.disconnected();
                return;
            }

            try (InputStream input = connection.getInputStream()) {
                String response = new String(
                        input.readAllBytes(),
                        StandardCharsets.UTF_8
                );

                JsonObject json = JsonParser.parseString(response)
                        .getAsJsonObject();

                boolean available = json.has("available")
                        && json.get("available").getAsBoolean();

                if (!available) {
                    String message = getString(
                            json, "message",
                            "No hay una sesión multimedia activa."
                    );

                    current = MediaInfo.noSession(message);
                    return;
                }

                current = new MediaInfo(
                        true,
                        getString(json, "source", "Multimedia"),
                        getString(json, "title", "Título desconocido"),
                        getString(json, "artist", "Artista desconocido"),
                        getString(json, "album", ""),
                        getString(json, "status", "Desconocido"),
                        ""
                );
            }
        } catch (Exception exception) {
            current = MediaInfo.disconnected();
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
    }

    private static String getString(
            JsonObject json,
            String key,
            String fallback
    ) {
        if (!json.has(key) || json.get(key).isJsonNull()) {
            return fallback;
        }

        return json.get(key).getAsString();
    }

    public static final class MediaInfo {
        public final boolean available;
        public final String source;
        public final String title;
        public final String artist;
        public final String album;
        public final String status;
        public final String message;

        private MediaInfo(
                boolean available,
                String source,
                String title,
                String artist,
                String album,
                String status,
                String message
        ) {
            this.available = available;
            this.source = source;
            this.title = title;
            this.artist = artist;
            this.album = album;
            this.status = status;
            this.message = message;
        }

        private static MediaInfo disconnected() {
            return new MediaInfo(
                    false, "", "", "", "", "",
                    "Bridge desconectado"
            );
        }

        private static MediaInfo noSession(String message) {
            return new MediaInfo(
                    false, "", "", "", "", "",
                    message
            );
        }
    }
}
