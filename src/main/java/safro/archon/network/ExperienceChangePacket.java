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
            if (add) {
                int xpInBar = player.totalExperience - getXpForLevel(player.experienceLevel);
                if (xpInBar == 0 && player.experienceLevel > 0) {
                    // If the bar is empty, de-level the player
                    int xpForPreviousLevel = getXpForLevel(player.experienceLevel) - getXpForLevel(player.experienceLevel - 1);
                    addToPouch(xpForPreviousLevel, player, stack);
                } else {
                    addToPouch(xpInBar, player, stack);
                }
            } else {
                // Remove enough experience to level up
                int xpNeeded = getXpForLevel(player.experienceLevel + 1) - player.totalExperience;
                removeFromPouch(xpNeeded, player, stack);
            }
        } else if (amount == -2) { // Add/remove all
            if (add) {
                addToPouch(player.totalExperience, player, stack);
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
            int added = ExperiencePouchItem.addExperience(stack, amount);
            player.addExperience(-added);
        }
    }

    private static void removeFromPouch(int amount, ServerPlayerEntity player, ItemStack stack) {
        if (ExperiencePouchItem.getExperience(stack) >= amount) {
            ExperiencePouchItem.grantExperience(stack, player, amount);
        }
    }

    private static int getXpForLevel(int level) {
        if (level <= 16) {
            return (int) (Math.pow(level, 2) + 6 * level);
        } else if (level <= 31) {
            return (int) (2.5 * Math.pow(level, 2) - 40.5 * level + 360);
        } else {
            return (int) (4.5 * Math.pow(level, 2) - 162.5 * level + 2220);
        }
    }
}
