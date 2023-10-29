package net.uniquepixels.playerqueue.queue.server.httpbody;

public class RequestServerStatus {
    private final String server;
    private final String task;

    public RequestServerStatus(String server, String task) {
        this.server = server;
        this.task = task;
    }

    public String getTask() {
        return task;
    }

    public String getServer() {
        return server;
    }

}
