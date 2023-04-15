package io.github.cottonmc.cotton.gui.client;

import com.mojang.blaze3d.vertex.PoseStack;
import dustw.libgui.mixin.ScreenAccessor;
import io.github.cottonmc.cotton.gui.GuiDescription;
import io.github.cottonmc.cotton.gui.impl.VisualLogger;
import io.github.cottonmc.cotton.gui.impl.client.CottonScreenImpl;
import io.github.cottonmc.cotton.gui.impl.client.FocusElements;
import io.github.cottonmc.cotton.gui.impl.client.MouseInputHandler;
import io.github.cottonmc.cotton.gui.impl.client.NarrationHelper;
import io.github.cottonmc.cotton.gui.widget.WPanel;
import io.github.cottonmc.cotton.gui.widget.WWidget;
import io.github.cottonmc.cotton.gui.widget.data.InputResult;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.opengl.GL11;

public class CottonClientScreen extends Screen implements CottonScreenImpl {
    private static final VisualLogger LOGGER = new VisualLogger(CottonInventoryScreen.class);
    protected GuiDescription description;
    protected int left = 0;
    protected int top = 0;

    /**
     * The X coordinate of the screen title.
     * This is relative to the root panel's top-left corner.
     *
     * @since 2.0.0
     */
    protected int titleX;

    /**
     * The Y coordinate of the screen title.
     * This is relative to the root panel's top-left corner.
     *
     * @since 2.0.0
     */
    protected int titleY;

    @Nullable
    protected WWidget lastResponder = null;

    private final MouseInputHandler<CottonClientScreen> mouseInputHandler = new MouseInputHandler<>(this);

    public CottonClientScreen(GuiDescription description) {
        this(CommonComponents.EMPTY, description);
    }

    public CottonClientScreen(Component title, GuiDescription description) {
        super(title);
        this.description = description;
        description.getRootPanel().validate(description);
    }

    @Override
    public GuiDescription getDescription() {
        return description;
    }

    @Override
    public void init() {
        super.init();

        WPanel root = description.getRootPanel();
        if (root != null) root.addPainters();
        description.addPainters();
        reposition(width, height);

        if (root != null) {
            GuiEventListener rootPanelElement = FocusElements.ofPanel(root);
            ((ScreenAccessor) this).libgui$getChildren().add(rootPanelElement);
            setInitialFocus(rootPanelElement);
        } else {
            LOGGER.warn("No root panel found, keyboard navigation disabled");
        }
    }

    @Override
    public void removed() {
        super.removed();
        VisualLogger.reset();
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
     * Repositions the root panel.
     *
     * @param screenWidth  the width of the screen
     * @param screenHeight the height of the screen
     */
    protected void reposition(int screenWidth, int screenHeight) {
        if (description != null) {
            WPanel root = description.getRootPanel();
            if (root != null) {
                titleX = description.getTitlePos().x();
                titleY = description.getTitlePos().y();

                if (!description.isFullscreen()) {
                    this.left = (screenWidth - root.getWidth()) / 2;
                    this.top = (screenHeight - root.getHeight()) / 2;
                } else {
                    this.left = 0;
                    this.top = 0;

                    root.setSize(screenWidth, screenHeight);
                }
            }
        }
    }

    private void paint(PoseStack matrices, int mouseX, int mouseY) {
        renderBackground(matrices);

        if (description != null) {
            WPanel root = description.getRootPanel();
            if (root != null) {
                GL11.glEnable(GL11.GL_SCISSOR_TEST);
                Scissors.refreshScissors();
                root.paint(matrices, left, top, mouseX - left, mouseY - top);
                GL11.glDisable(GL11.GL_SCISSOR_TEST);
                Scissors.checkStackIsEmpty();
            }

            if (getTitle() != null && description.isTitleVisible()) {
                int width = description.getRootPanel().getWidth();
                ScreenDrawing.drawString(matrices, getTitle().getVisualOrderText(), description.getTitleAlignment(), left + titleX, top + titleY, width - titleX, description.getTitleColor());
            }
        }
    }

    @Override
    public void render(PoseStack matrices, int mouseX, int mouseY, float partialTicks) {
        paint(matrices, mouseX, mouseY);

        super.render(matrices, mouseX, mouseY, partialTicks);

        if (description != null) {
            WPanel root = description.getRootPanel();
            if (root != null) {
                WWidget hitChild = root.hit(mouseX - left, mouseY - top);
                if (hitChild != null) hitChild.renderTooltip(matrices, left, top, mouseX - left, mouseY - top);
            }
        }

        VisualLogger.render(matrices);
    }

    @Override
    public void tick() {
        super.tick();
        if (description != null) {
            WPanel root = description.getRootPanel();
            if (root != null) {
                root.tick();
            }
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int mouseButton) {
        super.mouseClicked(mouseX, mouseY, mouseButton);

        int containerX = (int) mouseX - left;
        int containerY = (int) mouseY - top;
        mouseInputHandler.checkFocus(containerX, containerY);
        if (containerX < 0 || containerY < 0 || containerX >= width || containerY >= height) return true;
        mouseInputHandler.onMouseDown(containerX, containerY, mouseButton);

        return true;
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int mouseButton) {
        super.mouseReleased(mouseX, mouseY, mouseButton);

        int containerX = (int) mouseX - left;
        int containerY = (int) mouseY - top;
        mouseInputHandler.onMouseUp(containerX, containerY, mouseButton);

        return true;
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int mouseButton, double deltaX, double deltaY) {
        super.mouseDragged(mouseX, mouseY, mouseButton, deltaX, deltaY);

        int containerX = (int) mouseX - left;
        int containerY = (int) mouseY - top;
        mouseInputHandler.onMouseDrag(containerX, containerY, mouseButton, deltaX, deltaY);

        return true;
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double amount) {
        super.mouseScrolled(mouseX, mouseY, amount);

        int containerX = (int) mouseX - left;
        int containerY = (int) mouseY - top;
        mouseInputHandler.onMouseScroll(containerX, containerY, amount);

        return true;
    }

    @Override
    public void mouseMoved(double mouseX, double mouseY) {
        super.mouseMoved(mouseX, mouseY);

        int containerX = (int) mouseX - left;
        int containerY = (int) mouseY - top;
        mouseInputHandler.onMouseMove(containerX, containerY);
    }

    @Override
    public boolean charTyped(char ch, int keyCode) {
        WWidget focus = description.getFocus();
        if (focus != null && focus.onCharTyped(ch) == InputResult.PROCESSED) {
            return true;
        }

        return super.charTyped(ch, keyCode);
    }

    @Override
    public boolean keyPressed(int ch, int keyCode, int modifiers) {
        WWidget focus = description.getFocus();
        if (focus != null && focus.onKeyPressed(ch, keyCode, modifiers) == InputResult.PROCESSED) {
            return true;
        }

        return super.keyPressed(ch, keyCode, modifiers);
    }

    @Override
    public boolean keyReleased(int ch, int keyCode, int modifiers) {
        WWidget focus = description.getFocus();
        if (focus != null && focus.onKeyReleased(ch, keyCode, modifiers) == InputResult.PROCESSED) {
            return true;
        }

        return super.keyReleased(ch, keyCode, modifiers);
    }

    @Override
    public void renderTextHover(PoseStack matrices, @Nullable Style textStyle, int x, int y) {
        renderComponentHoverEffect(matrices, textStyle, x, y);
    }

    @Override
    protected void updateNarratedWidget(NarrationElementOutput builder) {
        if (description != null) NarrationHelper.addNarrations(description.getRootPanel(), builder);
    }
}
