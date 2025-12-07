package tech.thatgravyboat.skyblockapi.hooks;

public interface RunnableClickEventHook {

    void skyblockapi$setRunnable(Runnable action);

    Runnable skyblockapi$getRunnable();
}
