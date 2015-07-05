package tbs.fakehackerv3.console;

public class ConsoleItem {
    private String details;

    public ConsoleItem(String d) {
        if (d.equals("")) {
            this.details = (" " + d);
        } else {
            this.details = (" " + d);
        }

    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }
}