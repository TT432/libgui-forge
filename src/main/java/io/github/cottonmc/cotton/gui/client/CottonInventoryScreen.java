package io.github.cottonmc.cotton.gui.client;

import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.vertex.PoseStack;
import io.github.cottonmc.cotton.gui.GuiDescription;
import io.github.cottonmc.cotton.gui.SyncedGuiDescription;
import io.github.cottonmc.cotton.gui.impl.VisualLogger;
import io.github.cottonmc.cotton.gui.impl.client.CottonScreenImpl;
import io.github.cottonmc.cotton.gui.impl.client.MouseInputHandler;
import io.github.cottonmc.cotton.gui.impl.client.NarrationHelper;
import io.github.cottonmc.cotton.gui.widget.WPanel;
import io.github.cottonmc.cotton.gui.widget.WWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL11;

/**
 * A screen for a {@link SyncedGuiDescription}.
 *
 * @param <T> the description type
 */
public class CottonInventoryScreen<T extends SyncedGuiDescription> extends AbstractContainerScreen<T> implements CottonScreenImpl {
    protected SyncedGuiDescription description;
    @Nullable
    protected WWidget lastResponder = null;
    private final MouseInputHandler<CottonInventoryScreen<T>> mouseInputHandler = new MouseInputHandler<>(this);

    /**
     * Constructs a new screen without a title.
     *
     * @param description the GUI description
     * @param inventory   the player inventory
     * @since 5.2.0
     */
    public CottonInventoryScreen(T description, Inventory inventory) {
        this(description, inventory, new TextComponent(""));
    }

    /**
     * Constructs a new screen.
     *
     * @param description the GUI description
     * @param inventory   the player inventory
     * @param title       the screen title
     * @since 5.2.0
     */
    public CottonInventoryScreen(T description, Inventory inventory, Component title) {
        super(description, inventory, title);
        this.description = description;
        width = 18 * 9;
        height = 18 * 9;
        this.imageWidth = 18 * 9;
        this.imageHeight = 18 * 9;
        description.getRootPanel().validate(description);
    }

    /**
     * Constructs a new screen without a title.
     *
     * @param description the GUI description
     * @param player      the player
     */
    public CottonInventoryScreen(T description, Player player) {
        this(description, player.getInventory());
    }

    /**
     * Constructs a new screen.
     *
     * @param description the GUI description
     * @param player      the player
     * @param title       the screen title
     */
    public CottonInventoryScreen(T description, Player player, Component title) {
        this(description, player.getInventory(), title);
    }

    /*
     * RENDERING NOTES:
     *
     * * "width" and "height" are the width and height of the overall screen
     * * "backgroundWidth" and "backgroundHeight" are the width and height of the panel to render
     * * ~~"left" and "top" are *actually* self-explanatory~~
     *   * "left" and "top" are now (1.15) "x" and "y". A bit less self-explanatory, I guess.
     * * coordinates start at 0,0 at the topleft of the screen.
     */

    @Override
    public void init() {
        super.init();

        WPanel root = description.getRootPanel();
        if (root != null) root.addPainters();
        description.addPainters();

        reposition(width, height);
    }

    @Override
    public void removed() {
        super.removed();
        VisualLogger.reset();
    }

    @ApiStatus.Internal
    @Override
    public GuiDescription getDescription() {
        return description;
    }

    @Nullable
    @Override
    public WWidget getLastResponder() {
        return lastResponder;
    }

    @Override
    public void setLastResponder(@Nullable WWidget lastResponder) {
        this.lastResponder = lastResponder;
    }

    /**
     * Clears the heavyweight peers of this screen's GUI description.
     */
    private void clearPeers() {
        description.slots.clear();
    }

    /**
     * Repositions the root panel.
     *
     * @param screenWidth  the width of the screen
     * @param screenHeight the height of the screen
     */
    protected void reposition(int screenWidth, int screenHeight) {
        WPanel basePanel = description.getRootPanel();
        if (basePanel != null) {
            clearPeers();
            basePanel.validate(description);

            imageWidth = basePanel.getWidth();
            imageHeight = basePanel.getHeight();

            //DEBUG
            if (imageWidth < 16) imageWidth = 300;
            if (imageHeight < 16) imageHeight = 300;
        }

        titleLabelX = description.getTitlePos().x();
        titleLabelY = description.getTitlePos().y();

        if (!description.isFullscreen()) {
            leftPos = (screenWidth / 2) - (imageWidth / 2);
            topPos = (screenHeight / 2) - (imageHeight / 2);
        } else {
            leftPos = 0;
            topPos = 0;

            if (basePanel != null) {
                basePanel.setSize(screenWidth, screenHeight);
            }
        }
    }

