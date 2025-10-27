package safro.archon.network;

import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import safro.archon.Archon;

public class ExperienceSyncPacket {
    public static final Identifier ID = new Identifier(Archon.MODID, "experience_sync");

    public static void send(ServerPlayerEntity player, int experience) {
        PacketByteBuf buf = PacketByteBufs.create();
        buf.writeInt(experience);
        ServerPlayNetworking.send(player, ID, buf);
    }
}
