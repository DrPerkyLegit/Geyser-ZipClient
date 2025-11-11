package org.geyser.extension.zipclient.Managers;

import org.geysermc.geyser.api.connection.GeyserConnection;

import java.util.*;
import java.util.concurrent.atomic.AtomicLong;

public class FakeEntityManager {
    private static Map<GeyserConnection, Map<UUID, List<Long>>> trackedFakeEntities = new HashMap<>();
    private static List<Long> unregisteredEntityIds = new ArrayList<>();
    private static final AtomicLong ENTITY_ID_COUNTER = new AtomicLong(1_000_000L);

    public static void wipeConnection(GeyserConnection connection) {
        for(Map.Entry<GeyserConnection, Map<UUID, List<Long>>> entry : trackedFakeEntities.entrySet()) {
            Map<UUID, List<Long>> trackedPlayerMap = entry.getValue();
            for(Map.Entry<UUID, List<Long>> playerEntry : trackedPlayerMap.entrySet()) {
                unregisterWholePlayer(connection, playerEntry.getKey());
            }
        }

        trackedFakeEntities.remove(connection);
    }

    public static long getNextEntityId() {
        if (unregisteredEntityIds.isEmpty()) return ENTITY_ID_COUNTER.getAndIncrement();

        return unregisteredEntityIds.removeFirst();
    }

    public static void linkEntity(GeyserConnection user, UUID player, long entityID) {
        Map<UUID, List<Long>> allLinkedEntities;
        List<Long> playerLinkedEntities;

        if (trackedFakeEntities.containsKey(user)) allLinkedEntities = trackedFakeEntities.get(user);
        else allLinkedEntities = new HashMap<>();

        if (allLinkedEntities.containsKey(player)) playerLinkedEntities = allLinkedEntities.get(player);
        else playerLinkedEntities = new ArrayList<>();

        playerLinkedEntities.add(entityID);
        allLinkedEntities.put(player, playerLinkedEntities);
        trackedFakeEntities.put(user, allLinkedEntities);
    }

    public static List<Long> getLinkedEntities(GeyserConnection user, UUID player) {
        if (!trackedFakeEntities.containsKey(user)) return new ArrayList<>();
        Map<UUID, List<Long>> allLinkedEntities = trackedFakeEntities.get(user);
        if (!allLinkedEntities.containsKey(player)) return new ArrayList<>();

        return new ArrayList<>(allLinkedEntities.get(player));//immutable array
    }

    //ONLY CALL WHEN ENTITY REMOVED
    public static boolean unregisterEntityId(GeyserConnection user, UUID player, long entityID) {
        if (!trackedFakeEntities.containsKey(user)) return false;
        Map<UUID, List<Long>> allLinkedEntities = trackedFakeEntities.get(user);
        if (!allLinkedEntities.containsKey(player)) return false;

        List<Long> playerLinkedEntities = allLinkedEntities.get(player);
        if (!playerLinkedEntities.contains(entityID)) return false;

        playerLinkedEntities.remove(entityID);
        allLinkedEntities.put(player, playerLinkedEntities);
        trackedFakeEntities.put(user, allLinkedEntities);

        unregisteredEntityIds.add(entityID);

        return true;
    }

    //ONLY CALL WHEN ENTITY REMOVED
    public static boolean unregisterWholePlayer(GeyserConnection user, UUID player) {
        if (!trackedFakeEntities.containsKey(user)) return true;
        Map<UUID, List<Long>> allLinkedEntities = trackedFakeEntities.get(user);
        if (!allLinkedEntities.containsKey(player)) return true;

        unregisteredEntityIds.addAll(allLinkedEntities.get(player));

        allLinkedEntities.remove(player);
        trackedFakeEntities.put(user, allLinkedEntities);

        return true;
    }
}
