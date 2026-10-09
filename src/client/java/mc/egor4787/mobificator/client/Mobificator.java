package mc.egor4787.mobificator.client;

import com.mojang.blaze3d.platform.InputConstants;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keymapping.v1.KeyMappingHelper;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.phys.EntityHitResult;

public class Mobificator implements ClientModInitializer {
    private static final KeyMapping.Category MOBIFICATOR_CATEGORY =
            KeyMapping.Category.register(
                    Identifier.parse("mobificator:keybindings")
            );

    private static final KeyMapping OPEN_LAB = KeyMappingHelper.registerKeyMapping(
            new KeyMapping(
                    "Open Lab",
                    InputConstants.Type.KEYSYM,
                    InputConstants.KEY_M,
                    MOBIFICATOR_CATEGORY
            )
    );

    private static final KeyMapping OPEN_INSTANT = KeyMappingHelper.registerKeyMapping(
            new KeyMapping(
                    "Open Instant",
                    InputConstants.Type.KEYSYM,
                    InputConstants.KEY_I,
                    MOBIFICATOR_CATEGORY
            )
    );

    @Override
    public void onInitializeClient() {
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (OPEN_LAB.consumeClick()) {
                openMobificatorLab(client);
            }

            while (OPEN_INSTANT.consumeClick()) {
                openMobificatorInstant(client);
            }
        });
    }

	private static void openMobificatorLab(Minecraft client) {
		client.setScreenAndShow(new MobificatorLabScreen(
                client.gui.screen()
        ));
	}

    private static void openMobificatorInstant(Minecraft client) {
        if (client.player == null) return;

        LivingEntity target = client.player;

        if (client.hitResult instanceof EntityHitResult hit
            && hit.getEntity() instanceof Mob mob) {
            target = mob;
        }

        client.setScreenAndShow(new MobificatorInstant(client.gui.screen(), target));
    }
}