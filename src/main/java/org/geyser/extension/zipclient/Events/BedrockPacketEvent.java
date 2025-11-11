package org.geyser.extension.zipclient.Events;

import org.cloudburstmc.protocol.bedrock.packet.BedrockPacket;
import org.geysermc.geyser.api.connection.GeyserConnection;

public class BedrockPacketEvent extends Cancelable implements Event {
    private final BedrockPacket packet;
    private final GeyserConnection connection;

    public BedrockPacketEvent(BedrockPacket packet, GeyserConnection connection) {
        this.packet = packet;
        this.connection = connection;
    }

    public BedrockPacket getPacket() {
        return this.packet;
    }

    public GeyserConnection getConnection() {
        return this.connection;
    }
}
