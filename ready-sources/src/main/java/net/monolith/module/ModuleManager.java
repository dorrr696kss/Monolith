package net.monolith.module;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ModuleManager {
   public static final List<Module> modules = new ArrayList<>();

   public static void init() {
      Module attackAura = new Module("AttackAura", "Combat").description("Attacks selected targets with configurable rotations and filters");
      attackAura.addSetting("Range", 3.2, 2.0, 6.0, 0.1);
      attackAura.addSetting("Vision", 4.2, 1.5, 6.0, 0.1);
      attackAura.addOptionSetting("Rotations", false, "SpookyTime duels", "SpookyTime duels", "Polar", "Vulcan", "Grim", "Funtime");
      attackAura.addOptionSetting("Targets", true, "Players", "Players", "Friends", "Mobs", "Animals");
      attackAura.addOptionSetting("Move correction", false, "Free", "Free", "Targeted");
      attackAura.addOptionSetting("Extra", true, "Don't attack while eating", "Don't attack while eating", "Don't attack through walls", "Only with weapon");
      attackAura.addOptionSetting("Bypass features", true, "Randomize aim", "Randomize aim", "Smart cooldown", "Sprint reset", "Target strafe");
      modules.add(attackAura);
      modules.add(new Module("Velocity", "Combat").description("Reduces incoming knockback"));
      Module hudModule = new Module("HUD", "Visuals").description("Shows draggable client HUD widgets");
      hudModule.enabled = true;
      modules.add(hudModule);
      Module worldRender = new Module("World render", "Visuals").description("Changes world lighting and sky rendering");
      worldRender.addOptionSetting("Custom world", false, "Off", "Off", "On");
      worldRender.addColorSetting("Sky Color", 6728447);
      modules.add(worldRender);
      Module fullbright = new Module("Fullbright", "Visuals").description("Makes dark areas bright");
      modules.add(fullbright);
      Module prediction = new Module("Prediction", "Visuals").description("Shows projectile landing path, time and impact marker");
      Module.OptionSetting predictionShow = prediction.addOptionSetting("Show", true, "Pearl", "Pearl", "Bow", "Crossbow", "Trident");
      predictionShow.selected.addAll(Arrays.asList("Bow", "Crossbow", "Trident"));
      modules.add(prediction);
      Module removals = new Module("Removals", "Visuals").description("Removes selected visual effects");
      removals.addOptionSetting("Remove", true, "Fire", "Fire", "Shake");
      modules.add(removals);
      Module targetEsp = new Module("TargetESP", "Visuals").description("Highlights aura target with square or ghost souls mode");
      targetEsp.modes = Arrays.asList("Квадрат", "Души");
      targetEsp.currentMode = "Квадрат";
      modules.add(targetEsp);
      Module esp = new Module("ESP", "Visuals").description("Draws boxes on selected entity types");
      esp.addSetting("Opacity", 45.0, 10.0, 100.0, 5.0);
      esp.addOptionSetting("Targets", true, "Players", "Players", "Mobs", "Animals");
      modules.add(esp);
      Module nametags = new Module("Nametags", "Visuals").description("Shows entity names and equipment");
      nametags.addOptionSetting("Show", true, "Armor", "Armor", "Left item", "Right item");
      modules.add(nametags);
      Module trails = new Module("Trails", "Visuals").description("Leaves a smooth trail behind your player");
      trails.addSetting("Length", 36.0, 10.0, 90.0, 2.0);
      trails.addSetting("Height", 0.9, 0.55, 1.15, 0.02);
      trails.addSetting("Top Gap", 0.04, 0.0, 0.25, 0.01);
      trails.addOptionSetting("View", false, "Third person", "Third person", "Always");
      modules.add(trails);
      Module arrows = new Module("Arrows", "Visuals").description("Points toward nearby players on the screen");
      arrows.addSetting("Range", 80.0, 20.0, 180.0, 5.0);
      arrows.addSetting("Distance", 62.0, 35.0, 110.0, 1.0);
      arrows.addSetting("Size", 18.0, 10.0, 32.0, 1.0);
      arrows.addOptionSetting("Design", false, "Client", "Client", "Celestial", "Nursultan");
      modules.add(arrows);
      Module storageEsp = new Module("StorageESP", "Visuals").description("Highlights chests and other containers");
      storageEsp.addSetting("Range", 48.0, 12.0, 128.0, 4.0);
      modules.add(storageEsp);
      Module worldParticles = new Module("World Particles", "Visuals").description("Spawns many falling stars that always face you");
      worldParticles.addSetting("Amount", 70.0, 20.0, 140.0, 5.0);
      worldParticles.addSetting("Radius", 11.0, 5.0, 24.0, 1.0);
      modules.add(worldParticles);
      modules.add(new Module("Sprint", "Movement").description("Keeps sprint enabled while moving"));
      modules.add(new Module("NoSlow", "Movement").description("Reduces item-use slowdown"));
      modules.add(new Module("Optimization", "Misc").description("Applies lightweight client optimizations"));
   }

   public static List<Module> getModulesByCategory(String category) {
      List<Module> result = new ArrayList<>();

      for (Module m : modules) {
         if (m.category.equals(category)) {
            result.add(m);
         }
      }

      return result;
   }

   public static Module getModule(String name) {
      for (Module m : modules) {
         if (m.name.equalsIgnoreCase(name)) {
            return m;
         }
      }

      return null;
   }
}
