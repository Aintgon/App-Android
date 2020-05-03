package edu.uw.covidsafe.ble;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import edu.uw.covidsafe.utils.Constants;
import edu.uw.covidsafe.utils.TimeUtils;
import edu.uw.covidsafe.utils.Utils;
import java.util.List;

public class BleOpsAsyncTask extends AsyncTask<Void, Void, Void> {
    private BleDbRecordRepository repo;
    private BleRecord result;
    private Constants.BleDatabaseOps op;

    public BleOpsAsyncTask(Context cxt, String id, int rssi, long ts) {
        repo = new BleDbRecordRepository(cxt);
        this.result = new BleRecord(id, ts, rssi);
        this.op = Constants.BleDatabaseOps.Insert;
    }

    public BleOpsAsyncTask(Context cxt, Constants.BleDatabaseOps op) {
        repo = new BleDbRecordRepository(cxt);
        this.op = op;
    }

    @Override
    protected Void doInBackground(Void... params) {
        Log.e("ble","doinbackground ble "+this.op);
        if (this.op == Constants.BleDatabaseOps.Insert) {
            repo.insert(this.result);
//            if (Constants.WRITE_TO_DISK) {
//                Utils.bleLogToFile(this.context, this.result);
//            }
        }
        else if (this.op == Constants.BleDatabaseOps.ViewAll) {
            List<BleRecord> records = repo.getAllRecords();
            for (BleRecord record : records) {
                Log.e("ble",record.toString());
            }
        }
        else if (this.op == Constants.BleDatabaseOps.DeleteAll) {
            repo.deleteAll();
        }
        return null;
    }
}
