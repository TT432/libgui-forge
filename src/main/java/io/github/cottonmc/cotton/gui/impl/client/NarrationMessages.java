package io.github.cottonmc.cotton.gui.impl.client;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;

public final class NarrationMessages {
    public static final String ITEM_SLOT_TITLE_KEY = "widget.libgui.item_slot.narration.title";
    public static final String LABELED_SLIDER_TITLE_KEY = "widget.libgui.labeled_slider.narration.title";
    public static final Component SCROLL_BAR_TITLE = new TranslatableComponent("widget.libgui.scroll_bar.narration.title");
    public static final String SLIDER_MESSAGE_KEY = "widget.libgui.slider.narration.title";
    public static final Component SLIDER_USAGE = new TranslatableComponent("widget.libgui.slider.narration.usage");
    public static final String TAB_TITLE_KEY = "widget.libgui.tab.narration.title";
    public static final String TAB_POSITION_KEY = "widget.libgui.tab.narration.position";
    public static final String TEXT_FIELD_TITLE_KEY = "widget.libgui.text_field.narration.title";
    public static final String TEXT_FIELD_SUGGESTION_KEY = "widget.libgui.text_field.narration.suggestion";
    public static final String TOGGLE_BUTTON_NAMED_KEY = "widget.libgui.toggle_button.narration.named";
    public static final Component TOGGLE_BUTTON_OFF = new TranslatableComponent("widget.libgui.toggle_button.narration.off");
    public static final Component TOGGLE_BUTTON_ON = new TranslatableComponent("widget.libgui.toggle_button.narration.on");
    public static final String TOGGLE_BUTTON_UNNAMED_KEY = "widget.libgui.toggle_button.narration.unnamed";

    public static final class Vanilla {
        public static final Component BUTTON_USAGE_FOCUSED = new TranslatableComponent("narration.button.usage.focused");
        public static final Component BUTTON_USAGE_HOVERED = new TranslatableComponent("narration.button.usage.hovered");
        public static final Component COMPONENT_LIST_USAGE = new TranslatableComponent("narration.component_list.usage");
        public static final Component INVENTORY = new TranslatableComponent("container.inventory");
        public static final String SCREEN_POSITION_KEY = "narrator.position.screen";
        public static final Component HOTBAR = new TranslatableComponent("options.attack.hotbar");
    }
}
