package net.uniquepixels.playerqueue.queue.server.httpbody;

public class GotServerStatus {
    private final String task;
    private final String server;
    private final String status;

    public GotServerStatus(String task, String server, String status) {
        this.task = task;
        this.server = server;
        this.status = status;
    }

    public String getTask() {
        return task;
    }

    public String getServer() {
        return server;
    }

    public String getStatus() {
        return status;
    }

}
