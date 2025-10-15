package safro.archon.mixin.client;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import safro.archon.item.ExperiencePouchItem;
import safro.archon.network.ExperienceChangePacket;

@Mixin(MinecraftClient.class)
public class MinecraftClientMixin {

    @Shadow
    public ClientPlayerEntity player;

    @Inject(method = "doAttack", at = @At("HEAD"), cancellable = true)
    private void onDoAttack(CallbackInfoReturnable<Boolean> cir) {
        if (player != null) {
            ItemStack mainHandStack = player.getMainHandStack();
            if (player.isSneaking() && mainHandStack.getItem() instanceof ExperiencePouchItem) {
                ExperienceChangePacket.send(-1, false);
                cir.setReturnValue(false);
            }
        }
    }
}
