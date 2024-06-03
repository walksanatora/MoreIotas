package ram.talia.moreiotas.common.casting.arithmetic.operator.string

import at.petrak.hexcasting.api.casting.arithmetic.operator.Operator
import at.petrak.hexcasting.api.casting.arithmetic.predicates.IotaMultiPredicate
import at.petrak.hexcasting.api.casting.arithmetic.predicates.IotaPredicate
import at.petrak.hexcasting.api.casting.asActionResult
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment
import at.petrak.hexcasting.api.casting.eval.OperationResult
import at.petrak.hexcasting.api.casting.eval.vm.CastingImage
import at.petrak.hexcasting.api.casting.eval.vm.SpellContinuation
import at.petrak.hexcasting.api.casting.iota.DoubleIota
import at.petrak.hexcasting.api.casting.iota.Iota
import at.petrak.hexcasting.api.casting.mishaps.MishapInvalidIota
import at.petrak.hexcasting.common.lib.hex.HexEvalSounds
import at.petrak.hexcasting.common.lib.hex.HexIotaTypes.DOUBLE
import ram.talia.moreiotas.api.asActionResult
import ram.talia.moreiotas.api.casting.iota.StringIota
import ram.talia.moreiotas.common.casting.arithmetic.operator.nextString
import ram.talia.moreiotas.common.lib.hex.MoreIotasIotaTypes.STRING
import kotlin.math.abs
import kotlin.math.min
import kotlin.math.roundToInt

object OperatorStringReplace : Operator(3,
        IotaMultiPredicate.triple(IotaPredicate.ofType(STRING),
        IotaPredicate.or(IotaPredicate.ofType(DOUBLE), IotaPredicate.ofType(STRING)),
        IotaPredicate.ofType(STRING))) {

    override fun operate(
        env: CastingEnvironment,
        image: CastingImage,
        continuation: SpellContinuation
    ): OperationResult {
        val it: Iterator<Iota> = image.stack.reversed().iterator()

        val replaceIn = downcast(it.next(), STRING).string
        val x = it.next()
        val replaceWith = downcast(it.next(), STRING).string

        val ares = if (x is StringIota) {
            val toReplace = x.string
            replaceIn.replaceFirst(toReplace, replaceWith)
        } else {
            val double = (x as DoubleIota).double
            val rounded = double.roundToInt()
            if (abs(double - rounded) > DoubleIota.TOLERANCE || rounded !in 0..replaceIn.length) {
                throw MishapInvalidIota.of(x, 0, "int.positive.less.equal", replaceIn.length)
            }

            val replWithLength = min(replaceIn.length - rounded, replaceWith.length)
            replaceIn.substring(0, rounded) + replaceWith.substring(0, replWithLength) + replaceIn.substring(rounded + replWithLength)
        }.asActionResult

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