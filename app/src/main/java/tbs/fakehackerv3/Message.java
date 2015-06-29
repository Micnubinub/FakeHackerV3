package tbs.fakehackerv3;

/**
 * Created by Michael on 5/12/2015.
 */
public class Message {
    public static final String MESSAGE_SEPARATOR = "///,///,///";
    private String message;
    public MessageType messageType;

    public Message(String message, MessageType messageType) {
        this.messageType = messageType;
        switch (messageType) {
            case SEND_COMMAND:
                this.message = String.valueOf(messageType) + message;
                break;
            case SEND_FILE:
                this.message = String.valueOf(messageType) + MESSAGE_SEPARATOR + message;
                break;
            case SEND_MESSAGE:
                if (message == null || message.length() < 1) {
                    message = "nothing to see here";
                }
                this.message = String.valueOf(messageType) + MESSAGE_SEPARATOR + message + MESSAGE_SEPARATOR + String.valueOf(System.currentTimeMillis());
                break;
        }
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

    public String getSendableMessage() {
        return message;
    }

    public String getMessage() {
        return message.split(MESSAGE_SEPARATOR)[1];
    }

    public enum MessageType {
        SEND_MESSAGE, SEND_FILE, SEND_COMMAND
    }

}
