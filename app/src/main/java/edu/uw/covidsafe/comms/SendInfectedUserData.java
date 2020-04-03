package edu.uw.covidsafe.comms;

import android.content.Context;
import android.os.AsyncTask;

import com.google.gson.JsonObject;

import edu.uw.covidsafe.gps.GpsDbRecordRepository;
import edu.uw.covidsafe.gps.GpsRecord;
import edu.uw.covidsafe.json.MatchMessage;
import edu.uw.covidsafe.json.Region;
import edu.uw.covidsafe.seed_uuid.SeedUUIDDbRecordRepository;
import edu.uw.covidsafe.seed_uuid.SeedUUIDRecord;
import edu.uw.covidsafe.utils.Constants;
import edu.uw.covidsafe.utils.Utils;

import java.util.List;

public class SendInfectedUserData extends AsyncTask<Void, Void, Void> {

    Context context;
    String fname;
    String lname;
    long dob;

    public SendInfectedUserData(Context context, String fname, String lname, long dob) {
        this.context = context;
        this.fname = fname;
        this.lname = lname;
        this.dob = dob;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
//        CommunicationConfig config = new CommunicationConfig(NetworkConstant.HOSTNAME, NetworkConstant.PORT, "TestServer");
//        QueryBuilder queryBuilder = new QueryBuilder(config);
//        queryBuilder.sendInfectedLogsOfUser(this.bleRecords, this.gpsRecords);
//        queryBuilder.sendInfectedUserData(this.fname, this.lname, this.dob);
    }

    @Override
    protected Void doInBackground(Void... params) {
        SeedUUIDDbRecordRepository seedUUIDRepo = new SeedUUIDDbRecordRepository(context);
        // getSeedAtBeginningOfInfectionWindow
        //infectionWindowInMinutes = 20160 . if infection window is 14 days and uuid generation time is 5 minutes
        //seedIndexAtBeginningOfInfectionWindow = 4032
        int infectionWindowInMilliseconds = 1000*60*60*24*Constants.InfectionWindowInDays;
        int UUIDGenerationIntervalInMiliseconds = Constants.UUIDGenerationIntervalInMinutes*60*1000;

        // find timestamp 14 days ago
        long timestampAtBeginningOfInfectionWindow = System.currentTimeMillis() - infectionWindowInMilliseconds;
        // find 6 minutes past 14 days ago
        long timestampDeviation = timestampAtBeginningOfInfectionWindow +
                UUIDGenerationIntervalInMiliseconds +
                Constants.InfectionWindowIntervalDeviationInMilliseconds;

        SeedUUIDRecord generatedRecord = seedUUIDRepo.getRecordBetween(
                timestampAtBeginningOfInfectionWindow,
                timestampDeviation);

        // if user has less than 14 days of records, just get the earliest record
        SeedUUIDRecord recordToSend = generatedRecord;
        if (generatedRecord == null) {
            List<SeedUUIDRecord> allRecords = seedUUIDRepo.getAllSortedRecords();
            recordToSend = allRecords.get(0);
        }

        //get most recent GPS entry as coarse location and send it
        GpsDbRecordRepository gpsRepo = new GpsDbRecordRepository(context);
        List<GpsRecord> sortedGpsRecords = gpsRepo.getSortedRecords();
        if (sortedGpsRecords.size() > 0) {
            GpsRecord gpsRecord = sortedGpsRecords.get(0);

            int gpsResolution = Constants.MaximumGpsResolution;
            sendRequest(recordToSend.seed, recordToSend.ts,
                    Utils.getCoarseGpsCoord(gpsRecord.getLat(), gpsResolution),
                    Utils.getCoarseGpsCoord(gpsRecord.getLongi(), gpsResolution),
                    Utils.getGpsPrecision(gpsResolution));
        }

        return null;
    }

    public void sendRequest(String seed, long ts, double lat, double longi, int precision) {
        //TODO, send the seed and timestamp to the server
        //send matchMessage
        JsonObject matchMessage = MatchMessage.toJson(
                new String[]{seed},
                new long[]{ts});
        JsonObject region = Region.toJson(lat, longi, precision);
        JsonObject obj = new JsonObject();
        obj.add("match_message", matchMessage);
        obj.add("region", region);

        // send obj
    }

    public void testDatabase() {
        SeedUUIDDbRecordRepository seedUUIDRepo = new SeedUUIDDbRecordRepository(context);
        seedUUIDRepo.deleteAll();
        long[] tss = new long[]{1585724400000L,1585638000000L,1585551600000L,1585465200000L,1585378800000L,1585292400000L,1585206000000L,1585119600000L,1585033200000L,1584946800000L,1584860400000L,1584774000000L,1584687600000L,1584601200000L,1584514800000L,1584428400000L,};
        for (Long l : tss) {
            seedUUIDRepo.insert(new SeedUUIDRecord(l, "",""));
        }

        SeedUUIDRecord generatedRecord = seedUUIDRepo.getRecordBetween(
                1585551600000L,
                1585638000000L);

        SeedUUIDRecord generatedRecord2 = seedUUIDRepo.getRecordBetween(
                1585810800000L,
                1585897200000L);
    }
}
