package org.plucklabs.pylons.screen.custom;

import net.minecraft.network.chat.Component;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.player.Player;
import org.plucklabs.pylons.block.entity.custom.PylonBlockEntity;

public class PylonGUI {
    public static void openMenu(Player player, PylonBlockEntity blockEntity) {
        player.openMenu(new SimpleMenuProvider((containerID, inventory, playerEntity) ->
                new PylonMenu(containerID, inventory, blockEntity),
                Component.translatable("string.pylons.menu")
                ),
                buf -> buf.writeBlockPos(blockEntity.getBlockPos())
        );
    }
}
