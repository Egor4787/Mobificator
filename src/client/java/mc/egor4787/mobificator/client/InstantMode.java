package mc.egor4787.mobificator.client;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.List;

public class InstantMode extends Screen {

    private static final int TOP = 12;
    private static final int PANEL_HEIGHT = 18;
    private static final int PADDING_X = 8;
    private static final int PADDING_Y = 5;

    private final Screen parent;
    private final LivingEntity target;
    private EditBox input;

    private final List<AttributeInstance> attributes = new ArrayList<>();

    private List<String> completionMatches = new ArrayList<>();
    private int completionIndex = 0;
    private String completionPrefix = "";

    private static final List<String> history = new ArrayList<>();
    private int historyIndex = -1;

    public InstantMode(Screen parent, LivingEntity target) {
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

        for (AttributeInstance attribute : attributes) {
            String name = attribute.getAttribute().getRegisteredName();
            String shortName = name.substring(name.indexOf(':') + 1);

            if (!search.isEmpty() && !search.contains(" ")
                    && shortName.toLowerCase().startsWith(search)) {
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
        if (event.key() == GLFW.GLFW_KEY_UP || event.key() == GLFW.GLFW_KEY_PAGE_UP) {
            if (historyIndex < history.size() - 1) {
                input.setValue(history.get(++historyIndex));
            }
            return true;
        }

        if (event.key() == GLFW.GLFW_KEY_DOWN || event.key() == GLFW.GLFW_KEY_PAGE_DOWN) {
            if (historyIndex > 0) {
                input.setValue(history.get(--historyIndex));
            } else {
                historyIndex = -1;
                input.setValue("");
            }
            return true;
        }

        if (event.key() == GLFW.GLFW_KEY_TAB) {
            complete();
            return true;
        }

        if (event.key() == GLFW.GLFW_KEY_ENTER
                || event.key() == GLFW.GLFW_KEY_KP_ENTER) {
            handleInput();
            return true;
        }

        completionMatches = List.of();
        completionPrefix = "";

        return super.keyPressed(event);
    }

    private void complete() {
        String prefix = input.getValue().trim().toLowerCase();

        if (prefix.isEmpty() || prefix.contains(" ")) return;

        if (completionMatches.isEmpty()) {
            completionPrefix = prefix;
            completionMatches = attributes.stream()
                    .map(a -> a.getAttribute().getRegisteredName())
                    .map(name -> name.substring(name.indexOf(":") + 1))
                    .filter(name -> name.toLowerCase().startsWith(completionPrefix))
                    .distinct()
                    .toList();
            completionIndex = -1;
        }

        if (completionMatches.isEmpty()) return;

        completionIndex = (completionIndex + 1) % completionMatches.size();
        input.setValue(completionMatches.get(completionIndex));
    }

    private void handleInput() {
        String[] parts = input.getValue().trim().split("\\s+", 2);

        if (parts.length != 2) return;

        history.addFirst(input.getValue());
        historyIndex = -1;

        if (parts[0].equals("name")) {
            Names.execute(minecraft, target, parts[1]);
        } else {
            Attributes.execute(minecraft, target, attributes, parts[0], parts[1]);
        }
        onClose();
    }

    @Override
    public void onClose() {
        this.minecraft.gui.setScreen(parent);
    }
}