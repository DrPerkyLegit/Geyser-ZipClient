package org.geyser.extension.zipclient;

import io.netty.channel.*;
import io.netty.util.AttributeKey;
import org.cloudburstmc.protocol.bedrock.BedrockServerSession;
import org.geyser.extension.zipclient.Events.EventManager;
import org.geyser.extension.zipclient.Events.JavaPacketEvent;
import org.geyser.extension.zipclient.GUI.ZipClientForm;
import org.geyser.extension.zipclient.Listeners.ScoreboardListener;
import org.geyser.extension.zipclient.Random.EmoteID;
import org.geyser.extension.zipclient.Managers.FakeEntityManager;
import org.geyser.extension.zipclient.Random.PacketHandlers;
import org.geyser.extension.zipclient.Random.TaskScheduler;
import org.geyser.extension.zipclient.Tasks.ScoreboardTask;
import org.geysermc.event.subscribe.Subscribe;
import org.geysermc.geyser.api.event.bedrock.ClientEmoteEvent;
import org.geysermc.geyser.api.event.bedrock.SessionDisconnectEvent;
import org.geysermc.geyser.api.event.bedrock.SessionJoinEvent;
import org.geysermc.geyser.api.event.lifecycle.GeyserLoadResourcePacksEvent;
import org.geysermc.geyser.api.event.lifecycle.GeyserPostInitializeEvent;
import org.geysermc.geyser.api.extension.Extension;
import org.geysermc.geyser.api.extension.ExtensionLogger;
import org.geysermc.geyser.session.GeyserSession;
import org.geysermc.mcprotocollib.network.packet.Packet;

import java.util.concurrent.TimeUnit;

import static net.bytebuddy.matcher.ElementMatchers.takesArguments;

/**
 * The main class of your extension - must implement extension, and be in the extension.yml file.
 * See {@link Extension} for available methods - for example to get the path to the configuration folder.
 */
public class ZipClientExtension implements Extension {
    public static final AttributeKey<GeyserSession> SESSION_KEY = AttributeKey.valueOf("zipclient-session");
    //public TaskScheduler scheduler;

    public static ExtensionLogger staticLogger;

    private static List<>

    @Subscribe
    public void onGeyserLoadResourcePacksEvent(GeyserLoadResourcePacksEvent event) {
        //logger().info("Loading: " + event.resourcePacks().size() + " resource packs.");
    }

    @Subscribe
    public void onPostInitialize(GeyserPostInitializeEvent event) {
        staticLogger = this.logger();
        staticLogger.info("Loading %s...".formatted(this.description().name()));

        //scheduler = new TaskScheduler();

        //EventManager.register(new ScoreboardListener(this));
        //EventManager.register(new ChamsListener(this));

        //scheduler.runRepeating(new ScoreboardTask(this), 0, 1, TimeUnit.SECONDS);


    }

    @Subscribe
    public void onPlayerJoin(SessionJoinEvent event) {
        BedrockServerSession upstreamSession = ((GeyserSession) event.connection()).getUpstream().getSession();
        //i think jvm is deleting old handler before it calls disconnect
        upstreamSession.setPacketHandler(PacketHandlers.createBedrockHandler(event.connection(), upstreamSession.getPacketHandler()));


        Channel channel = ((GeyserSession) event.connection()).getDownstream().getSession().getChannel();
        channel.attr(SESSION_KEY).set((GeyserSession) event.connection());


        if (channel.pipeline().get("ZipClient-PacketHandler") == null) {
            channel.pipeline().addBefore("manager", "ZipClient-PacketHandler", new ChannelDuplexHandler() {
                @Override
                public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                    if (msg instanceof Packet packet) {
                        GeyserSession session = ctx.channel().attr(ZipClientExtension.SESSION_KEY).get();

                        JavaPacketEvent packetEvent = new JavaPacketEvent(packet, session);
                        EventManager.call(packetEvent);

                        if (packetEvent.isCanceled()) return;
                    }
                    super.channelRead(ctx, msg);
                }

                @Override
                public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
                    super.write(ctx, msg, promise);
                }
            });
        }

        //((GeyserSession) event.connection()).getDownstream().getSession().callPacketReceived();

    }

    @Subscribe
    public void onDisconnect(SessionDisconnectEvent event) {
        ScoreboardTask.wipeConnection(event.connection());
        FakeEntityManager.wipeConnection(event.connection());

        ZipClientExtension.staticLogger.info("Bedrock Player Disconnect: " + event.connection().bedrockUsername());
    }


    //handle menu open
    @Subscribe
    public void onEmoteEvent(ClientEmoteEvent event) {
        if (EmoteID.Follow.isSame(event.emoteId())) {
            ZipClientForm menuSelector = new ZipClientForm(this, event.connection());
            menuSelector.sendForm();
        }
        //event.connection().sendMessage(event.emoteId());
        event.setCancelled(true);
    }


}
