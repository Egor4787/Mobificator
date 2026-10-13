package mc.egor4787.mobificator.client.modes;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public class LabMode extends Screen {
    private final Screen parent;

    public LabMode(Screen parent) {
        super(Component.literal("Mobificator Lab"));

        this.parent = parent;
    }

    @Override
    public void extractRenderState(
            GuiGraphicsExtractor graphics,
            int mouseX,
            int mouseY,
            float delta
    ) {
        String label = "Mobificator Lab is coming soon";

        graphics.text(
                this.font,
                label,
                (this.width - font.width(label)) / 2,
                40,
                0xFFFFFFFF,
                true
        );

        super.extractRenderState(graphics, mouseX, mouseY, delta);
    }

    @Override
    public void onClose() {
        this.minecraft.gui.setScreen(parent);
    }
}
