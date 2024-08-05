package com.lazrproductions.cuffed.event.base;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.event.entity.living.LivingEvent;

public class LivingDismountEvent extends LivingEvent
{
    Entity vehicle;
    public LivingDismountEvent(LivingEntity entity, Entity vehicle)
    {
        super(entity);
        this.setVehicle(vehicle);
    }

    public void setVehicle(Entity entity) { this.vehicle = entity; }
    public Entity getVehicle() { return vehicle; }
}