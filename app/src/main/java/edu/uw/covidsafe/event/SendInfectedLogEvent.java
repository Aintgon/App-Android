package edu.uw.covidsafe.event;

public class SendInfectedLogEvent {
    private boolean requestStatus;
//    private AddedLogs response;

//    public AddedLogs getResponse() {
//        return response;
//    }

//    public void setResponse(AddedLogs response) {
//        this.response = response;
//    }

    public boolean isRequestStatus() {
        return requestStatus;
    }

    public void setRequestStatus(boolean requestStatus) {
        this.requestStatus = requestStatus;
    }
}
