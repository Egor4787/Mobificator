package mc.egor4787.mobificator.client.handlers;

import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.LivingEntity;

public final class Effects {
    private Effects() {}

    public static boolean execute(
            Minecraft client,
            LivingEntity target,
            String input
    ) {
        if (client.getConnection() == null || input.isBlank()) return false;

        String[] parts = input.trim().split("\\s+");
        String command = "execute as " + target.getUUID() + " run effect ";

        if (parts.length == 1 && parts[0].equalsIgnoreCase("clear")) {
            command += "clear @s";
        } else if (parts.length == 2 && parts[1].equalsIgnoreCase("clear")) {
            command += "clear @s " + parts[0];
        } else if (parts.length == 2) {
            command += "give @s " + parts[0] + " " + parts[1]   ;
        } else if (parts.length == 3) {
            command += "give @s " + parts[0] + " " + parts[1] + " " + parts[2];
        } else {
            return false;
        }

        client.getConnection().sendCommand(command);
        return true;
    }
}
