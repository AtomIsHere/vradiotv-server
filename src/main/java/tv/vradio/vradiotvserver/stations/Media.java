package tv.vradio.vradiotvserver.stations;

/**
 * Stores a given type of media and where to find it
 *
 * @param name The title of the media
 * @param url the link to the media
 * @param duration how long the media is (in ms)
 * @param streamingService which service the media is on
 */
public record Media(String name, String url, long duration, StreamingService streamingService) {
    public enum StreamingService {
        SPOTIFY,
        NETFLIX
    }
}
