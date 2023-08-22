package net.uniquepixels.playerqueue.queue.server.httpbody;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class RequestNewCloudServer {

    private final String task;

}
