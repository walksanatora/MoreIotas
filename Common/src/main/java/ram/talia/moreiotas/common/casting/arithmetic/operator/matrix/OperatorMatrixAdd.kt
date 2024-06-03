package ram.talia.moreiotas.common.casting.arithmetic.operator.matrix

import at.petrak.hexcasting.api.casting.arithmetic.operator.Operator
import at.petrak.hexcasting.api.casting.arithmetic.predicates.IotaMultiPredicate.any
import at.petrak.hexcasting.api.casting.arithmetic.predicates.IotaPredicate.ofType
import at.petrak.hexcasting.api.casting.arithmetic.predicates.IotaPredicate.or
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment
import at.petrak.hexcasting.api.casting.eval.OperationResult
import at.petrak.hexcasting.api.casting.eval.vm.CastingImage
import at.petrak.hexcasting.api.casting.eval.vm.SpellContinuation
import at.petrak.hexcasting.api.casting.iota.Iota
import at.petrak.hexcasting.api.casting.mishaps.MishapInvalidIota
import at.petrak.hexcasting.common.lib.hex.HexEvalSounds
import at.petrak.hexcasting.common.lib.hex.HexIotaTypes.*
import ram.talia.moreiotas.api.*
import ram.talia.moreiotas.common.casting.arithmetic.operator.nextNumOrVecOrMatrix
import ram.talia.moreiotas.common.lib.hex.MoreIotasIotaTypes.MATRIX

class OperatorMatrixAdd(private val subtract: Boolean) : Operator(2, any(ofType(MATRIX), or(ofType(DOUBLE), ofType(VEC3)))) {
    override fun operate(
        env: CastingEnvironment,
        image: CastingImage,
        continuation: SpellContinuation
    ): OperationResult {
        val it = image.stack.reversed().iterator().withIndex()
        val mat0 = it.nextNumOrVecOrMatrix(arity).asMatrix
        val mat1 = it.nextNumOrVecOrMatrix(arity).asMatrix

        if (mat0.rows != mat1.rows || mat0.columns != mat1.columns)
            throw MishapInvalidIota.matrixWrongSize(image.stack.last(), 0, mat0.rows, mat0.columns)
        val ares = if (subtract) { mat0 - mat1 } else { mat0 + mat1 }.asActionResult
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