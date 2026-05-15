package net.monolith.hud;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ChatScreen;
import net.monolith.module.Module;
import net.monolith.module.ModuleManager;
import net.monolith.render.Mre2D;
import net.monolith.utils.RenderUtils;
import org.lwjgl.glfw.GLFW;

public class KeybindsHudElement extends HudElement {
   private float heightAnim = 20.0F;
   private float widthAnim = 100.0F;
   private float alphaAnim = 0.0F;

   public KeybindsHudElement(int x, int y) {
      super(x, y, 100, 20);
   }

   @Override
   public void render(DrawContext context, float tickDelta) {
      MinecraftClient mc = MinecraftClient.getInstance();
      Mre2D renderer = Mre2D.of(context);
      List<Module> active = this.activeModules();
      boolean edit = mc.currentScreen instanceof ChatScreen;
      boolean visible = !active.isEmpty() || edit;
      this.alphaAnim = RenderUtils.lerp(this.alphaAnim, visible ? 1.0F : 0.0F, 0.3F * Math.max(1.0F, tickDelta));
      if (!(this.alphaAnim < 0.02F) || visible) {
         int rows = Math.max(active.size(), edit ? 1 : 0);
         float targetWidth = Math.max(118.0F, this.maxWidth(renderer, mc, active, edit));
         float targetHeight = 28.0F + (float)rows * 18.0F;
         this.widthAnim = RenderUtils.lerp(this.widthAnim, targetWidth, 0.2F * Math.max(1.0F, tickDelta));
         this.heightAnim = RenderUtils.lerp(this.heightAnim, targetHeight, 0.2F * Math.max(1.0F, tickDelta));
         this.width = (int)this.widthAnim;
         this.height = (int)this.heightAnim;
         int alpha = Math.min(255, Math.max(0, (int)(this.alphaAnim * 255.0F)));
         int text = alpha << 24 | 16777215;
         int bind = alpha << 24 | 15422825;
         HudStyle.panel(renderer, (float)this.x, (float)this.y, (float)this.width, (float)this.height, 9.0F, alpha);
         HudStyle.header(renderer, mc, "K", "Keybinds", this.x + 4, this.y + 3, this.width - 8, alpha);
         int rowY = this.y + 29;
         if (active.isEmpty() && edit) {
            this.renderRow(renderer, mc, "AttackAura", "R", rowY, text, bind);
         } else {
            for (Module module : active) {
               this.renderRow(renderer, mc, module.name, this.bindName(this.normalize(module.keyCode)), rowY, text, bind);
               rowY += 17;
            }
         }
      }
   }

   private void renderRow(Mre2D renderer, MinecraftClient mc, String moduleName, String keyName, int rowY, int textColor, int bindColor) {
      HudStyle.row(renderer, (float)(this.x + 5), (float)(rowY - 3), (float)(this.width - 10), 14.0F, 150);
      renderer.text(mc.textRenderer, moduleName, this.x + 11, rowY, textColor, false);
      int keyWidth = renderer.textWidth(mc.textRenderer, keyName) + 12;
      int keyX = this.x + this.width - keyWidth - 8;
      renderer.roundedRect((float)keyX, (float)(rowY - 2), (float)keyWidth, 12.0F, 6.0F, HudStyle.withAlpha(HudStyle.ACCENT, 95));
      renderer.centeredText(mc.textRenderer, keyName, keyX + keyWidth / 2, rowY, bindColor, false);
   }

   private List<Module> activeModules() {
      List<Module> modules = new ArrayList<>();

      for (Module module : ModuleManager.modules) {
         if (module.enabled && this.normalize(module.keyCode) >= 0) {
            modules.add(module);
         }
      }

      modules.sort(Comparator.comparing(modulex -> modulex.name));
      return modules;
   }

   private float maxWidth(Mre2D renderer, MinecraftClient mc, List<Module> active, boolean edit) {
      float max = (float)(renderer.textWidth(mc.textRenderer, "Keybinds") + 50);
      if (active.isEmpty() && edit) {
         return Math.max(max, (float)(renderer.textWidth(mc.textRenderer, "AttackAura") + renderer.textWidth(mc.textRenderer, "R") + 52));
      } else {
         for (Module module : active) {
            String keyName = this.bindName(this.normalize(module.keyCode));
            max = Math.max(max, (float)(renderer.textWidth(mc.textRenderer, module.name) + renderer.textWidth(mc.textRenderer, keyName) + 58));
         }

         return max + 6.0F;
      }
   }

   private int normalize(int key) {
      return key < -1 ? -key - 2 : key;
   }

   private String bindName(int keyCode) {
      if (keyCode >= 0 && keyCode <= 7) {
         return "MOUSE_" + (keyCode + 1);
      } else {
         String name = GLFW.glfwGetKeyName(keyCode, 0);
         if (name != null) {
            return name.toUpperCase();
         } else {
            return switch (keyCode) {
               case 32 -> "SPACE";
               case 256 -> "ESC";
               case 257 -> "ENTER";
               case 258 -> "TAB";
               case 259 -> "BACKSPACE";
               case 260 -> "INSERT";
               case 261 -> "DEL";
               case 262 -> "RIGHT";
               case 263 -> "LEFT";
               case 264 -> "DOWN";
               case 265 -> "UP";
               case 266 -> "PGUP";
               case 267 -> "PGDN";
               case 268 -> "HOME";
               case 269 -> "END";
               case 280 -> "CAPS";
               case 340 -> "LSHIFT";
               case 341 -> "LCTRL";
               case 342 -> "LALT";
               case 344 -> "RSHIFT";
               case 345 -> "RCTRL";
               case 346 -> "RALT";
               default -> "KEY_" + keyCode;
            };
         }
      }
   }
}
