package com.lazrproductions.cuffed.entity.base;

import net.minecraft.resources.ResourceLocation;

public interface IRestrainableEntity {
    /** Get whether or not this entity is restrained. */
    public boolean isRestrained();
    
    /** Get the restraint code for this entity. */
    public int getRestraintCode();
    /** Get the arm restraint's id for this entity, cooresponding with each restraint item */
    public ResourceLocation getArmRestraintId();
    /** Get the leg restraint's id for this entity, cooresponding with each restraint item */
    public ResourceLocation getLegRestraintId();
    /** Get the head restraint's id for this entity, cooresponding with each restraint item */
    public ResourceLocation getHeadRestraintId();

    /** Get whether or not the arm restraint is enchanted. */
    public boolean getArmsAreEnchanted();
    /** Get whether or not the leg restraint is enchanted. */
    public boolean getLegsAreEnchanted();
    /** Get whether or not the head restraint is enchanted. */
    public boolean getHeadIsEnchanted();

    /** Set and automatically sync the restraint code to all clients and server. */
    public void setRestraintCode(int v);
    /** Set and automatically sync the arm restraint id to all clients and server. */
    public void setArmRestraintId(ResourceLocation v);
    /** Set and automatically sync the leg restraint id to all clients and server. */
    public void setLegRestraintId(ResourceLocation v);
    /** Set and automatically sync the head restraint id to all clients and server. */
    public void setHeadRestraintId(ResourceLocation v);
    
    /** Set and automatically sync whether or not the arms restraints are enchanted clients and server */
    public void setArmsEnchanted(boolean v);
    /** Set and automatically sync whether or not the legs restraints are enchanted clients and server */
    public void setLegsEnchanted(boolean v);
    /** Set and automatically sync whether or not the head restraints are enchanted clients and server */
    public void setHeadEnchanted(boolean v);
}
