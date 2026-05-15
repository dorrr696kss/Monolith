package net.monolith.hud;

import net.minecraft.client.MinecraftClient;
import net.monolith.render.Mre2D;

public final class HudStyle {
   public static final int ACCENT = -16718337;
   public static final int ACCENT_2 = -6595329;
   public static final int TEXT = -1;
   public static final int MUTED = -3421237;
   public static final int DARK = 0xFF070A12;

   private HudStyle() {
   }

   public static void panel(Mre2D renderer, float x, float y, float width, float height, float radius, int alpha) {
      int a = clamp(alpha);
      renderer.blur(x, y, width, height, radius, 14.0F, withAlpha(0xFF05070D, Math.min(220, a)));
      renderer.gradient(x, y, width, height, withAlpha(0xFF111827, Math.min(215, a)), withAlpha(0xFF070A12, Math.min(205, a)));
      renderer.roundedOutline(x, y, width, height, radius, 1.0F, withAlpha(0xFF46F0E7, Math.min(95, a)));
      renderer.roundedRect(x + 2.0F, y + 2.0F, Math.max(0.0F, width - 4.0F), 1.4F, radius, withAlpha(ACCENT, Math.min(150, a)));
   }

   public static void header(Mre2D renderer, MinecraftClient client, String icon, String title, int x, int y, int width, int alpha) {
      int a = clamp(alpha);
      renderer.circle((float)(x + 11), (float)(y + 11), 8.0F, withAlpha(ACCENT, Math.min(165, a)));
      renderer.centeredText(client.textRenderer, icon, x + 11, y + 7, withAlpha(TEXT, a), false);
      renderer.text(client.textRenderer, title, x + 24, y + 7, withAlpha(TEXT, a), false);
      renderer.roundedRect((float)(x + width - 25), (float)(y + 9), 16.0F, 4.0F, 2.0F, withAlpha(ACCENT_2, Math.min(145, a)));
   }

   public static void row(Mre2D renderer, float x, float y, float width, float height, int alpha) {
      renderer.roundedRect(x, y, width, height, 6.0F, withAlpha(0xFF151C2A, Math.min(138, clamp(alpha))));
   }

   public static int withAlpha(int color, int alpha) {
      return clamp(alpha) << 24 | color & 16777215;
   }

   private static int clamp(int alpha) {
      return Math.max(0, Math.min(255, alpha));
   }
}
