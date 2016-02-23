package nu.geeks.uio_kth.Database;

import java.util.ArrayList;

import nu.geeks.uio_kth.Objects.Transaction;

/**
 * Created by Micke on 2016-02-23.
 */
public interface GetTransactionCallback {

    void done(ArrayList<Transaction> transactions);
}
