package safro.archon.network;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import safro.archon.Archon;
import safro.archon.item.ExperiencePouchItem;

public class ExperienceChangePacket {
    public static final Identifier ID = new Identifier(Archon.MODID, "experience_change");

    public static void send(int amount, boolean add) {
        PacketByteBuf buf = PacketByteBufs.create();
        buf.writeInt(amount);
        buf.writeBoolean(add);
        ClientPlayNetworking.send(ID, buf);
    }

    public static void receive(ServerPlayerEntity player, PacketByteBuf buf) {
        int amount = buf.readInt();
        boolean add = buf.readBoolean();
        ItemStack stack = player.getMainHandStack();

        if (amount == -1) { // Add/remove one level
            int xp = player.getNextLevelExperience();
            if (add) {
                addToPouch(xp, player, stack);
            } else {
                removeFromPouch(xp, player, stack);
            }
        } else if (amount == -2) { // Add/remove all
            if (add) {
                int xp = (int) (player.experienceProgress * player.getNextLevelExperience());
                addToPouch(xp, player, stack);
            } else {
                removeFromPouch(ExperiencePouchItem.getExperience(stack), player, stack);
            }
        } else {
            if (add) {
                addToPouch(amount, player, stack);
            } else {
                removeFromPouch(amount, player, stack);
            }
        }
    }

    private static void addToPouch(int amount, ServerPlayerEntity player, ItemStack stack) {
        if (ExperiencePouchItem.canAddXp(player, stack, amount)) {
            ExperiencePouchItem.addExperience(stack, amount);
            player.addExperience(-amount);
        }
    }

    private static void removeFromPouch(int amount, ServerPlayerEntity player, ItemStack stack) {
        if (ExperiencePouchItem.getExperience(stack) >= amount) {
            ExperiencePouchItem.grantExperience(stack, player, amount);
        }
    }
}
