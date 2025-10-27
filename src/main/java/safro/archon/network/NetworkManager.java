package safro.archon.network;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import safro.archon.client.screen.ExperiencePouchScreenHandler;

public class NetworkManager {

    public static void initServer() {
        ServerPlayNetworking.registerGlobalReceiver(ExperienceChangePacket.ID, ((server, player, handler, buf, responseSender) -> ExperienceChangePacket.receive(player, buf)));
        ServerPlayNetworking.registerGlobalReceiver(InfernalImplodePacket.ID, (((server, player, handler, buf, responseSender) -> InfernalImplodePacket.receive(player))));
    }

    public static void initClient() {
        ClientPlayNetworking.registerGlobalReceiver(ShakePacket.ID, ((client, handler, buf, responseSender) -> ShakePacket.receive(client)));
        ClientPlayNetworking.registerGlobalReceiver(ExperienceSyncPacket.ID, (client, handler, buf, responseSender) -> {
            int experience = buf.readInt();
            client.execute(() -> {
                if (client.player.currentScreenHandler instanceof ExperiencePouchScreenHandler pouchHandler) {
                    pouchHandler.experience = experience;
                }
            });
        });
    }
}
