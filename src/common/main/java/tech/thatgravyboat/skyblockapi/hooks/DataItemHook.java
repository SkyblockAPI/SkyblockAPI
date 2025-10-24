package tech.thatgravyboat.skyblockapi.hooks;

import org.jetbrains.annotations.ApiStatus;

@ApiStatus.Internal
public interface DataItemHook<T> {

    void skyblockapi$setServerValue(T value);
    T skyblockapi$getServerValue();
}
