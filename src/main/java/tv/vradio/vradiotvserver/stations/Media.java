package tv.vradio.vradiotvserver.stations;

import java.net.URL;

/**
 * Stores a given type of media and where to find it
 *
 * @param name The title of the media
 * @param url the link to the media
 * @param duration how long the media is (in ms)
 * @param streamingService which service the media is on
 */
public record Media(String name, URL url, double duration, StreamingService streamingService) {
    enum StreamingService {
        SPOTIFY,
        NETFLIX
    }
}
