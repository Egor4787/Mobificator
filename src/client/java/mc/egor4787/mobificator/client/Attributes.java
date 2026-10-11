package mc.egor4787.mobificator.client;

import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;

import java.util.List;

public class Attributes {
    public static void execute(
            Minecraft client,
            LivingEntity target,
            List<AttributeInstance> attributes,
            String name,
            String value
    ) {
        if (client.getConnection() == null) return;

        for (AttributeInstance attribute : attributes) {
            String id = attribute.getAttribute().getRegisteredName();
            String shortName = id.substring(id.indexOf(':') + 1);

            if (!name.equalsIgnoreCase(shortName) && !name.equalsIgnoreCase(id)) continue;

            String command = "attribute " + target.getUUID() + " " + id + " base ";

            if (value.equalsIgnoreCase("reset")) {
                client.getConnection().sendCommand(command + "reset");
                return;
            }

            try {
                double number = Double.parseDouble(value);

                if (!Double.isFinite(number)) return;

                client.getConnection().sendCommand(
                        command + "set " + number
                );
            } catch (NumberFormatException ignored) {}

            return;
        }
    }
}