package net.monolith.hud;

import java.util.Locale;
import net.minecraft.entity.LivingEntity;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.monolith.combat.AttackAura;
import net.monolith.render.Mre2D;
import net.monolith.utils.RenderUtils;

public class TargetHudElement extends HudElement {
   private LivingEntity displayedTarget;
   private long lastSeenTime;
   private float healthAnim;
   private float absorptionAnim;
   private float openAnim;

   public TargetHudElement(int x, int y) {
      super(x, y, 150, 54);
   }

   @Override
   public void render(DrawContext context, float tickDelta) {
      MinecraftClient mc = MinecraftClient.getInstance();
      LivingEntity target = this.currentTarget(mc);
      boolean edit = mc.currentScreen instanceof ChatScreen;
      if (target != null) {
         this.displayedTarget = target;
         this.lastSeenTime = System.currentTimeMillis();
      }

      boolean visible = this.displayedTarget != null && (target != null || edit || System.currentTimeMillis() - this.lastSeenTime < 1000L);
      this.openAnim = RenderUtils.lerp(this.openAnim, visible ? 1.0F : 0.0F, 0.18F * Math.max(1.0F, tickDelta));
      if (this.openAnim < 0.02F) {
         this.displayedTarget = null;
      } else {
         LivingEntity entity = (LivingEntity)(this.displayedTarget == null ? mc.player : this.displayedTarget);
         if (entity != null) {
            String name = entity.getName().getString();
            float health = Math.max(0.0F, entity.getHealth());
            float maxHealth = Math.max(health, entity.getMaxHealth());
            float percent = Math.max(0.0F, Math.min(1.0F, health / maxHealth));
            float absorption = Math.max(0.0F, Math.min(1.0F, entity.getAbsorptionAmount() / maxHealth));
            this.healthAnim = RenderUtils.lerp(this.healthAnim, percent, 0.22F * Math.max(1.0F, tickDelta));
            this.absorptionAnim = RenderUtils.lerp(this.absorptionAnim, absorption, 0.22F * Math.max(1.0F, tickDelta));
            Mre2D renderer = Mre2D.of(context);
            renderer.push();
            renderer.translate((float)this.x + (float)this.width / 2.0F, (float)this.y + (float)this.height / 2.0F, 0.0F);
            renderer.scale(this.openAnim, this.openAnim, 1.0F);
            renderer.translate(-((float)this.x + (float)this.width / 2.0F), -((float)this.y + (float)this.height / 2.0F), 0.0F);
            int alpha = Math.min(255, Math.max(0, (int)(this.openAnim * 255.0F)));
            int textColor = alpha << 24 | 16777215;
            HudStyle.panel(renderer, (float)this.x, (float)this.y, (float)this.width, (float)this.height, 10.0F, alpha);
            this.drawAvatar(context, renderer, entity, this.x + 8, this.y + 8, 38, alpha);
            renderer.text(mc.textRenderer, this.trim(renderer, mc, name, 88), this.x + 54, this.y + 10, textColor, false);
            renderer.text(mc.textRenderer, "Health " + ((int)health + (int)entity.getAbsorptionAmount()), this.x + 54, this.y + 23, HudStyle.withAlpha(HudStyle.MUTED, alpha), false);
            renderer.roundedRect((float)(this.x + 54), (float)(this.y + 40), (float)(this.width - 64), 6.0F, 3.0F, HudStyle.withAlpha(0xFF121827, Math.min(150, alpha)));
            renderer.roundedRect((float)(this.x + 54), (float)(this.y + 40), (float)(this.width - 64) * this.healthAnim, 6.0F, 3.0F, HudStyle.withAlpha(0xFF3BF0D2, alpha));
            if (this.absorptionAnim > 0.01F) {
               renderer.roundedRect(
                  (float)(this.x + 54),
                  (float)(this.y + 40),
                  Math.min((float)(this.width - 64), (float)(this.width - 64) * (this.healthAnim + this.absorptionAnim)),
                  6.0F,
                  3.0F,
                  HudStyle.withAlpha(0xFFFFD166, alpha)
               );
            }

            renderer.roundedRect((float)(this.x + 54), (float)(this.y + 40), (float)(this.width - 64) * this.healthAnim, 2.0F, 2.0F, HudStyle.withAlpha(0xFFFFFFFF, Math.min(95, alpha)));
            renderer.pop();
         }
      }
   }

   private String trim(Mre2D renderer, MinecraftClient mc, String name, int width) {
      return renderer.textWidth(mc.textRenderer, name) <= width ? name : mc.textRenderer.trimToWidth(name, width);
   }

   private LivingEntity currentTarget(MinecraftClient mc) {
      LivingEntity auraTarget = AttackAura.target();
      if (auraTarget != null) {
         return auraTarget;
      } else {
         return mc.currentScreen instanceof ChatScreen ? mc.player : null;
      }
   }

   private void drawAvatar(DrawContext context, Mre2D renderer, LivingEntity entity, int x, int y, int size, int alpha) {
      renderer.roundedRect((float)x, (float)y, (float)size, (float)size, 8.0F, HudStyle.withAlpha(HudStyle.ACCENT, Math.min(80, alpha)));
      renderer.roundedOutline((float)x, (float)y, (float)size, (float)size, 8.0F, 1.0F, HudStyle.withAlpha(HudStyle.ACCENT, Math.min(150, alpha)));
      if (entity instanceof AbstractClientPlayerEntity player) {
         context.drawTexture(RenderLayer::getGuiTextured, player.getSkinTextures().texture(), x + 4, y + 4, 8.0F, 8.0F, size - 8, size - 8, 64, 64);
      } else {
         String shortName = entity.getName().getString();
         String letter = shortName.isEmpty() ? "?" : shortName.substring(0, 1).toUpperCase(Locale.ROOT);
         renderer.circle((float)x + (float)size / 2.0F, (float)y + (float)size / 2.0F, (float)size / 2.0F - 7.0F, HudStyle.withAlpha(HudStyle.ACCENT_2, Math.min(210, alpha)));
         renderer.centeredText(MinecraftClient.getInstance().textRenderer, letter, x + size / 2, y + 16, alpha << 24 | 16777215, false);
      }

      if (entity.hurtTime > 0) {
         renderer.roundedRect((float)x, (float)y, (float)size, (float)size, 6.0F, Math.min(90, alpha) << 24 | 16724787);
      }
   }
}
