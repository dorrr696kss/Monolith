package net.monolith.visual;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.monolith.module.Module;
import net.monolith.module.ModuleManager;
import net.monolith.render.Render3D;

public final class WorldParticles {
   private static final Identifier STAR = Identifier.of("monolith", "textures/particles/snow.png");
   private static final List<WorldParticles.Particle> PARTICLES = new ArrayList<>();
   private static final Random RANDOM = new Random();

   private WorldParticles() {
   }

   public static void tick(MinecraftClient client) {
      Module module = ModuleManager.getModule("World Particles");
      if (module == null || !module.enabled || client.player == null || client.world == null) {
         PARTICLES.clear();
         return;
      }

      int amount = Math.max(1, (int)Math.round(module.getSettingValue("Amount", 70.0)));
      double radius = module.getSettingValue("Radius", 7.0);
      while (PARTICLES.size() < amount) {
         PARTICLES.add(spawn(client.player.getPos(), radius));
      }

      Iterator<WorldParticles.Particle> iterator = PARTICLES.iterator();
      Vec3d player = client.player.getPos().add(0.0, 0.9, 0.0);
      while (iterator.hasNext()) {
         WorldParticles.Particle particle = iterator.next();
         particle.velocity = particle.velocity.add(0.0, -0.0025, 0.0);
         particle.pos = particle.pos.add(particle.velocity);
         particle.age++;
         if (particle.age > particle.life || particle.pos.y < player.y - 1.3 || horizontalDistanceSq(particle.pos, player) > radius * radius * 2.6) {
            iterator.remove();
         }
      }
   }

   public static void render(WorldRenderContext context) {
      Module module = ModuleManager.getModule("World Particles");
      if (module != null && module.enabled && MinecraftClient.getInstance().player != null) {
         Render3D renderer = Render3D.of(context);
         for (WorldParticles.Particle particle : PARTICLES) {
            float fadeIn = MathHelper.clamp((float)particle.age / 18.0F, 0.0F, 1.0F);
            float fadeOut = MathHelper.clamp(1.0F - (float)particle.age / (float)particle.life, 0.0F, 1.0F);
            int alpha = (int)(210.0F * Math.min(fadeIn, fadeOut));
            renderer.billboardAdditive(STAR, particle.pos, particle.size, 0.0F, alpha << 24 | 16777215);
         }
      }
   }

   private static WorldParticles.Particle spawn(Vec3d center, double radius) {
      double angle = RANDOM.nextDouble() * Math.PI * 2.0;
      double distance = 2.2 + RANDOM.nextDouble() * radius;
      Vec3d pos = center.add(Math.cos(angle) * distance, 7.0 + RANDOM.nextDouble() * 6.0, Math.sin(angle) * distance);
      Vec3d velocity = new Vec3d((RANDOM.nextDouble() - 0.5) * 0.012, -0.045 - RANDOM.nextDouble() * 0.035, (RANDOM.nextDouble() - 0.5) * 0.012);
      return new WorldParticles.Particle(pos, velocity, 0.34F + RANDOM.nextFloat() * 0.28F, RANDOM.nextInt(190) + 140);
   }

   private static double horizontalDistanceSq(Vec3d first, Vec3d second) {
      double dx = first.x - second.x;
      double dz = first.z - second.z;
      return dx * dx + dz * dz;
   }

   private static final class Particle {
      private Vec3d pos;
      private Vec3d velocity;
      private final float size;
      private final int life;
      private int age;

      private Particle(Vec3d pos, Vec3d velocity, float size, int life) {
         this.pos = pos;
         this.velocity = velocity;
         this.size = size;
         this.life = life;
      }
   }
}
