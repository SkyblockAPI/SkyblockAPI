package tech.thatgravyboat.skyblockapi.hooks;

import org.jetbrains.annotations.ApiStatus;

@ApiStatus.Internal
public interface AttributeInstanceHook {

    void skyblockapi$setServerValue(double value);
    double skyblockapi$getServerValue();
}
