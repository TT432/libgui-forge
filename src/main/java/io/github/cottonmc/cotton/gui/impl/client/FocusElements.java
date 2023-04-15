package io.github.cottonmc.cotton.gui.impl.client;

import io.github.cottonmc.cotton.gui.widget.WPanel;
import io.github.cottonmc.cotton.gui.widget.WWidget;
import io.github.cottonmc.cotton.gui.widget.data.Rect2i;
import io.github.cottonmc.cotton.gui.widget.focus.Focus;
import io.github.cottonmc.cotton.gui.widget.focus.FocusModel;
import net.minecraft.client.gui.ComponentPath;
import net.minecraft.client.gui.components.events.AbstractContainerEventHandler;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.navigation.FocusNavigationEvent;
import net.minecraft.client.gui.navigation.ScreenRectangle;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public final class FocusElements {
    public static PanelFocusElement ofPanel(WPanel panel) {
        PanelFocusElement result = new PanelFocusElement(panel);
        result.refreshChildren();
        return result;
    }

    public static Stream<FocusElement<?>> toElements(WWidget widget) {
        if (widget instanceof WPanel panel) {
            return Stream.of(ofPanel(panel));
        } else {
            return fromFoci(widget);
        }
    }

    private static Stream<FocusElement<?>> fromFoci(WWidget widget) {
        @Nullable FocusModel<?> focusModel = widget.getFocusModel();
        if (focusModel == null) return Stream.empty();

        return focusModel.foci().map(focus -> new LeafFocusElement(widget, focus));
    }

    public sealed interface FocusElement<W extends WWidget> extends GuiEventListener {
        W widget();
    }

    private record LeafFocusElement(WWidget widget, Focus<?> focus) implements FocusElement<WWidget> {
        @SuppressWarnings("unchecked")
        @Override
        public void setFocused(boolean focused) {
            if (focused) {
                Focus<?> focus = focus();

                if (focus != null) {
                    widget.requestFocus();
                    ((FocusModel<Object>) widget.getFocusModel()).setFocused((Focus<Object>) focus);
                }
            } else {
                widget.releaseFocus();
            }
        }

        @SuppressWarnings("unchecked")
        @Override
        public boolean isFocused() {
            if (widget.isFocused()) {
                FocusModel<Object> focusModel = (FocusModel<Object>) widget.getFocusModel();
                if (focusModel != null) {
                    return focusModel.isFocused((Focus<Object>) focus);
                }
            }

            return false;
        }

        @Override
        public ScreenRectangle getRectangle() {
            Rect2i area = focus.area();
            return new ScreenRectangle(
                    widget.getAbsoluteX() + area.x(),
                    widget.getAbsoluteY() + area.y(),
                    area.width(), area.height()
            );
        }

        @Override
        public @Nullable ComponentPath nextFocusPath(FocusNavigationEvent navigation) {
            return widget.canFocus() && !isFocused() ? ComponentPath.leaf(this) : null;
        }
    }

    private static final class PanelFocusElement extends AbstractContainerEventHandler implements FocusElement<WPanel> {
        private final List<FocusElement<?>> children = new ArrayList<>();
        private final WPanel widget;
        private List<WWidget> childWidgets;

        private PanelFocusElement(WPanel widget) {
            this.widget = widget;
        }

        private void refreshChildren() {
            boolean shouldRefresh = false;
            if (childWidgets == null) {
                childWidgets = widget.streamChildren().toList();
                shouldRefresh = true;
            } else {
                List<WWidget> currentChildren = widget.streamChildren().toList();
                if (!childWidgets.equals(currentChildren)) {
                    childWidgets = currentChildren;
                    shouldRefresh = true;
                }
            }

            if (shouldRefresh) {
                children.clear();
                fromFoci(widget).forEach(children::add);
                childWidgets.stream()
                        .flatMap(FocusElements::toElements)
                        .forEach(children::add);
                refreshFocus();
            }
        }

        @Override
        public List<FocusElement<?>> children() {
            refreshChildren();
            return children;
        }

        @Override
        public WPanel widget() {
            return widget;
        }

        @Override
        public @Nullable GuiEventListener getFocused() {
            refreshFocus();
            return super.getFocused();
        }

        public void refreshFocus() {
            if (children.isEmpty()) return;

            boolean foundFocus = false;
            for (FocusElement<?> child : children) {
                if (child instanceof PanelFocusElement panel) {
                    panel.refreshFocus();
                }

                if (!foundFocus && child.isFocused()) {
                    setFocused(child);
                    foundFocus = true;
                }
            }
        }
    }
}
