package org.geyser.extension.zipclient.Listeners;

import org.cloudburstmc.protocol.bedrock.packet.AddEntityPacket;
import org.geyser.extension.zipclient.Events.BedrockPacketEvent;
import org.geyser.extension.zipclient.Events.EventHandler;
import org.geyser.extension.zipclient.Events.EventListener;
import org.geyser.extension.zipclient.Events.JavaPacketEvent;
import org.geyser.extension.zipclient.Managers.FakeEntityManager;
import org.geyser.extension.zipclient.ZipClientExtension;
import org.geysermc.geyser.entity.type.Entity;
import org.geysermc.geyser.entity.type.player.PlayerEntity;
import org.geysermc.geyser.session.GeyserSession;
import org.geysermc.mcprotocollib.protocol.data.game.entity.type.EntityType;
import org.geysermc.mcprotocollib.protocol.packet.ingame.clientbound.entity.*;

public class ChamsListener implements EventListener {
    public ChamsListener(ZipClientExtension extension) {

    }

    @EventHandler
    public void onBedrockPacketEvent(BedrockPacketEvent event) {

    }

    @EventHandler
    public void onJavaPacketEvent(JavaPacketEvent event) {
        //ZipClientExtension.staticLogger.info("JavaPacketEvent");

        if (event.getPacket() instanceof ClientboundAddEntityPacket addEntityPacket) {
            if (addEntityPacket.getType().equals(EntityType.PLAYER)) {
                Entity entity = ((GeyserSession) event.getConnection()).getEntityCache().getEntityByJavaId(addEntityPacket.getEntityId());
                if (entity == null) {
                    ZipClientExtension.staticLogger.info("Entity Null");
                    return;

                }

                AddEntityPacket newAddEntityPacket = new AddEntityPacket();
                newAddEntityPacket.setUniqueEntityId(FakeEntityManager.getNextEntityId());
                newAddEntityPacket.setRuntimeEntityId(newAddEntityPacket.getUniqueEntityId());
                newAddEntityPacket.setIdentifier("wiki:ghost");
                newAddEntityPacket.setPosition(entity.getPosition());
                newAddEntityPacket.setMotion(entity.getMotion());
                newAddEntityPacket.setRotation(entity.getBedrockRotation().toVector2());

                ((GeyserSession) event.getConnection()).getUpstream().getSession().getPacketHandler().handle(newAddEntityPacket);

                FakeEntityManager.linkEntity(event.getConnection(), entity.getUuid(), newAddEntityPacket.getUniqueEntityId());
                ZipClientExtension.staticLogger.info("Added Fake Entity");

            }
        } else if (event.getPacket() instanceof ClientboundRemoveEntitiesPacket removeEntitiesPacket) {
            for (int entityID : removeEntitiesPacket.getEntityIds()) {
                if (((GeyserSession) event.getConnection()).getEntityCache().getEntityByJavaId(entityID) instanceof PlayerEntity playerEntity) {
                    FakeEntityManager.unregisterWholePlayer(event.getConnection(), playerEntity.getUuid());
                    ZipClientExtension.staticLogger.info("Tried To Wipe Fake Entity");//need more code here to make sure entity doesnt stay when player doesnt
                }
            }
        } else if (event.getPacket() instanceof ClientboundMoveEntityPosPacket moveEntityPosPacket) {
            if (((GeyserSession) event.getConnection()).getEntityCache().getEntityByJavaId(moveEntityPosPacket.getEntityId()) instanceof PlayerEntity) {

            }

        } else if (event.getPacket() instanceof ClientboundMoveEntityRotPacket moveEntityRotPacket) {

        } else if (event.getPacket() instanceof ClientboundMoveEntityPosRotPacket moveEntityPosRotPacket) {

        }
    }


}
