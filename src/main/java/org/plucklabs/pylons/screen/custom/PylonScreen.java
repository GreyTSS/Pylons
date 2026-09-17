package org.plucklabs.pylons.screen.custom;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.plucklabs.pylons.Pylons;
import org.plucklabs.pylons.block.entity.custom.PylonBlockEntity;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Set;

public class PylonScreen extends AbstractContainerScreen<PylonMenu> {
    private final int imageWidth;
    private final int imageHeight;

    private Level level;
    private Player player;
    private PylonBlockEntity blockEntity;

    private PylonButton sortByButton;
    private PylonButton includeSelectedButton;
    private PylonButton confirmButton;
    private PylonButton cancelButton;
    private PylonEditBox searchBox;

    @Nullable
    private PylonButton showMapButton;

    private PylonMobList mobList;

    // false = A to Z, true = Z to A
    private boolean sortZtoA;
    private boolean includeSelected;

    public final ResourceLocation GUI_TEXTURE =
            ResourceLocation.fromNamespaceAndPath(Pylons.MODID, "textures/gui/pylon/pylon.png");

    public static final List<EntityType<?>> ALL_MONSTERS = BuiltInRegistries.ENTITY_TYPE.stream().filter(
            (entityType) ->
                    entityType.getCategory() == MobCategory.MONSTER &&
                            entityType != EntityType.ENDER_DRAGON &&
                            entityType != EntityType.ELDER_GUARDIAN &&
                            entityType != EntityType.WITHER
    ).toList();

    public PylonScreen(PylonMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
        this.imageWidth = 230;
        this.imageHeight = 219;

        this.level = Minecraft.getInstance().level;
        this.player = Minecraft.getInstance().player;
        this.blockEntity = menu.blockEntity;

        this.font = Minecraft.getInstance().font;
    }

    public void onPressSortBy(Button button) {
        if (button.getMessage().equals(Component.translatable("string.pylons.sortByAZ"))) {
            button.setMessage(Component.translatable("string.pylons.sortByZA"));
            sortZtoA = true;
        }
        else {
            button.setMessage(Component.translatable("string.pylons.sortByAZ"));
            sortZtoA = false;
        }
        sortMobEntries();
    }

    public void onPressIncludeSelected(Button button) {
        if (button.getMessage().equals(Component.translatable("string.pylons.includeSelectedON"))) {
            button.setMessage(Component.translatable("string.pylons.includeSelectedOFF"));
            includeSelected = false;
        }
        else {
            button.setMessage(Component.translatable("string.pylons.includeSelectedON"));
            includeSelected = true;
        }
        sortMobEntries();
    }

    public void onPressConfirm(Button button) {
        Set<EntityType<?>> blacklist = mobList.getBlacklist();
        menu.setBlacklist(blacklist);
        onPressCancel(null);
    }

    public void onPressCancel(Button button) {
        this.onClose();
    }

    public void onPressShowOnMap(Button button) {

    }

    public void onPressMobEntry(Button button) {

    }

    public void setUpWidgets() {
        clearWidgets();

        WidgetData.loadValues();
        WidgetData.setOffset(getCornerX(), getCornerY());

        sortByButton = addRenderableWidget(new PylonButton(
                WidgetData.SORT_BY,
                Component.translatable("string.pylons.sortByAZ"),
                this::onPressSortBy
        ));

        includeSelectedButton = addRenderableWidget(new PylonButton(
                WidgetData.INCLUDE_SELECTED,
                Component.translatable("string.pylons.includeSelectedON"),
                this::onPressIncludeSelected
        ));

        confirmButton = addRenderableWidget(new PylonButton(
                WidgetData.CONFIRM,
                Component.translatable("string.pylons.confirm"),
                this::onPressConfirm
        ));

        cancelButton = addRenderableWidget(new PylonButton(
                WidgetData.CANCEL,
                Component.translatable("string.pylons.cancel"),
                this::onPressCancel
        ));

        searchBox = addRenderableWidget(new PylonEditBox(font,
                WidgetData.SEARCH_BOX,
                Component.translatable("string.pylons.search")
        ));

        mobList = addRenderableWidget(new PylonMobList(this, WidgetData.MOB_LIST));

        if (WidgetData.compatibleMapIsInstalled()) {
            showMapButton = addRenderableWidget(new PylonButton(
                    WidgetData.SHOW_ON_MAP,
                    Component.translatable("string.pylons.showOnMap"),
                    this::onPressShowOnMap
            ));
        }
    }

    public void sortMobEntries() {
        mobList.sortEntries(includeSelected, sortZtoA);
    }

    @Override
    protected void init() {
        super.init();
        setUpWidgets();
    }

    public int getCornerX() {
        return (this.width - this.imageWidth) / 2;
    }
    public int getCornerY() {
        return (this.height - this.imageHeight) / 2;
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float v, int i, int i1) {
        int x = getCornerX();
        int y = getCornerY();
        guiGraphics.blit(GUI_TEXTURE, x, y, 0, 0, this.imageWidth, this.imageHeight);
    }

    @Override
    protected void renderLabels(GuiGraphics guiGraphics, int mouseX, int mouseY) {}
}
