package tech.thatgravyboat.skyblockapi.mixins.tagattachments;

//@Mixin(EntityRenderer.class)
public class EntityRendererMixin {

    /*
    @Inject(method = "render", at = @At("HEAD"))
    public <S extends EntityRenderState> void render(
        CallbackInfo ci,
        @Local(argsOnly = true) S state,
        @Local(argsOnly = true) PoseStack poseStack,
        @Local(argsOnly = true) MultiBufferSource bufferSource
    ) {
        if (!EntityEvents.INSTANCE.getDebug()) {
            return;
        }
        final Entity entity = ((EntityRenderAccessor) state).skyblockapi$getSelf();
        if (entity == Minecraft.getInstance().crosshairPickEntity) {
            final List<WeakReference<Entity>> attachedLines = SkyBlockEntity.getAttachedEntities(entity);
            poseStack.pushPose();
            for (WeakReference<Entity> attachedLine : attachedLines) {
                final Entity attachedLineEntity = attachedLine.get();
                if (attachedLineEntity == null) {
                    continue;
                }

                final Vec3 subtract = attachedLineEntity.position().subtract(entity.position());

                ShapeRenderer.renderVector(
                    poseStack,
                    bufferSource.getBuffer(RenderType.lines()),
                    Vec3.ZERO.toVector3f(),
                    subtract.add(0, 0.1, 0).normalize().scale(2), -1);
            }
            poseStack.popPose();
        }
    }

    @Inject(method = "extractRenderState", at = @At("HEAD"))
    public <T extends Entity, S extends EntityRenderState> void extractRenderState(
        CallbackInfo ci,
        @Local(argsOnly = true) T self,
        @Local(argsOnly = true) S state
    ) {
        ((EntityRenderAccessor) state).skyblockapi$setSelf(self);
    }
*/
}
