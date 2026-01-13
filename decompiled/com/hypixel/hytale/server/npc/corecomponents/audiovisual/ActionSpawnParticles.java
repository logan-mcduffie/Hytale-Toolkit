package com.hypixel.hytale.server.npc.corecomponents.audiovisual;

import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.component.spatial.SpatialResource;
import com.hypixel.hytale.math.vector.Vector3d;
import com.hypixel.hytale.server.core.modules.entity.EntityModule;
import com.hypixel.hytale.server.core.modules.entity.component.TransformComponent;
import com.hypixel.hytale.server.core.universe.world.ParticleUtil;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.hypixel.hytale.server.npc.asset.builder.BuilderSupport;
import com.hypixel.hytale.server.npc.corecomponents.ActionBase;
import com.hypixel.hytale.server.npc.corecomponents.audiovisual.builders.BuilderActionSpawnParticles;
import com.hypixel.hytale.server.npc.role.Role;
import com.hypixel.hytale.server.npc.sensorinfo.InfoProvider;
import it.unimi.dsi.fastutil.objects.ObjectList;
import javax.annotation.Nonnull;

public class ActionSpawnParticles extends ActionBase {
   protected final String particleSystem;
   protected final double range;
   protected final Vector3d offset;

   public ActionSpawnParticles(@Nonnull BuilderActionSpawnParticles builder, @Nonnull BuilderSupport support) {
      super(builder);
      this.particleSystem = builder.getParticleSystem(support);
      this.offset = builder.getOffset();
      this.range = builder.getRange();
   }

   @Override
   public boolean execute(@Nonnull Ref<EntityStore> ref, @Nonnull Role role, InfoProvider sensorInfo, double dt, @Nonnull Store<EntityStore> store) {
      super.execute(ref, role, sensorInfo, dt, store);
      TransformComponent transformComponent = store.getComponent(ref, TransformComponent.getComponentType());

      assert transformComponent != null;

      Vector3d position = new Vector3d(this.offset).rotateY(transformComponent.getRotation().getYaw()).add(transformComponent.getPosition());
      SpatialResource<Ref<EntityStore>, EntityStore> playerSpatialResource = store.getResource(EntityModule.get().getPlayerSpatialResourceType());
      ObjectList<Ref<EntityStore>> results = SpatialResource.getThreadLocalReferenceList();
      playerSpatialResource.getSpatialStructure().collect(position, this.range, results);
      ParticleUtil.spawnParticleEffect(this.particleSystem, position, results, store);
      return true;
   }
}
