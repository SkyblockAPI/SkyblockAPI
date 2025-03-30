package tech.thatgravyboat.skyblockapi.mixins.tagattachments;

import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import tech.thatgravyboat.skyblockapi.api.SkyBlockAPI;
import tech.thatgravyboat.skyblockapi.api.events.entity.ComponentAttachEvent;
import tech.thatgravyboat.skyblockapi.api.events.entity.ListenForNameChange;
import tech.thatgravyboat.skyblockapi.api.events.entity.NameChangedEvent;
import tech.thatgravyboat.skyblockapi.helpers.EntityAttachmentAccessor;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Mixin(Entity.class)
public abstract class EntityMixin implements ListenForNameChange, EntityAttachmentAccessor {

    @Unique
    boolean autoAttach = false;
    @Unique
    boolean isNameTag = false;
    @Unique
    private int cooldown = 20;
    @Shadow
    private Level level;
    @Unique
    private List<WeakReference<Entity>> attached;
    @Unique
    private Entity attachedTo;

    @Shadow
    public abstract AABB getBoundingBox();

    @Shadow
    @Final
    private static EntityDataAccessor<Optional<Component>> DATA_CUSTOM_NAME;

    @Shadow
    @Nullable
    public abstract Component getCustomName();

    @Inject(method = "setCustomName", at = @At("RETURN"))
    public void setCustomName(Component name, CallbackInfo ci) {
        if (isNameTag) {
            if (name == null) {
                return;
            }
            new NameChangedEvent(name, self()).post(SkyBlockAPI.getEventBus());
        }
    }

    @Override
    public void skyblockapi$markAsNameTag() {
        isNameTag = true;
    }

    @Override
    public void skyblockapi$unmarkNameTag() {
        isNameTag = false;
    }

    @Override
    public boolean skyblockapi$isNameTag() {
        return isNameTag;
    }

    @Inject(method = "tick", at = @At("RETURN"))
    public void tick(CallbackInfo ci) {
        if (!autoAttach) {
            return;
        }

        if (cooldown-- < 0) {
            skyblockapi$attachToClosest();
            cooldown = 20;
        }
    }

    @Override
    public void skyblockapi$attachToClosest() {
        autoAttach = true;
        final List<Entity> entities = this.level.getEntities(self(), getBoundingBox().inflate(0, 1, 0));
        entities.sort(Comparator.comparing(e -> e.distanceToSqr(self())));
        entities.removeIf(it -> it == null || it instanceof ArmorStand);

        int index = entities.indexOf(attachedTo);
        if ((index != -1 && index < 2) || entities.isEmpty()) {
            return;
        }

        final Entity first = entities.getFirst();
        if (first == null) {
            return;
        }

        if (attachedTo != first && attachedTo != null) {
            ((EntityAttachmentAccessor) first).skyblockapi$getAttachments().removeIf(it -> it.get() == self());
        }

        ((EntityAttachmentAccessor) first).skyblockapi$getAttachments().add(new WeakReference<>(self()));
        final Component customName = self().getCustomName();
        attachedTo = first;
        if (customName != null) {
            new ComponentAttachEvent(customName, self()).post(SkyBlockAPI.getEventBus());
        }
    }

    @Override
    public @NotNull List<WeakReference<Entity>> skyblockapi$getAttachments() {
        if (attached == null) {
            attached = new ArrayList<>();
        }

        return attached;
    }

    @Inject(method = "onRemoval", at = @At("RETURN"))
    public void remove(CallbackInfo ci) {
        if (this.attachedTo != null) {
            ((EntityAttachmentAccessor) attachedTo).skyblockapi$getAttachments().removeIf(it -> it.get() == self());
            this.autoAttach = false;
        }
    }

    @Unique
    private Entity self() {
        return (Entity) (Object) this;
    }

    @Override
    public @NotNull Entity skyblockapi$getAttachedTo() {
        return attachedTo;
    }

    @Inject(method = "onSyncedDataUpdated(Lnet/minecraft/network/syncher/EntityDataAccessor;)V", at = @At("TAIL"))
    public void onEntityDataUpdate(EntityDataAccessor<?> entityDataAccessor, CallbackInfo ci) {
        if (this.skyblockapi$isNameTag()) {
            if (entityDataAccessor == DATA_CUSTOM_NAME) {
                final Component customName = getCustomName();
                if (customName != null) {
                    new NameChangedEvent(customName, self()).post(SkyBlockAPI.getEventBus());
                }
            }
        }
    }
}
