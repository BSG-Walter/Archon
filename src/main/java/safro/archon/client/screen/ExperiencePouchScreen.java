package safro.archon.client.screen;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import safro.archon.Archon;

import java.util.List;

@Environment(EnvType.CLIENT)
public class ExperiencePouchScreen extends HandledScreen<ExperiencePouchScreenHandler> {
    public static final Identifier TEXTURE = new Identifier(Archon.MODID, "textures/gui/experience_pouch.png");
    private static final Text ADD_TEXT = Text.translatable("container.archon.add_xp");
    private static final Text REMOVE_TEXT = Text.translatable("container.archon.remove_xp");
    private final List<ExperienceButtonWidget> buttons = Lists.newArrayList();

    public ExperiencePouchScreen(ExperiencePouchScreenHandler handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, title);
        this.backgroundWidth = 230;
        this.backgroundHeight = 196;
    }

    protected void init() {
        super.init();
        this.buttons.clear();

        // Add buttons - Top row
        for (int i = 0; i < 4; i++) {
            int value = (int) Math.pow(10, i);
            this.addButton(new ExperienceButtonWidget(this.x + (14 + (i * 24)), this.y + 30, value, Text.of(Integer.toString(value)), ExperienceButtonWidget.ButtonType.ADD, this.textRenderer));
        }

        // Add buttons - Bottom row
        this.addButton(new ExperienceButtonWidget(this.x + 14, this.y + 55, 1, Text.of("Lvl"), ExperienceButtonWidget.ButtonType.ADD_LEVEL, this.textRenderer));
        this.addButton(new ExperienceButtonWidget(this.x + 38, this.y + 55, 0, Text.of("All"), ExperienceButtonWidget.ButtonType.ADD_ALL, this.textRenderer));

        // Remove buttons - Top row
        for (int i = 0; i < 4; i++) {
            int value = (int) Math.pow(10, i);
            this.addButton(new ExperienceButtonWidget(this.x + (120 + (i * 24)), this.y + 30, value, Text.of(Integer.toString(value)), ExperienceButtonWidget.ButtonType.REMOVE, this.textRenderer));
        }

        // Remove buttons - Bottom row
        this.addButton(new ExperienceButtonWidget(this.x + 120 + 24*2, this.y + 55, 1, Text.of("Lvl"), ExperienceButtonWidget.ButtonType.REMOVE_LEVEL, this.textRenderer));
        this.addButton(new ExperienceButtonWidget(this.x + 120 + 24*3, this.y + 55, 0, Text.of("All"), ExperienceButtonWidget.ButtonType.REMOVE_ALL, this.textRenderer));
    }

    private <T extends ExperienceButtonWidget> void addButton(T button) {
        this.addDrawableChild(button);
        this.buttons.add(button);
    }

    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        this.renderBackground(context);
        super.render(context, mouseX, mouseY, delta);
        this.drawMouseoverTooltip(context, mouseX, mouseY);
    }

    @Override
    protected void drawBackground(DrawContext context, float delta, int mouseX, int mouseY) {
        RenderSystem.setShader(GameRenderer::getPositionTexProgram);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        int i = (this.width - this.backgroundWidth) / 2;
        int j = (this.height - this.backgroundHeight) / 2;
        context.drawTexture(TEXTURE, i, j, 0, 0, this.backgroundWidth, this.backgroundHeight);
    }

    protected void drawForeground(DrawContext context, int mouseX, int mouseY) {
        context.drawCenteredTextWithShadow(this.textRenderer, ADD_TEXT, 58, 13, 14737632);
        context.drawCenteredTextWithShadow(this.textRenderer, REMOVE_TEXT, 170, 15, 14737632);
        Text xp = Text.literal(this.handler.getExperience() + "/" + this.handler.getMaxExperience());
        context.drawCenteredTextWithShadow(this.textRenderer, xp, 116, 85, 14737632);
    }
}