    @Override
    public boolean isPauseScreen() {
        //...yeah, we're going to go ahead and override that.
        return false;
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int mouseButton) {
        super.mouseClicked(mouseX, mouseY, mouseButton);

        int containerX = (int) mouseX - leftPos;
        int containerY = (int) mouseY - topPos;
        mouseInputHandler.checkFocus(containerX, containerY);
        if (containerX < 0 || containerY < 0 || containerX >= width || containerY >= height) return true;
        mouseInputHandler.onMouseDown(containerX, containerY, mouseButton);

        return true;
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int mouseButton) {
        super.mouseReleased(mouseX, mouseY, mouseButton);

        int containerX = (int) mouseX - leftPos;
        int containerY = (int) mouseY - topPos;
        mouseInputHandler.onMouseUp(containerX, containerY, mouseButton);

        return true;
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int mouseButton, double deltaX, double deltaY) {
        super.mouseDragged(mouseX, mouseY, mouseButton, deltaX, deltaY);

        int containerX = (int) mouseX - leftPos;
        int containerY = (int) mouseY - topPos;
        mouseInputHandler.onMouseDrag(containerX, containerY, mouseButton, deltaX, deltaY);

        return true;
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double amount) {
        super.mouseScrolled(mouseX, mouseY, amount);

        int containerX = (int) mouseX - leftPos;
        int containerY = (int) mouseY - topPos;
        mouseInputHandler.onMouseScroll(containerX, containerY, amount);

        return true;
    }

    @Override
    public void mouseMoved(double mouseX, double mouseY) {
        super.mouseMoved(mouseX, mouseY);

        int containerX = (int) mouseX - leftPos;
        int containerY = (int) mouseY - topPos;
        mouseInputHandler.onMouseMove(containerX, containerY);
    }

    @Override
    public boolean charTyped(char ch, int keyCode) {
        if (description.getFocus() == null) return super.charTyped(ch, keyCode);
        description.getFocus().onCharTyped(ch);
        return true;
    }

    @Override
    public boolean keyPressed(int ch, int keyCode, int modifiers) {
        if (ch == GLFW.GLFW_KEY_ESCAPE || ch == GLFW.GLFW_KEY_TAB) {
            // special hardcoded keys, these will never be delivered to widgets
            return super.keyPressed(ch, keyCode, modifiers);
        } else {
            if (description.getFocus() == null) return super.keyPressed(ch, keyCode, modifiers);
            description.getFocus().onKeyPressed(ch, keyCode, modifiers);
            return true;
        }
    }

    @Override
    public boolean keyReleased(int ch, int keyCode, int modifiers) {
        if (description.getFocus() == null) return super.keyReleased(ch, keyCode, modifiers);
        description.getFocus().onKeyReleased(ch, keyCode, modifiers);
        return true;
    }

    @Override
    protected void renderBg(PoseStack matrices, float partialTicks, int mouseX, int mouseY) {
    } //This is just an AbstractContainerScreen thing; most Screens don't work this way.

    private void paint(PoseStack matrices, int mouseX, int mouseY) {
        renderBackground(matrices);

        if (description != null) {
            WPanel root = description.getRootPanel();
            if (root != null) {
                GL11.glEnable(GL11.GL_SCISSOR_TEST);
                Scissors.refreshScissors();
                root.paint(matrices, leftPos, topPos, mouseX - leftPos, mouseY - topPos);
                GL11.glDisable(GL11.GL_SCISSOR_TEST);
                Scissors.checkStackIsEmpty();
            }
        }
    }

    @Override
    public void render(PoseStack matrices, int mouseX, int mouseY, float partialTicks) {
        paint(matrices, mouseX, mouseY);

        super.render(matrices, mouseX, mouseY, partialTicks);
        Lighting.setupForFlatItems(); //Needed because super.render leaves dirty state

        if (description != null) {
            WPanel root = description.getRootPanel();
            if (root != null) {
                WWidget hitChild = root.hit(mouseX - leftPos, mouseY - topPos);
                if (hitChild != null)
                    hitChild.renderTooltip(matrices, leftPos, topPos, mouseX - leftPos, mouseY - topPos);
            }
        }

        renderTooltip(matrices, mouseX, mouseY); //Draws the itemstack tooltips
        VisualLogger.render(matrices);
    }

    @Override
    protected void renderLabels(PoseStack matrices, int mouseX, int mouseY) {
        if (description != null && description.isTitleVisible()) {
            int width = description.getRootPanel().getWidth();
            ScreenDrawing.drawString(matrices, getTitle().getVisualOrderText(), description.getTitleAlignment(), titleLabelX, titleLabelY, width - titleLabelX, description.getTitleColor());
        }

        // Don't draw the player inventory label as it's drawn by the widget itself
    }

    @Override
    protected void containerTick() {
        super.containerTick();
        if (description != null) {
            WPanel root = description.getRootPanel();
            if (root != null) {
                root.tick();
            }
        }
    }

    @Override
    public void renderTextHover(PoseStack matrices, @Nullable Style textStyle, int x, int y) {
        renderComponentHoverEffect(matrices, textStyle, x, y);
    }

    @Override
    public boolean changeFocus(boolean lookForwards) {
        if (description != null) {
            description.cycleFocus(lookForwards);
        }

        return true;
    }

    @Override
    protected void updateNarratedWidget(NarrationElementOutput builder) {
        if (description != null) NarrationHelper.addNarrations(description.getRootPanel(), builder);
    }
}
