package mc.egor4787.mobificator.client.handlers;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public final class Attributes {
    private Attributes() {}

    private static List<AttributeInstance> getInstances(
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

    public static void execute(
            Minecraft client,
            LivingEntity target,
            String name,
            String value
    ) {
        if (client.getConnection() == null) return;

        for (AttributeInstance attribute : getInstances(target)) {
            String id = attribute.getAttribute().getRegisteredName();
            String shortName = getShortName(attribute);

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

    private static String getShortName(AttributeInstance attribute) {
        String id = attribute.getAttribute().getRegisteredName();
        return id.substring(id.indexOf(':') + 1);
    }

    public static final class Suggestions {
        private static final int MAX_SUGGESTIONS = 8;
        private static final int ROW_HEIGHT = 16;

        private final List<AttributeInstance> attributes;

        private List<String> matches = new ArrayList<>();
        private int index = -1;

        public Suggestions(LivingEntity target) {
            this.attributes = getInstances(target);
        }

        public void render(
                GuiGraphicsExtractor graphics,
                Font font,
                EditBox input
        ) {
            String search = input.getValue().trim().toLowerCase(Locale.ROOT);

            if (search.isEmpty() || search.contains(" ")) return;

            int y = input.getY() + input.getHeight() + 4;
            int count = 0;

            for (AttributeInstance attribute : attributes) {
                String name = getShortName(attribute);

                if (!name.toLowerCase(Locale.ROOT).contains(search)) continue;
                String content = name + " " + attribute.getBaseValue();
                int rowY = y + count * ROW_HEIGHT;

                graphics.text(
                        font,
                        content,
                        input.getX() + (input.getWidth() - font.width(content)) / 2,
                        rowY + 4,
                        0xFFFFFFFF,
                        true
                );

                if (++count >= MAX_SUGGESTIONS) break;
            }
        }

        public void complete(EditBox input) {
            String search = input.getValue().trim().toLowerCase(Locale.ROOT);

            if (search.isEmpty() || search.contains(" ")) return;

            if (matches.isEmpty()) {
                matches = attributes.stream()
                        .map(Attributes::getShortName)
                        .filter(name -> name
                                .toLowerCase(Locale.ROOT)
                                .contains(search))
                        .distinct()
                        .toList();

                index = -1;
            }

            if (matches.isEmpty()) return;

            index = (index + 1) % matches.size();
            input.setValue(matches.get(index));
        }

        public void resetCompletion() {
            matches = new ArrayList<>();
            index = -1;
        }
    }
}