package net.uniquepixels.playerqueue.queue.listening;

import com.velocitypowered.api.proxy.messages.ChannelIdentifier;

public class QueueChannelIdentifier implements ChannelIdentifier {
    @Override
    public String getId() {
        return "uniquepixels:queue";
    }
}
