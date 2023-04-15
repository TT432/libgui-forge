package io.github.cottonmc.cotton.gui.impl.client;

import net.minecraft.network.chat.Component;

public final class NarrationMessages {
    public static final String ITEM_SLOT_TITLE_KEY = "widget.libgui.item_slot.narration.title";
    public static final String LABELED_SLIDER_TITLE_KEY = "widget.libgui.labeled_slider.narration.title";
    public static final Component SCROLL_BAR_TITLE = Component.translatable("widget.libgui.scroll_bar.narration.title");
    public static final String SLIDER_MESSAGE_KEY = "widget.libgui.slider.narration.title";
    public static final Component SLIDER_USAGE = Component.translatable("widget.libgui.slider.narration.usage");
    public static final String TAB_TITLE_KEY = "widget.libgui.tab.narration.title";
    public static final String TAB_POSITION_KEY = "widget.libgui.tab.narration.position";
    public static final String TEXT_FIELD_TITLE_KEY = "widget.libgui.text_field.narration.title";
    public static final String TEXT_FIELD_SUGGESTION_KEY = "widget.libgui.text_field.narration.suggestion";
    public static final String TOGGLE_BUTTON_NAMED_KEY = "widget.libgui.toggle_button.narration.named";
    public static final Component TOGGLE_BUTTON_OFF = Component.translatable("widget.libgui.toggle_button.narration.off");
    public static final Component TOGGLE_BUTTON_ON = Component.translatable("widget.libgui.toggle_button.narration.on");
    public static final String TOGGLE_BUTTON_UNNAMED_KEY = "widget.libgui.toggle_button.narration.unnamed";

    public static final class Vanilla {
        public static final Component BUTTON_USAGE_FOCUSED = Component.translatable("narration.button.usage.focused");
        public static final Component BUTTON_USAGE_HOVERED = Component.translatable("narration.button.usage.hovered");
        public static final Component COMPONENT_LIST_USAGE = Component.translatable("narration.component_list.usage");
        public static final Component INVENTORY = Component.translatable("container.inventory");
        public static final String SCREEN_POSITION_KEY = "narrator.position.screen";
        public static final Component HOTBAR = Component.translatable("options.attack.hotbar");
    }
}
