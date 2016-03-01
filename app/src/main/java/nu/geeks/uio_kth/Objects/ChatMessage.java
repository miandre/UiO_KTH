package nu.geeks.uio_kth.Objects;

/**
 * Created by Micke on 2016-03-01.
 */
public class ChatMessage {

    public String name;
    public String message;
    public String project_id;

    public ChatMessage(String name, String message, String project_id) {
        this.name = name;
        this.message = message;
        this.project_id = project_id;
    }
}
