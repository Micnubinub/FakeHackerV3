package tbs.fakehackerv3;

/**
 * Created by Michael on 6/11/2015.
 */
public class ReceivedMessage {
    public final String message, when, from;

    public ReceivedMessage(String message, String when, String from) {
        this.when = when;
        this.message = message;
        this.from = from;
    }
}
