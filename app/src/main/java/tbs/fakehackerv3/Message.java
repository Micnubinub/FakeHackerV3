package tbs.fakehackerv3;

/**
 * Created by Michael on 5/12/2015.
 */
public class Message {
    public static final String MESSAGE_SEPARATOR = "///,///,///";
    public String message;
    public MessageType messageType;

    public Message(String message, MessageType messageType) {
        this.message = message;
        this.messageType = messageType;
    }

    public Message(String messageStringWithSep) {
        setMessage(messageStringWithSep);
    }

    public void setMessage(String messageStringWithSep) {
        final String[] msg = messageStringWithSep.split(MESSAGE_SEPARATOR);
        if (msg.length < 1) {

        } else {

            if (msg[0].contains("SEND_MESSAGE")) {
                messageType = MessageType.SEND_MESSAGE;
            } else if (msg[0].contains("SEND_FILE")) {
                messageType = MessageType.SEND_FILE;
            } else if (msg[0].contains("SEND_COMMAND")) {
                messageType = MessageType.SEND_COMMAND;
            }
            this.message = msg[1];
        }
    }

    public enum MessageType {
        SEND_MESSAGE, SEND_FILE, SEND_COMMAND
    }

}
