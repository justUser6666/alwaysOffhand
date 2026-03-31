package justUser.alwaysoffhand;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import net.minecraft.client.MinecraftClient;
import net.minecraft.item.ItemStack;
import net.minecraft.network.packet.c2s.play.ClickSlotC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.screen.sync.ItemStackHash;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

public class left {
    public static void defaultClick() {
        var client = MinecraftClient.getInstance();
        var player = client.player;
        var handler = player.currentScreenHandler;
        var inventory = player.getInventory();

        // Server
        // change offhand <-> mainhand
        client.getNetworkHandler().sendPacket(new PlayerActionC2SPacket(
                PlayerActionC2SPacket.Action.SWAP_ITEM_WITH_OFFHAND,
                BlockPos.ORIGIN,
                Direction.DOWN
        ));

        // change mainhand <-> cursor
        Int2ObjectMap modifiedStacks = new Int2ObjectOpenHashMap();
        ItemStackHash cursorHash = ItemStackHash.fromItemStack(
                handler.getCursorStack(),
                client.getNetworkHandler().getComponentHasher());

        int mainHandSlot = 0;

        for (int i = 0; i < handler.slots.size(); i++) {
            if (handler.slots.get(i).inventory == inventory &&
                    handler.slots.get(i).getIndex() == inventory.getSelectedSlot()) {
                mainHandSlot = i;
            }
        }

        client.getNetworkHandler().sendPacket(
                new ClickSlotC2SPacket(
                        handler.syncId,
                        handler.getRevision(),
                        (short)mainHandSlot,
                        (byte) 0,
                        SlotActionType.PICKUP,
                        modifiedStacks,
                        cursorHash
                )
        );

        // change mainhand <-> offhand
        client.getNetworkHandler().sendPacket(new PlayerActionC2SPacket(
                PlayerActionC2SPacket.Action.SWAP_ITEM_WITH_OFFHAND,
                BlockPos.ORIGIN,
                Direction.DOWN
        ));

        ItemStack item1 = handler.getCursorStack();
        ItemStack item2 = inventory.getStack(40).copy();
        inventory.setStack(40, item1);
        handler.setCursorStack(item2);
        handler.syncState();
        main.isChanged = true;
    }
}
