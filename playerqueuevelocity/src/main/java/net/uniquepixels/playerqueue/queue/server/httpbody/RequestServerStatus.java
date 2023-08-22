package net.uniquepixels.playerqueue.queue.server.httpbody;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class RequestServerStatus {

    private final String server;
    private final String task;

}
