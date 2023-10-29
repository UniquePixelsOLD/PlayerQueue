package net.uniquepixels.playerqueue.queue.server.httpbody;

public class RequestNewCloudServer {
    private final String task;

    public RequestNewCloudServer(String task) {
        this.task = task;
    }

    public String getTask() {
        return task;
    }

}
