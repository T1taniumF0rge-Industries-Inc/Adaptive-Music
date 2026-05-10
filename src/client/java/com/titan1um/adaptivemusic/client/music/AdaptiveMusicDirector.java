package com.titan1um.adaptivemusic.client.music;

import com.titan1um.adaptivemusic.client.config.AdaptiveMusicConfig;
import com.titan1um.adaptivemusic.network.HomeRequestPayload;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.client.toast.SystemToast;
import net.minecraft.entity.Entity;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.sound.SoundEvent;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

public final class AdaptiveMusicDirector {
    private final MinecraftClient client;
    private final Set<UUID> knownPlayers = new HashSet<>();
    private String currentMood = "default";
    private int ticksUntilScan;
    private int ticksUntilSwitch;
    private int rememberedDeaths;
    private int lastObservedDeathCount = -1;
    private int socialPulseTicks;
    private int ticksUntilHomeRequest;
    private BlockPos homeAnchor;
    private boolean serverHomeSynced;

    public AdaptiveMusicDirector(MinecraftClient client) {
        this.client = client;
    }

    public void tick() {
        AdaptiveMusicConfig config = AdaptiveMusicConfig.get();
        if (!config.enabled || client.player == null || client.world == null) {
            return;
        }

        if (ticksUntilScan-- > 0) {
            return;
        }
        ticksUntilScan = 40;
        ticksUntilSwitch = Math.max(0, ticksUntilSwitch - 40);

        rememberDeaths();
        rememberHome();
        rememberPlayers();

        String nextMood = chooseMood(config);
        if (!nextMood.equals(currentMood) && ticksUntilSwitch <= 0) {
            switchMood(nextMood, config);
        }
    }

    public void openConfig() {
        client.setScreen(com.titan1um.adaptivemusic.client.gui.AdaptiveMusicConfigScreen.create(client.currentScreen));
    }

    public String statusLine() {
        return "Mood=" + currentMood + ", deaths=" + rememberedDeaths + ", home=" + (homeAnchor == null ? "unset" : homeAnchor.toShortString());
    }

    public void markHomeHere() {
        if (client.player != null) {
            homeAnchor = client.player.getBlockPos();
            serverHomeSynced = false;
            toast("Adaptive Music", "Home motif anchor saved.");
        }
    }

    public void applyServerHome(Optional<BlockPos> homePos) {
        serverHomeSynced = true;
        if (homePos.isPresent()) {
            homeAnchor = homePos.get();
            toast("Adaptive Music", "Server home motif synced at " + homeAnchor.toShortString() + ".");
        } else {
            homeAnchor = null;
            toast("Adaptive Music", "Server has no respawn/home anchor yet.");
        }
    }

    public void prepareForServerJoin() {
        homeAnchor = null;
        serverHomeSynced = false;
        ticksUntilHomeRequest = 0;
        requestHomeSyncFromServer();
    }

    public void requestHomeSyncFromServer() {
        try {
            if (ClientPlayNetworking.canSend(HomeRequestPayload.ID)) {
                ClientPlayNetworking.send(HomeRequestPayload.INSTANCE);
                ticksUntilHomeRequest = 20 * 30;
            }
        } catch (IllegalStateException ignored) {
            // Not connected to a play server yet.
        }
    }

    private void rememberDeaths() {
        int deaths = client.player.getStatHandler().getStat(net.minecraft.stat.Stats.CUSTOM.getOrCreateStat(net.minecraft.stat.Stats.DEATHS));
        if (lastObservedDeathCount >= 0 && deaths > lastObservedDeathCount) {
            rememberedDeaths += deaths - lastObservedDeathCount;
        }
        lastObservedDeathCount = deaths;
    }

    private void rememberHome() {
        if (!serverHomeSynced && homeAnchor == null && ticksUntilHomeRequest <= 0) {
            requestHomeSyncFromServer();
        }
        ticksUntilHomeRequest = Math.max(0, ticksUntilHomeRequest - 40);
    }

    private void rememberPlayers() {
        Set<UUID> visibleNow = new HashSet<>();
        client.world.getPlayers().forEach(player -> {
            visibleNow.add(player.getUuid());
            if (!player.getUuid().equals(client.player.getUuid()) && knownPlayers.add(player.getUuid())) {
                socialPulseTicks = 20 * 30;
                toast("Adaptive Music", player.getName().getString() + " motif noticed.");
            }
        });
        knownPlayers.retainAll(visibleNow);
        socialPulseTicks = Math.max(0, socialPulseTicks - 40);
    }

    private String chooseMood(AdaptiveMusicConfig config) {
        if (socialPulseTicks > 0) {
            return "social";
        }
        if (nearDanger(config)) {
            return "danger";
        }
        if (rememberedDeaths >= config.deathMemoryThreshold) {
            return "death_memory";
        }
        if (nearHome(config)) {
            return "home";
        }
        if (nearSpawn(config)) {
            return "spawn_nostalgia";
        }
        return "default";
    }

    private boolean nearDanger(AdaptiveMusicConfig config) {
        Box box = client.player.getBoundingBox().expand(config.scanRadius);
        for (Entity entity : client.world.getOtherEntities(client.player, box)) {
            if (entity instanceof HostileEntity hostile && hostile.isAlive() && hostile.canSee(client.player)) {
                return true;
            }
        }
        return false;
    }

    private boolean nearHome(AdaptiveMusicConfig config) {
        return homeAnchor != null && client.player.getBlockPos().isWithinDistance(homeAnchor, config.homeRadius);
    }

    private boolean nearSpawn(AdaptiveMusicConfig config) {
        BlockPos pos = client.player.getBlockPos();
        return Math.abs(pos.getX()) <= config.spawnRadius && Math.abs(pos.getZ()) <= config.spawnRadius;
    }

    private void switchMood(String mood, AdaptiveMusicConfig config) {
        currentMood = mood;
        ticksUntilSwitch = config.switchCooldownSeconds * 20;
        Optional<SoundEvent> sound = config.soundFor(mood);
        sound.ifPresent(event -> client.getSoundManager().play(PositionedSoundInstance.music(event)));
        toast("Adaptive Music", "Mood: " + mood + sound.map(event -> " (" + event.getId() + ")").orElse(""));
    }

    private void toast(String title, String message) {
        if (AdaptiveMusicConfig.get().showDebugToasts) {
            client.getToastManager().add(new SystemToast(SystemToast.Type.PERIODIC_NOTIFICATION, Text.literal(title), Text.literal(message)));
        }
    }
}
