package mc.egor4787.mobificator.client.configuration;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import org.jspecify.annotations.NonNull;

public class ConfigScreen extends Screen {
    private final Screen parent;

    public ConfigScreen(Screen parent) {
        super(Component.literal("Mobificator"));
        this.parent = parent;
    }

    @Override
    protected void init() {
        int centerX = this.width / 2;

        addRenderableWidget(
                Button.builder(
                                Component.translatable("gui.done"),
                                _ -> onClose()
                        )
                        .bounds(centerX - 100, this.height - 40, 200, 20)
                        .build()
        );
    }

    @Override
    public void extractRenderState(
            @NonNull GuiGraphicsExtractor graphics,
            int mouseX,
            int mouseY,
            float delta
    ) {
        super.extractRenderState(graphics, mouseX, mouseY, delta);

        Component text = Component.literal("Configuration is coming soon");

        graphics.text(
                this.font,
                text,
                (this.width - this.font.width(text)) / 2,
                40,
                0xFFFFFFFF,
                true
        );
    }

    @Override
    public void onClose() {
        this.minecraft.gui.setScreen(parent);
    }
}
