package mc.egor4787.mobificator.client.handlers;

import net.minecraft.client.Minecraft;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;

import java.util.ArrayList;
import java.util.List;

public final class Attributes {
    private Attributes() {}

    public static List<AttributeInstance> getInstances(
            LivingEntity target
    ) {
        List<AttributeInstance> attributes = new ArrayList<>();

        for (var holder : BuiltInRegistries.ATTRIBUTE.listElements().toList()) {
            AttributeInstance attribute = target.getAttribute(holder);

            if (attribute != null) {
                attributes.add(attribute);
            }
        }

        return attributes;
    }

    public static boolean execute(
            Minecraft client,
            LivingEntity target,
            String name,
            String value
    ) {
        if (client.getConnection() == null) return false;

        for (AttributeInstance attribute : getInstances(target)) {
            String id = attribute.getAttribute().getRegisteredName();
            String shortName = getShortName(attribute);

            if (!name.equalsIgnoreCase(shortName) && !name.equalsIgnoreCase(id)) continue;

            String command = "attribute " + target.getUUID() + " " + id + " base ";

            if (value.equalsIgnoreCase("reset")) {
                client.getConnection().sendCommand(command + "reset");
                return true;
            }

            try {
                double number = Double.parseDouble(value);

                if (!Double.isFinite(number)) return false;

                client.getConnection().sendCommand(
                        command + "set " + number
                );
                return true;
            } catch (NumberFormatException ignored) {
                return false;
            }
        }
        return false;
    }

    private static String getShortName(AttributeInstance attribute) {
        String id = attribute.getAttribute().getRegisteredName();
        return id.substring(id.indexOf(':') + 1);
    }
}