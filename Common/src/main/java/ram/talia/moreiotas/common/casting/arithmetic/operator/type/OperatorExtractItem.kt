package ram.talia.moreiotas.common.casting.arithmetic.operator.type

import at.petrak.hexcasting.api.casting.arithmetic.operator.Operator
import at.petrak.hexcasting.api.casting.arithmetic.predicates.IotaMultiPredicate
import at.petrak.hexcasting.api.casting.arithmetic.predicates.IotaPredicate.*
import at.petrak.hexcasting.api.casting.asActionResult
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment
import at.petrak.hexcasting.api.casting.eval.OperationResult
import at.petrak.hexcasting.api.casting.eval.vm.CastingImage
import at.petrak.hexcasting.api.casting.eval.vm.SpellContinuation
import at.petrak.hexcasting.api.casting.iota.EntityIota
import at.petrak.hexcasting.api.casting.iota.Iota
import at.petrak.hexcasting.api.casting.iota.Vec3Iota
import at.petrak.hexcasting.api.casting.mishaps.MishapInvalidIota
import at.petrak.hexcasting.common.lib.hex.HexEvalSounds
import at.petrak.hexcasting.common.lib.hex.HexIotaTypes.ENTITY
import at.petrak.hexcasting.common.lib.hex.HexIotaTypes.VEC3
import net.minecraft.core.BlockPos
import net.minecraft.world.entity.decoration.ItemFrame
import net.minecraft.world.entity.item.ItemEntity
import net.minecraft.world.item.Items
import ram.talia.moreiotas.api.asActionResult
import ram.talia.moreiotas.api.casting.iota.ItemStackIota
import ram.talia.moreiotas.common.lib.hex.MoreIotasIotaTypes
import ram.talia.moreiotas.common.lib.hex.MoreIotasIotaTypes.ITEM_STACK

object OperatorExtractItem : Operator(1, IotaMultiPredicate.all(any(ofType(VEC3), ofType(ENTITY), ofType(ITEM_STACK)))) {

    override fun operate(
        env: CastingEnvironment,
        image: CastingImage,
        continuation: SpellContinuation
    ): OperationResult {
        val it: Iterator<Iota> = image.stack.reversed().iterator()

        val ares = when (val iota = it.next()) {
            is Vec3Iota -> {
                if (!env.isVecInRange(iota.vec3))
                    null.asActionResult
                else {
                    val blockState = env.world.getBlockState(BlockPos.containing(iota.vec3))
                    blockState.block.asActionResult
                }
            }
            is EntityIota -> {
                when (val entity = iota.entity) {
                    is ItemEntity -> entity.item.item.asActionResult
                    is ItemFrame -> entity.item.item.asActionResult
                    else -> throw MishapInvalidIota.of(iota, 0, "blockitementityitemframeitem")
                }
            }
            is ItemStackIota -> {
                if (iota.itemStack.isEmpty)
                    Items.AIR.asActionResult
                else
                    iota.itemStack.item.asActionResult
            }
            else -> throw IllegalStateException("iota argument to OperatorExtractItem must be one of Vec3, Entity, or ItemStack.")
        }

        val output = mutableListOf<Iota>()
        it.asSequence().toMutableList().reversed().forEach {output.add(it)}
        ares.forEach { output.add(it) }
        return OperationResult(
            image.copy(output),
            listOf(),
            continuation,
            HexEvalSounds.NORMAL_EXECUTE
        )
    }
}