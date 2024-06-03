package ram.talia.moreiotas.common.casting.arithmetic.operator.string

import at.petrak.hexcasting.api.casting.arithmetic.operator.Operator
import at.petrak.hexcasting.api.casting.arithmetic.predicates.IotaMultiPredicate
import at.petrak.hexcasting.api.casting.arithmetic.predicates.IotaPredicate
import at.petrak.hexcasting.api.casting.asActionResult
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment
import at.petrak.hexcasting.api.casting.eval.OperationResult
import at.petrak.hexcasting.api.casting.eval.vm.CastingImage
import at.petrak.hexcasting.api.casting.eval.vm.SpellContinuation
import at.petrak.hexcasting.api.casting.iota.Iota
import at.petrak.hexcasting.common.casting.arithmetic.operator.nextPositiveIntUnderInclusive
import at.petrak.hexcasting.common.lib.hex.HexEvalSounds
import at.petrak.hexcasting.common.lib.hex.HexIotaTypes.DOUBLE
import ram.talia.moreiotas.api.asActionResult
import ram.talia.moreiotas.api.casting.iota.StringIota
import ram.talia.moreiotas.common.casting.arithmetic.operator.nextString
import ram.talia.moreiotas.common.lib.hex.MoreIotasIotaTypes.STRING
import kotlin.math.max
import kotlin.math.min

object OperatorStringSlice : Operator(3, IotaMultiPredicate.triple(IotaPredicate.ofType(STRING), IotaPredicate.ofType(DOUBLE), IotaPredicate.ofType(DOUBLE))) {

    override fun operate(
        env: CastingEnvironment,
        image: CastingImage,
        continuation: SpellContinuation
    ): OperationResult {
        val it = image.stack.reversed().iterator().withIndex()
        var ares: List<StringIota>? = null

        val string = it.nextString(arity)
        val index0 = it.nextPositiveIntUnderInclusive(string.length, arity)
        val index1 = it.nextPositiveIntUnderInclusive(string.length, arity)

        if (index0 == index1)
            ares = "".asActionResult
        if (ares == null) {
            ares = string.substring(min(index0, index1), max(index0, index1)).asActionResult
        }

        val output = mutableListOf<Iota>()
        it.asSequence().toMutableList().reversed().forEach {output.add(it.value)}
        ares.forEach { output.add(it) }
        return OperationResult(
            image.copy(output),
            listOf(),
            continuation,
            HexEvalSounds.NORMAL_EXECUTE
        )
    }
}