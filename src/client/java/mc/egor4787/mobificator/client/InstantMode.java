package mc.egor4787.mobificator.client;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.LivingEntity;
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

    private final Attributes.Suggestions suggestions;

    private static final List<String> history = new ArrayList<>();
    private int historyIndex = -1;

    public InstantMode(Screen parent, LivingEntity target) {
        super(Component.literal("Mobificator Instant"));
        this.parent = parent;
        this.target = target;
        this.suggestions = new Attributes.Suggestions(target);
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

        suggestions.render(graphics, this.font, input);
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
            suggestions.complete(input);
            return true;
        }

        if (event.key() == GLFW.GLFW_KEY_ENTER
                || event.key() == GLFW.GLFW_KEY_KP_ENTER) {
            handleInput();
            return true;
        }

        suggestions.resetCompletion();
        return super.keyPressed(event);
    }

    private void handleInput() {
        String command = input.getValue().trim();

        if (command.isEmpty()) return;

        if (command.regionMatches(true, 0, "name ", 0, 5)) {
            String name = command.substring(5).trim();
            boolean alwaysVisible = name.toLowerCase().endsWith(" alwaysvisible");

            if (alwaysVisible) name = name.substring(0, name.length() - " alwaysVisible".length()).trim();

            Names.execute(minecraft, target, name, alwaysVisible);
        } else {
            String[] parts = command.split("\\s+", 2);
            if (parts.length != 2) return;
            Attributes.execute(minecraft, target, parts[0], parts[1]);
        }

        history.addFirst(command);
        historyIndex = -1;
        onClose();
    }

    @Override
    public void onClose() {
        this.minecraft.gui.setScreen(parent);
    }
}