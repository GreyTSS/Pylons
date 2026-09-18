package org.plucklabs.pylons.screen.custom;

import net.minecraft.client.gui.components.Button;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.plucklabs.pylons.block.ModBlocks;
import org.plucklabs.pylons.block.entity.custom.PylonBlockEntity;
import org.plucklabs.pylons.screen.ModMenuTypes;

import java.util.Set;

public class PylonMenu extends AbstractContainerMenu {
    public final PylonBlockEntity blockEntity;
    private final Level level;
    private final Set<EntityType<?>> blacklist;

    public PylonMenu(int containerId, Inventory inv, FriendlyByteBuf extraData) {
        this(containerId, inv, inv.player.level().getBlockEntity(extraData.readBlockPos()));
    }

    public PylonMenu(int containerId, Inventory inv, BlockEntity blockEntity) {
        super(ModMenuTypes.PYLON_MENU.get(), containerId);
        this.blockEntity = ((PylonBlockEntity) blockEntity);
        this.level = inv.player.level();
        this.blacklist = this.blockEntity.getBlackList();
    }

    public void setBlacklist(Set<EntityType<?>> blacklist, Player player) {
        blockEntity.setBlacklist(blacklist);
        if(level instanceof ServerLevel serverLevel) {
            System.out.println("Server!");
        } else if(level instanceof ClientLevel client){
            System.out.println("Client!");
        }
        player.sendSystemMessage(Component.literal("Menu Set"+blockEntity.getBlackList().toString()));
        blockEntity.setChanged();
    }

    // This method handles shift clicking an item to and from inventories / containers
    // The Pylon's GUI won't have such interactions
    // No quickMoveStack interactions
    @Override
    public ItemStack quickMoveStack(Player player, int i) {
        return null;
    }

    @Override
    public boolean stillValid(Player player) {
        return stillValid(
                ContainerLevelAccess.create(level, blockEntity.getBlockPos()),
                player,
                ModBlocks.PYLON.get()
        );
    }
}
