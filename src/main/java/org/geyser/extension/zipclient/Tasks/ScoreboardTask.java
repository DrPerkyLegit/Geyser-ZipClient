package org.geyser.extension.zipclient.Tasks;

import io.netty.channel.Channel;
import net.kyori.adventure.text.Component;
import org.cloudburstmc.protocol.bedrock.packet.BedrockPacket;
import org.cloudburstmc.protocol.bedrock.packet.BedrockPacketHandler;
import org.cloudburstmc.protocol.common.PacketSignal;
import org.geyser.extension.zipclient.Listeners.ScoreboardListener;
import org.geyser.extension.zipclient.ZipClientExtension;
import org.geysermc.event.subscribe.Subscribe;
import org.geysermc.geyser.api.connection.GeyserConnection;
import org.geysermc.geyser.scoreboard.Objective;
import org.geysermc.geyser.scoreboard.Scoreboard;
import org.geysermc.geyser.scoreboard.ScoreboardUpdater;
import org.geysermc.geyser.scoreboard.display.slot.DisplaySlot;
import org.geysermc.geyser.session.GeyserSession;
import org.geysermc.mcprotocollib.protocol.data.game.chat.numbers.BlankFormat;
import org.geysermc.mcprotocollib.protocol.data.game.scoreboard.ObjectiveAction;
import org.geysermc.mcprotocollib.protocol.data.game.scoreboard.ScoreType;
import org.geysermc.mcprotocollib.protocol.data.game.scoreboard.ScoreboardPosition;
import org.geysermc.mcprotocollib.protocol.packet.ingame.clientbound.scoreboard.ClientboundSetDisplayObjectivePacket;
import org.geysermc.mcprotocollib.protocol.packet.ingame.clientbound.scoreboard.ClientboundSetObjectivePacket;
import org.geysermc.mcprotocollib.protocol.packet.ingame.clientbound.scoreboard.ClientboundSetScorePacket;

import java.text.NumberFormat;
import java.util.HashMap;
import java.util.Map;

public class ScoreboardTask implements Runnable {
    private ZipClientExtension extension;
    private static Map<GeyserConnection, Scoreboard> scoreboardMap = new HashMap<>();

    public ScoreboardTask(ZipClientExtension extension) {
        this.extension = extension;
    }

    public static void wipeConnection(GeyserConnection connection) {
        scoreboardMap.remove(connection);
    }

    @Override
    public void run() {
        try {
            for (GeyserConnection user : extension.geyserApi().onlineConnections()) {

                if (!scoreboardMap.containsKey(user)) {
                    GeyserSession session = (GeyserSession) user;

                    Scoreboard scoreboard = new Scoreboard(session);

                    // 1) create objective
                    Objective objective = scoreboard.registerNewObjective("test");

                    // 2) set display name if API exists
                    // if Objective has setDisplayName(Component), call it here
                    // otherwise check for a constructor overload that accepts a name

                    // 3) show it in sidebar
                    scoreboard.displayObjective("test", ScoreboardPosition.SIDEBAR);

                    // 4) add lines (unique keys, indexes)
                    objective.registerScore("line_top", 1, Component.text("§fYep"), BlankFormat.INSTANCE);

                    // 5) register player to push data
                    scoreboard.playerRegistered(session.getPlayerEntity());

                    // 6) send the packets immediately
                    scoreboard.onUpdate();

                    // store scoreboard for later updates
                    scoreboardMap.put(user, scoreboard);

                }

            }
        } catch (Exception e) {
            ZipClientExtension.staticLogger.error("ScoreboardTask failed", e);
        }
    }


}
