package nu.geeks.uio_kth.Database;

/**
 * Created by Micke on 2016-02-22.
 *
 * Callback interface for asynchronus background task
 */
public interface GetProjectCallback {

    void done (int projectPosition);
}
