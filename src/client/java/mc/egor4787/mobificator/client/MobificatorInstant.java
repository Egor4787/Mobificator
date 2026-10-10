package mc.egor4787.mobificator.client;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import org.jspecify.annotations.NonNull;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class MobificatorInstant extends Screen {

    private static final int TOP = 12;
    private static final int PANEL_HEIGHT = 18;
    private static final int PADDING_X = 8;
    private static final int PADDING_Y = 5;

    private final Screen parent;
    private final LivingEntity target;
    private EditBox input;

    private final List<AttributeInstance> attributes = new ArrayList<>();
    private int completionIndex = 0;

    public MobificatorInstant(Screen parent, LivingEntity target) {
        super(Component.literal("Mobificator Instant"));
        this.parent = parent;
        this.target = target;
    }

    @Override
    protected void init() {
        int width = this.width / 3;
        int x = (this.width - width) / 2;

        input = new EditBox(
                this.font,
                x + PADDING_X,
                TOP + PADDING_Y,
                width - PADDING_X * 2,
                10,
                Component.empty()
        );

        input.setBordered(false);
        input.setTextColor(0xFFEAEAEA);
        input.setMaxLength(255);

        addRenderableWidget(input);
        setInitialFocus(input);

        attributes.clear();

        for (var holder : BuiltInRegistries.ATTRIBUTE.listElements().toList()) {
            AttributeInstance attribute = target.getAttribute(holder);

            if (attribute != null) {
                attributes.add(attribute);
            }
        }
    }

    @Override
    public void extractBackground(
            @NonNull GuiGraphicsExtractor graphics,
            int mouseX,
            int mouseY,
            float delta
    ) {
    }

    @Override
    public void extractRenderState(
            GuiGraphicsExtractor graphics,
            int mouseX,
            int mouseY,
            float delta
    ) {
        int width = this.width / 3;
        int x = (this.width - width) / 2;

        graphics.fill(
                x,
                TOP,
                x + width,
                TOP + PANEL_HEIGHT,
                0xFF454750
        );

        graphics.fill(
                x + 1,
                TOP + 1,
                x + width - 1,
                TOP + PANEL_HEIGHT - 1,
                0xFF191A20
        );

        graphics.text(
                this.font,
                target.getDisplayName(),
                8,
                8,
                0xFFFFFFFF,
                true
        );

        super.extractRenderState(graphics, mouseX, mouseY, delta);

        String search = input.getValue().trim().toLowerCase();
        int y = input.getY() + input.getHeight() + 4;
        int count = 0;

        for (AttributeInstance  attribute : attributes) {
            String name = attribute.getAttribute().getRegisteredName();
            String shortName = name.substring(name.indexOf(':') + 1);

            if (!search.isEmpty() && !search.contains(" ")
                && shortName.toLowerCase().contains(search)) {
                int rowY = y + count * 16;

                String content = shortName + " " + attribute.getBaseValue();

                graphics.text(this.font,
                        content,
                        input.getX() + (input.getWidth() - this.font.width(content)) / 2,
                        rowY + 4,
                        0xFFFffFFF,
                        true
                );
                if (++count == 5) break;
            }
        }
    }

    @Override
    public boolean keyPressed(KeyEvent event) {
        if (event.key() == GLFW.GLFW_KEY_TAB) {
            complete();
            return true;
        }

        if (event.key() == GLFW.GLFW_KEY_ENTER
                || event.key() == GLFW.GLFW_KEY_KP_ENTER) {
            execute();
            return true;
        }

        return super.keyPressed(event);
    }

    private void complete() {
        String prefix = input.getValue().trim().toLowerCase();

        if (prefix.isEmpty() || prefix.contains(" ")) return;

        List<String> matches = attributes.stream()
                .map(a -> a.getAttribute().getRegisteredName())
                .map(name -> name.substring(name.indexOf(":") + 1))
                .filter(name -> name.toLowerCase().startsWith(prefix))
                .distinct()
                .toList();

        if (matches.isEmpty()) return;

        input.setValue(matches.get(completionIndex++ % matches.size()));
    }

    private void execute() {
        if (minecraft.getConnection() == null) return;

        String[] parts = input.getValue().trim().split("\\s+");

        if (parts.length != 2) return;

        for (AttributeInstance attribute : attributes) {
            String name = attribute.getAttribute().getRegisteredName();
            String shortName = name.substring(name.indexOf(':') + 1);

            if (!parts[0].equalsIgnoreCase(shortName)
                    && !parts[0].equalsIgnoreCase(name)) {
                continue;
            }

            String command = "attribute " + target.getUUID() + " " + name + " base ";

            if (parts[1].equalsIgnoreCase("reset")) {
                Objects.requireNonNull(minecraft.getConnection()).sendCommand(command + "reset");
                onClose();
                return;
            }

            try {
                double value = Double.parseDouble(parts[1]);

                if (!Double.isFinite(value) || minecraft.getConnection() == null) return;

                minecraft.getConnection().sendCommand(
                        command + "set " + value
                );

                onClose();
            } catch (NumberFormatException ignored) {
            }

            return;
        }
    }

    @Override
    public void onClose() {
        this.minecraft.gui.setScreen(parent);
    }
}