package com.hypixel.hytale.server.core.modules.projectile.config;

public interface BallisticData {
   double getMuzzleVelocity();

   double getGravity();

   double getVerticalCenterShot();

   double getDepthShot();

   boolean isPitchAdjustShot();
}
