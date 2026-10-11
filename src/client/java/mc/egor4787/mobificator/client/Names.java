package mc.egor4787.mobificator.client;

import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.LivingEntity;

public class Names {
    public static void execute(
            Minecraft client,
            LivingEntity target,
            String name
    ) {
        if (client.getConnection() == null || name.isBlank()) return;

        client.getConnection().sendCommand(
                "execute as " + target.getUUID() + " run data merge entity @s {CustomName:'" + name + "'}"
        );
    }
}
