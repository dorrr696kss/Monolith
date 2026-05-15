package net.monolith.mixin;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.TitleScreen;
import net.monolith.ui.MonolithMainMenu;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({MinecraftClient.class})
public class MinecraftClientMixin {
   private boolean monolith$openingMainMenu;

   @Inject(
      method = {"setScreen"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void onSetScreen(Screen screen, CallbackInfo ci) {
      if (!this.monolith$openingMainMenu && screen instanceof TitleScreen) {
         ci.cancel();
         this.monolith$openingMainMenu = true;
         MinecraftClient.getInstance().setScreen(new MonolithMainMenu());
         this.monolith$openingMainMenu = false;
      }
   }
}
