package tech.thatgravyboat.skyblockapi.utils.codecs

import com.mojang.authlib.GameProfile
import com.mojang.serialization.Codec
import me.owdding.ktcodecs.IncludedCodec
import net.minecraft.advancements.critereon.BlockPredicate
import net.minecraft.core.BlockPos
import net.minecraft.core.GlobalPos
import net.minecraft.core.Vec3i
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.nbt.CompoundTag
import net.minecraft.nbt.TagParser
import net.minecraft.resources.ResourceLocation
import net.minecraft.util.ExtraCodecs
import net.minecraft.util.ResourceLocationPattern
import net.minecraft.util.Unit
import net.minecraft.util.valueproviders.FloatProvider
import net.minecraft.util.valueproviders.IntProvider
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import org.joml.Vector3f
import org.joml.Vector4f
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Instant

internal object IncludedCodecs {
    @IncludedCodec
    val RESOURCE_LOCATION_CODEC: Codec<ResourceLocation> = ResourceLocation.CODEC

    @IncludedCodec
    val BLOCK_PREDICATE_CODEC: Codec<BlockPredicate> = BlockPredicate.CODEC

    @IncludedCodec
    val BLOCK_POS_CODEC: Codec<BlockPos> = BlockPos.CODEC

    @IncludedCodec
    val GLOBAL_POS_CODEC: Codec<GlobalPos> = GlobalPos.CODEC

    @IncludedCodec
    val VEC_3I_CODEC: Codec<Vec3i> = Vec3i.CODEC

    @IncludedCodec
    val COMPOUND_TAG_CODEC: Codec<CompoundTag> = TagParser.LENIENT_CODEC

    @IncludedCodec
    val VECTOR_3F_CODEC: Codec<Vector3f> = ExtraCodecs.VECTOR3F

    @IncludedCodec
    val VECTOR_4F_CODEC: Codec<Vector4f> = ExtraCodecs.VECTOR4F

    @IncludedCodec
    val GAME_PROFILE_CODEC: Codec<GameProfile> = ExtraCodecs.GAME_PROFILE

    @IncludedCodec
    val RESOURCE_LOCATION_PATTERN_CODEC: Codec<ResourceLocationPattern> = ResourceLocationPattern.CODEC

    @IncludedCodec
    val UNIT_CODEC: Codec<Unit> = Unit.CODEC

    @IncludedCodec
    val FLOAT_PROVIDER_CODEC: Codec<FloatProvider> = FloatProvider.CODEC

    @IncludedCodec
    val INT_PROVIDER_CODEC: Codec<IntProvider> = IntProvider.CODEC

    @IncludedCodec
    val ITEM_CODEC: Codec<Item> = BuiltInRegistries.ITEM.byNameCodec()

    @IncludedCodec
    val ITEM_STACK_CODEC: Codec<ItemStack> = ItemStack.OPTIONAL_CODEC

    @IncludedCodec
    val DURATION: Codec<Duration> = Codec.LONG.xmap({ it.milliseconds }, Duration::inWholeMilliseconds)

    @IncludedCodec
    val INSTANT: Codec<Instant> = Codec.LONG.xmap(Instant::fromEpochMilliseconds, Instant::toEpochMilliseconds)

    val INT_KEY: Codec<Int> = Codec.STRING.xmap({ it.toInt() }, { it.toString() })
}
