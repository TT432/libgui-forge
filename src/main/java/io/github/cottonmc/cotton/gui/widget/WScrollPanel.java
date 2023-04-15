package io.github.cottonmc.cotton.gui.widget;

import com.mojang.blaze3d.vertex.PoseStack;
import dustw.libgui.util.TriState;
import io.github.cottonmc.cotton.gui.GuiDescription;
import io.github.cottonmc.cotton.gui.widget.data.Axis;
import io.github.cottonmc.cotton.gui.widget.data.InputResult;

/**
 * Similar to the JScrollPane in Swing, this widget represents a scrollable widget.
 *
 * @since 2.0.0
 */
public class WScrollPanel extends WClippedPanel {
    protected int scrollBarSize = 8;
    protected final WWidget widget;

    protected TriState scrollingHorizontally = TriState.DEFAULT;
    protected TriState scrollingVertically = TriState.DEFAULT;

    /**
     * The horizontal scroll bar of this panel.
     */
    protected WScrollBar horizontalScrollBar;

    /**
     * The vertical scroll bar of this panel.
     */
    protected WScrollBar verticalScrollBar;

    protected int lastHorizontalScroll = -1;
    protected int lastVerticalScroll = -1;

    /**
     * Creates a vertically scrolling panel.
     *
     * @param widget the viewed widget
     */
    public WScrollPanel(WWidget widget) {
        this.widget = widget;

        widget.setParent(this);
        horizontalScrollBar = new WScrollBar(Axis.HORIZONTAL);
        verticalScrollBar = new WScrollBar(Axis.VERTICAL);
        horizontalScrollBar.setParent(this);
        verticalScrollBar.setParent(this);

        children.add(widget);
        children.add(verticalScrollBar); // Only vertical scroll bar
    }

    /**
     * Returns whether this scroll panel has a horizontal scroll bar.
     *
     * @return true if there is a horizontal scroll bar,
     * default if a scroll bar should be added if needed,
     * and false otherwise
     */
    public TriState isScrollingHorizontally() {
        return scrollingHorizontally;
    }

    public WScrollPanel setScrollingHorizontally(TriState scrollingHorizontally) {
        if (scrollingHorizontally != this.scrollingHorizontally) {
            this.scrollingHorizontally = scrollingHorizontally;
            layout();
        }

        return this;
    }

    /**
     * Returns whether this scroll panel has a vertical scroll bar.
     *
     * @return true if there is a vertical scroll bar,
     * *         default if a scroll bar should be added if needed,
     * *         and false otherwise
     */
    public TriState isScrollingVertically() {
        return scrollingVertically;
    }

    public WScrollPanel setScrollingVertically(TriState scrollingVertically) {
        if (scrollingVertically != this.scrollingVertically) {
            this.scrollingVertically = scrollingVertically;
            layout();
        }

        return this;
    }


    @Override
    public void paint(PoseStack matrices, int x, int y, int mouseX, int mouseY) {
        if (verticalScrollBar.getValue() != lastVerticalScroll || horizontalScrollBar.getValue() != lastHorizontalScroll) {
            layout();
            lastHorizontalScroll = horizontalScrollBar.getValue();
            lastVerticalScroll = verticalScrollBar.getValue();
        }

        super.paint(matrices, x, y, mouseX, mouseY);
    }

    @Override
    public void layout() {
        children.clear();

        boolean horizontal = hasHorizontalScrollbar();
        boolean vertical = hasVerticalScrollbar();

        int offset = (horizontal && vertical) ? scrollBarSize : 0;
        verticalScrollBar.setSize(scrollBarSize, this.height - offset);
        verticalScrollBar.setLocation(this.width - verticalScrollBar.getWidth(), 0);
        horizontalScrollBar.setSize(this.width - offset, scrollBarSize);
        horizontalScrollBar.setLocation(0, this.height - horizontalScrollBar.getHeight());

        if (widget instanceof WPanel) ((WPanel) widget).layout();
        children.add(widget);
        int x = horizontal ? -horizontalScrollBar.getValue() : 0;
        int y = vertical ? -verticalScrollBar.getValue() : 0;
        widget.setLocation(x, y);

        verticalScrollBar.setWindow(this.height - (horizontal ? scrollBarSize : 0));
        verticalScrollBar.setMaxValue(widget.getHeight());
        horizontalScrollBar.setWindow(this.width - (vertical ? scrollBarSize : 0));
        horizontalScrollBar.setMaxValue(widget.getWidth());

        if (vertical) children.add(verticalScrollBar);
        if (horizontal) children.add(horizontalScrollBar);
    }

    private boolean hasHorizontalScrollbar() {
        return (scrollingHorizontally == TriState.DEFAULT)
                ? (widget.width > this.width - scrollBarSize)
                : scrollingHorizontally.get();
    }

    private boolean hasVerticalScrollbar() {
        return (scrollingVertically == TriState.DEFAULT)
                ? (widget.height > this.height - scrollBarSize)
                : scrollingVertically.get();
    }

    @Override
    public InputResult onMouseScroll(int x, int y, double amount) {
        if (hasVerticalScrollbar()) {
            return verticalScrollBar.onMouseScroll(0, 0, amount);
        }

        return InputResult.IGNORED;
    }

    @Override
    public void validate(GuiDescription c) {
        //you have to validate these ones manually since they are not in children list
        this.horizontalScrollBar.validate(c);
        this.verticalScrollBar.validate(c);
        super.validate(c);
    }
}
