package org.geyser.extension.zipclient.Listeners;

import org.geyser.extension.zipclient.Events.EventHandler;
import org.geyser.extension.zipclient.Events.EventListener;
import org.geyser.extension.zipclient.Events.JavaPacketEvent;
import org.geyser.extension.zipclient.ZipClientExtension;
import org.geysermc.mcprotocollib.protocol.packet.ingame.clientbound.scoreboard.ClientboundSetDisplayObjectivePacket;
import org.geysermc.mcprotocollib.protocol.packet.ingame.clientbound.scoreboard.ClientboundSetObjectivePacket;
import org.geysermc.mcprotocollib.protocol.packet.ingame.clientbound.scoreboard.ClientboundSetScorePacket;

public class ScoreboardListener implements EventListener {
    public static String staticObjectiveName = "zc_pl";//short for ZipClient PlayerList

    public ScoreboardListener(ZipClientExtension extension) {

    }

    @EventHandler
    public void onJavaPacketEvent(JavaPacketEvent event) {
        //ZipClientExtension.staticLogger.info("JavaPacketEvent");

        if (event.getPacket() instanceof ClientboundSetDisplayObjectivePacket setDisplayObjectivePacket) {
            ZipClientExtension.staticLogger.info("ClientboundSetDisplayObjectivePacket: " + setDisplayObjectivePacket.getName());
            event.setCanceled(!(setDisplayObjectivePacket.getName().equals(staticObjectiveName)));
        } else if (event.getPacket() instanceof ClientboundSetObjectivePacket setObjectivePacket) {
            ZipClientExtension.staticLogger.info("ClientboundSetObjectivePacket: " + setObjectivePacket.getName());
            event.setCanceled(!(setObjectivePacket.getName().equals(staticObjectiveName)));
        } else if (event.getPacket() instanceof ClientboundSetScorePacket setScorePacket) {
            ZipClientExtension.staticLogger.info("ClientboundSetScorePacket: " + setScorePacket.getObjective());
            event.setCanceled(!(setScorePacket.getObjective().equals(staticObjectiveName)));
        }
    }


}
