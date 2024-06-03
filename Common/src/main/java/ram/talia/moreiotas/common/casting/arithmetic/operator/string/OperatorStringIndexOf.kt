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
import at.petrak.hexcasting.common.lib.hex.HexEvalSounds
import at.petrak.hexcasting.common.lib.hex.HexIotaTypes
import ram.talia.moreiotas.api.asActionResult
import ram.talia.moreiotas.common.casting.arithmetic.operator.nextString
import ram.talia.moreiotas.common.lib.hex.MoreIotasIotaTypes.STRING
import kotlin.math.roundToInt

object OperatorStringIndexOf : Operator(2, IotaMultiPredicate.pair(IotaPredicate.ofType(STRING), IotaPredicate.ofType(STRING))) {
    override fun operate(
        env: CastingEnvironment,
        image: CastingImage,
        continuation: SpellContinuation
    ): OperationResult {
        val it: Iterator<Iota> = image.stack.reversed().iterator()
        val toSearch = downcast(it.next(), STRING).string
        val searchFor = downcast(it.next(), STRING).string
        val ares = toSearch.indexOf(searchFor).asActionResult
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