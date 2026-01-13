package com.hypixel.hytale.server.npc.corecomponents.combat;

import com.hypixel.hytale.component.ComponentAccessor;
import com.hypixel.hytale.component.ComponentType;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.math.shape.Box;
import com.hypixel.hytale.math.vector.Vector3d;
import com.hypixel.hytale.math.vector.Vector3f;
import com.hypixel.hytale.server.core.modules.entity.component.BoundingBox;
import com.hypixel.hytale.server.core.modules.entity.component.HeadRotation;
import com.hypixel.hytale.server.core.modules.entity.component.ModelComponent;
import com.hypixel.hytale.server.core.modules.entity.component.TransformComponent;
import com.hypixel.hytale.server.core.modules.physics.component.Velocity;
import com.hypixel.hytale.server.core.modules.physics.util.PhysicsMath;
import com.hypixel.hytale.server.core.modules.projectile.config.BallisticData;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.hypixel.hytale.server.npc.asset.builder.BuilderSupport;
import com.hypixel.hytale.server.npc.corecomponents.HeadMotionBase;
import com.hypixel.hytale.server.npc.corecomponents.combat.builders.BuilderHeadMotionAim;
import com.hypixel.hytale.server.npc.movement.Steering;
import com.hypixel.hytale.server.npc.role.Role;
import com.hypixel.hytale.server.npc.sensorinfo.IPositionProvider;
import com.hypixel.hytale.server.npc.sensorinfo.InfoProvider;
import com.hypixel.hytale.server.npc.util.AimingData;
import com.hypixel.hytale.server.npc.util.NPCPhysicsMath;
import java.util.concurrent.ThreadLocalRandom;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class HeadMotionAim extends HeadMotionBase {
   protected static final ComponentType<EntityStore, TransformComponent> TRANSFORM_COMPONENT_TYPE = TransformComponent.getComponentType();
   protected static final ComponentType<EntityStore, ModelComponent> MODEL_COMPONENT_TYPE = ModelComponent.getComponentType();
   protected static final ComponentType<EntityStore, BoundingBox> BOUNDING_BOX_COMPONENT_TYPE = BoundingBox.getComponentType();
   protected final double spread;
   protected final boolean deflection;
   protected final double hitProbability;
   protected final double relativeTurnSpeed;
   protected final AimingData aimingData = new AimingData();
   protected Ref<EntityStore> lastTargetReference;
   protected double spreadX;
   protected double spreadY;
   protected double spreadZ;

   public HeadMotionAim(@Nonnull BuilderHeadMotionAim builder, @Nonnull BuilderSupport support) {
      super(builder);
      this.spread = builder.getSpread();
      this.hitProbability = builder.getHitProbability();
      this.deflection = builder.isDeflection();
      this.relativeTurnSpeed = builder.getRelativeTurnSpeed(support);
   }

   @Override
   public void preComputeSteering(@Nonnull Ref<EntityStore> ref, @Nonnull Role role, @Nullable InfoProvider sensorInfo, @Nonnull Store<EntityStore> store) {
      if (sensorInfo != null) {
         sensorInfo.passExtraInfo(this.aimingData);
      }
   }

   @Override
   public void activate(@Nonnull Ref<EntityStore> ref, @Nonnull Role role, @Nonnull ComponentAccessor<EntityStore> componentAccessor) {
      this.aimingData.setHaveAttacked(true);
   }

   @Override
   public boolean computeSteering(
      @Nonnull Ref<EntityStore> ref,
      @Nonnull Role support,
      @Nullable InfoProvider sensorInfo,
      double dt,
      @Nonnull Steering desiredSteering,
      @Nonnull ComponentAccessor<EntityStore> componentAccessor
   ) {
      if (sensorInfo != null && sensorInfo.hasPosition()) {
         TransformComponent transformComponent = componentAccessor.getComponent(ref, TRANSFORM_COMPONENT_TYPE);

         assert transformComponent != null;

         ModelComponent modelComponent = componentAccessor.getComponent(ref, MODEL_COMPONENT_TYPE);

         assert modelComponent != null;

         Vector3d position = transformComponent.getPosition();
         IPositionProvider positionProvider = sensorInfo.getPositionProvider();
         double x = positionProvider.getX() - position.getX();
         double y = positionProvider.getY() - position.getY() - modelComponent.getModel().getEyeHeight();
         double z = positionProvider.getZ() - position.getZ();
         double vx = 0.0;
         double vy = 0.0;
         double vz = 0.0;
         Ref<EntityStore> targetRef = positionProvider.getTarget();
         if (targetRef != null) {
            Velocity targetVelocityComponent = componentAccessor.getComponent(targetRef, Velocity.getComponentType());

            assert targetVelocityComponent != null;

            BoundingBox boundingBoxComponent = componentAccessor.getComponent(ref, BOUNDING_BOX_COMPONENT_TYPE);
            Box boundingBox = boundingBoxComponent != null ? boundingBoxComponent.getBoundingBox() : null;
            if (this.aimingData.isBallistic()) {
               if (boundingBox != null) {
                  x += (boundingBox.getMax().getX() + boundingBox.getMin().getX()) / 2.0;
                  y += (boundingBox.getMax().getY() + boundingBox.getMin().getY()) / 2.0;
                  z += (boundingBox.getMax().getZ() + boundingBox.getMin().getZ()) / 2.0;
               }

               if (this.deflection) {
                  Vector3d steeringVelocity = targetVelocityComponent.getVelocity();
                  vx = steeringVelocity.getX();
                  vy = steeringVelocity.getY();
                  vz = steeringVelocity.getZ();
               }
            } else if (boundingBox != null) {
               double minY = y + boundingBox.getMin().y;
               double maxY = y + boundingBox.getMax().y;
               if (minY > 0.0) {
                  y = minY;
               } else if (maxY < 0.0) {
                  y = maxY;
               } else {
                  y = 0.0;
               }
            }
         }

         if (this.aimingData.isBallistic()) {
            BallisticData ballisticData = this.aimingData.getBallisticData();
            if (ballisticData != null) {
               y += ballisticData.getVerticalCenterShot();
               this.aimingData.setDepthOffset(ballisticData.getDepthShot(), ballisticData.isPitchAdjustShot());
            } else {
               this.aimingData.setDepthOffset(0.0, false);
            }

            if (targetRef != null && (this.lastTargetReference == null || !this.lastTargetReference.equals(targetRef))) {
               this.lastTargetReference = targetRef;
               this.aimingData.setHaveAttacked(true);
            }

            if (this.aimingData.isHaveAttacked()) {
               ThreadLocalRandom random = ThreadLocalRandom.current();
               if (this.spread > 0.0 && random.nextDouble() > this.hitProbability) {
                  double spread2 = 2.0 * this.spread * Math.sqrt(NPCPhysicsMath.dotProduct(x, y, z)) / 10.0;
                  this.spreadX = this.spreadX + spread2 * (random.nextDouble() - 0.5);
                  this.spreadY = this.spreadY + spread2 * (random.nextDouble() - 0.5);
                  this.spreadZ = this.spreadZ + spread2 * (random.nextDouble() - 0.5);
               } else {
                  this.spreadX = 0.0;
                  this.spreadY = 0.0;
                  this.spreadZ = 0.0;
               }

               this.aimingData.setHaveAttacked(false);
            }

            x += this.spreadX;
            y += this.spreadY;
            z += this.spreadZ;
         }

         float pitch;
         float yaw;
         if (this.aimingData.computeSolution(x, y, z, vx, vy, vz)) {
            yaw = this.aimingData.getYaw();
            pitch = this.aimingData.getPitch();
            this.aimingData.setTarget(targetRef);
         } else {
            HeadRotation headRotationComponent = componentAccessor.getComponent(ref, HeadRotation.getComponentType());

            assert headRotationComponent != null;

            double xxzz = x * x + z * z;
            double xxyyzz = xxzz + y * y;
            Vector3f headRotation = headRotationComponent.getRotation();
            yaw = xxzz >= 1.0E-4 ? PhysicsMath.normalizeTurnAngle(PhysicsMath.headingFromDirection(x, z)) : headRotation.getYaw();
            pitch = xxyyzz >= 1.0E-4 ? PhysicsMath.pitchFromDirection(x, y, z) : headRotation.getPitch();
            this.aimingData.setOrientation(yaw, pitch);
            this.aimingData.setTarget(null);
         }

         desiredSteering.clearTranslation();
         desiredSteering.setYaw(yaw);
         desiredSteering.setPitch(pitch);
         desiredSteering.setRelativeTurnSpeed(this.relativeTurnSpeed);
         return true;
      } else {
         desiredSteering.clear();
         return true;
      }
   }
}
