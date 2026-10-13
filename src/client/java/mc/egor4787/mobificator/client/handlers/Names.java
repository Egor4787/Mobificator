package mc.egor4787.mobificator.client.handlers;

import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.LivingEntity;

public class Names {
    private Names() {}

    public static boolean execute(
            Minecraft client,
            LivingEntity target,
            String name,
            boolean alwaysVisible
    ) {
        if (client.getConnection() == null || name.isBlank()) return false;

        String command = "execute as " + target.getUUID() + " run data merge entity @s {CustomName:\"" + name + "\"";

        if (alwaysVisible) {
            command += ",CustomNameVisible:1b";
        }

        command += "}";

        client.getConnection().sendCommand(command);
        return true;
    }
}
