package nu.geeks.uio_kth.Database;

import java.util.ArrayList;

import nu.geeks.uio_kth.Objects.ChatMessage;

/**
 * Created by Micke on 2016-03-01.
 */
public interface GetChatCallback {

    void done(ArrayList<ChatMessage> chatContent);
}
