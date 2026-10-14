package mc.egor4787.mobificator.client.handlers;

import net.minecraft.client.Minecraft;

public class Time {
    private Time() {}

    public static boolean execute(
            Minecraft client,
            String input
    ) {
        if (client.getConnection() == null || input.isBlank()) return false;

        String command = "time set " + input;

        client.getConnection().sendCommand(command);
        return true;
    }
}
