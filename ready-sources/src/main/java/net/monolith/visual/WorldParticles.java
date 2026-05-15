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

      int amount = Math.max(1, (int)Math.round(module.getSettingValue("Amount", 22.0)));
      double radius = module.getSettingValue("Radius", 7.0);
      while (PARTICLES.size() < amount) {
         PARTICLES.add(spawn(client.player.getPos(), radius));
      }

      Iterator<WorldParticles.Particle> iterator = PARTICLES.iterator();
      Vec3d player = client.player.getPos().add(0.0, 0.9, 0.0);
      while (iterator.hasNext()) {
         WorldParticles.Particle particle = iterator.next();
         Vec3d follow = player.subtract(particle.pos).normalize().multiply(0.012);
         particle.velocity = particle.velocity.multiply(0.92).add(follow).add(0.0, -0.006, 0.0);
         particle.pos = particle.pos.add(particle.velocity);
         particle.age++;
         particle.rotation += 0.055F;
         if (particle.age > particle.life || particle.pos.y < player.y - 2.4 || particle.pos.squaredDistanceTo(player) > radius * radius * 5.0) {
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
            renderer.billboardAdditive(STAR, particle.pos, particle.size, particle.rotation, alpha << 24 | 16777215);
         }
      }
   }

   private static WorldParticles.Particle spawn(Vec3d center, double radius) {
      double angle = RANDOM.nextDouble() * Math.PI * 2.0;
      double distance = 1.0 + RANDOM.nextDouble() * radius;
      Vec3d pos = center.add(Math.cos(angle) * distance, 2.4 + RANDOM.nextDouble() * 3.2, Math.sin(angle) * distance);
      Vec3d velocity = new Vec3d((RANDOM.nextDouble() - 0.5) * 0.03, -0.025 - RANDOM.nextDouble() * 0.035, (RANDOM.nextDouble() - 0.5) * 0.03);
      return new WorldParticles.Particle(pos, velocity, 0.28F + RANDOM.nextFloat() * 0.24F, RANDOM.nextInt(150) + 90);
   }

   private static final class Particle {
      private Vec3d pos;
      private Vec3d velocity;
      private final float size;
      private final int life;
      private int age;
      private float rotation;

      private Particle(Vec3d pos, Vec3d velocity, float size, int life) {
         this.pos = pos;
         this.velocity = velocity;
         this.size = size;
         this.life = life;
      }
   }
}
