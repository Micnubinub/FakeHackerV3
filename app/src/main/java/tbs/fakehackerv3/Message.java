package tbs.fakehackerv3;

/**
 * Created by Michael on 5/12/2015.
 */
public class Message {
    public static final String MESSAGE_SEPARATOR = "///,///,///";
    public MessageType messageType;
    private String message;

    public Message(String message, MessageType messageType) {
        this.messageType = messageType;
        switch (messageType) {
            case COMMAND:
                this.message = message;
                break;
            case FILE:
                this.message = String.valueOf(messageType) + MESSAGE_SEPARATOR + message;
                break;
            case MESSAGE:
                if (message == null || message.length() < 1) {
                    message = "nothing to see here";
                }
                this.message = String.valueOf(messageType) + MESSAGE_SEPARATOR + message + MESSAGE_SEPARATOR + String.valueOf(System.currentTimeMillis());
                break;
            case CONFIRMATION:
                this.message = message;
                break;
        }
    }

    public String getSendableMessage() {
        return message;
    }

    public String getMessage() {
        return message.split(MESSAGE_SEPARATOR)[1];
    }

    public void setMessage(String messageStringWithSep) {
        final String[] msg = messageStringWithSep.split(MESSAGE_SEPARATOR);
        if (msg.length < 1) {

        } else {

            if (msg[0].contains("MESSAGE")) {
                messageType = MessageType.MESSAGE;
            } else if (msg[0].contains("FILE")) {
                messageType = MessageType.FILE;
            } else if (msg[0].contains("COMMAND")) {
                messageType = MessageType.COMMAND;
            }
            this.message = msg[1];
        }
    }

    @Override
    public String toString() {
        return message;
    }

    public enum MessageType {
        MESSAGE, FILE, COMMAND, CONFIRMATION
    }
}
