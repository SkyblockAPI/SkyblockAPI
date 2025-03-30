package tech.thatgravyboat.skyblockapi.mixins.tagattachments;

import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import org.jetbrains.annotations.NotNull;
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

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

@Mixin(Entity.class)
public class EntityMixin implements ListenForNameChange, EntityAttachmentAccessor {

    @Unique
    boolean autoAttach = false;
    @Unique
    boolean isNameTag = false;
    @Unique
    private int cooldown = 20;
    @Shadow
    private Level level;
    @Unique
    private List<Entity> attached;
    @Unique
    private Entity attachedTo;

    @Inject(method = "setCustomName", at = @At("RETURN"))
    public void setCustomName(Component name, CallbackInfo ci) {
        if (isNameTag) {
            if (name == null) return;
            new NameChangedEvent(((Entity) (Object) this), name).post(SkyBlockAPI.getEventBus());
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
        final List<Entity> entities = this.level.getEntities(self(), AABB.ofSize(self().position(), 3, 3, 3));
        entities.sort(Comparator.comparing(e -> e.distanceToSqr(self())));
        entities.removeIf(ArmorStand.class::isInstance);
        entities.removeIf(Objects::isNull);

        int index = entities.indexOf(attachedTo);
        if ((index != -1 && index < 2) || entities.isEmpty()) {
            return;
        }

        final Entity first = entities.getFirst();
        if (first == null) {
            return;
        }

        if (attachedTo != first && attachedTo != null) {
            ((EntityAttachmentAccessor) first).skyblockapi$getAttachments().remove(self());
        }

        ((EntityAttachmentAccessor) first).skyblockapi$getAttachments().add(self());
        final Component customName = self().getCustomName();
        attachedTo = first;
        if (customName != null) {
            new ComponentAttachEvent(customName, self()).post(SkyBlockAPI.getEventBus());
        }
    }

    @Override
    public @NotNull List<Entity> skyblockapi$getAttachments() {
        if (attached == null) {
            attached = new ArrayList<>();
        }

        return attached;
    }

    @Inject(method = "onRemoval", at = @At("RETURN"))
    public void remove(CallbackInfo ci) {
        if (this.attachedTo != null) {
            ((EntityAttachmentAccessor) attachedTo).skyblockapi$getAttachments().remove(self());
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
}
