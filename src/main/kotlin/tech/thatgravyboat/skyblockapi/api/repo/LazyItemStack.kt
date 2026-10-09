package tech.thatgravyboat.skyblockapi.api.repo

import com.mojang.serialization.Codec
import net.minecraft.core.component.DataComponentPatch
import net.minecraft.core.component.DataComponentType
import net.minecraft.core.component.DataComponents
import net.minecraft.core.component.TypedDataComponent
import net.minecraft.network.chat.Component
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.ItemStackTemplate
import tech.thatgravyboat.skyblockapi.utils.extentions.holder
import tech.thatgravyboat.skyblockapi.utils.text.Text

public class LazyItemStack {

    private val item: Item
    private val count: Int
    private val components: DataComponentPatch

    private var cached: ItemStack? = null

    private constructor(item: Item, count: Int, builder: DataComponentPatch) {
        this.item = item
        this.count = count
        this.components = builder
    }

    private constructor(item: Item, count: Int = 1, builder: DataComponentPatch.Builder = DataComponentPatch.builder()) : this(
        item,
        count,
        builder.build()
    )

    public constructor(item: Item, count: Int = 1, builder: DataComponentPatch.Builder.() -> Unit) : this(
        item,
        count,
        DataComponentPatch.builder().apply(builder)
    )

    public fun withComponents(builder: DataComponentPatch.Builder.() -> Unit): LazyItemStack {
        val components = DataComponentPatch.builder()

        val patch = this.components.split()
        patch.added().forEach { typesComponent ->
            components.set(TypedDataComponent.createUnchecked(typesComponent.type, typesComponent.value))
        }
        patch.removed().forEach { typesComponent ->
            components.remove(typesComponent)
        }

        return LazyItemStack(item, count, components.apply(builder))
    }

    public operator fun <T : Any> get(component: DataComponentType<T>): T? {
         return components.get(net.minecraft.core.component.DataComponentMap.EMPTY, component)
    }
    public fun <T : Any> getOrDefault(component: DataComponentType<T>, default: T): T = get(component) ?: default

    public fun getDisplayName(): Component {
        return this[DataComponents.CUSTOM_NAME] ?: this[DataComponents.ITEM_NAME] ?: Text.translatable(this.item.descriptionId)
    }

    public fun create(): ItemStack {
        if (this.cached == null) {
            this.cached = ItemStack(item.holder, count, components)
        }
        return this.cached!!
    }

    public fun invalidate() {
        this.cached = null
    }

    companion object {

        public val CODEC: Codec<LazyItemStack> = ItemStackTemplate.MAP_CODEC.codec().xmap(
            { template ->
                LazyItemStack(template.item.value(), template.count, template.components)
            },
            { stack ->
                ItemStackTemplate(stack.item.holder, stack.count, stack.components)
            },
        )

        public fun ItemStack.toLazy(): LazyItemStack = LazyItemStack(this.item, this.count) {
            val patch = this@toLazy.componentsPatch.split()
            patch.added().forEach { typesComponent ->
                this.set(typesComponent.type as DataComponentType<Any>, typesComponent.value)
            }
            patch.removed().forEach { typesComponent ->
                this.remove(typesComponent)
            }
        }
    }
}
