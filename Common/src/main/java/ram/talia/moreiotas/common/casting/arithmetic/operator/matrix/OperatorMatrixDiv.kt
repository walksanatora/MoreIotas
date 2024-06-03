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
import at.petrak.hexcasting.api.casting.mishaps.Mishap
import at.petrak.hexcasting.api.casting.mishaps.MishapInvalidIota
import at.petrak.hexcasting.common.lib.hex.HexEvalSounds
import at.petrak.hexcasting.common.lib.hex.HexIotaTypes.DOUBLE
import at.petrak.hexcasting.common.lib.hex.HexIotaTypes.VEC3
import org.jblas.Solve
import ram.talia.moreiotas.api.asActionResult
import ram.talia.moreiotas.api.asMatrix
import ram.talia.moreiotas.api.casting.iota.MatrixIota
import ram.talia.moreiotas.api.matrixWrongSize
import ram.talia.moreiotas.common.casting.arithmetic.operator.nextNumOrVecOrMatrix
import ram.talia.moreiotas.common.lib.hex.MoreIotasIotaTypes.MATRIX

object OperatorMatrixDiv : Operator(2, any(ofType(MATRIX), or(ofType(DOUBLE), ofType(VEC3)))) {
     fun apply(iotas: Iterable<Iota>, env: CastingEnvironment): Iterable<Iota> {
        val it = iotas.iterator().withIndex()
        val arg0 = it.nextNumOrVecOrMatrix(arity)
        val arg1 = it.nextNumOrVecOrMatrix(arity)

        arg0.a?.let { return (arg1.asMatrix.divi(it)).asActionResult }
        arg1.a?.let { return (arg0.asMatrix.rdivi(it)).asActionResult }

        val mat0 = arg0.asMatrix
        val mat1 = arg1.asMatrix

        if (mat0.columns != mat1.rows)
            throw MishapInvalidIota.matrixWrongSize(iotas.last(), 0, mat0.columns, null)
        if (mat1.columns != mat1.rows)
            throw MishapInvalidIota.matrixWrongSize(iotas.last(), 0, mat1.rows, mat0.rows)
        return (mat0.mmul(Solve.pinv(mat1))).asActionResult
    }

    override fun operate(
        env: CastingEnvironment,
        image: CastingImage,
        continuation: SpellContinuation
    ): OperationResult {
        val it = image.stack.reversed().iterator().withIndex()
        val arg0 = it.nextNumOrVecOrMatrix(arity)
        val arg1 = it.nextNumOrVecOrMatrix(arity)
        var ares: List<MatrixIota>? = null
        arg0.a?.let { ares =(arg1.asMatrix.divi(it)).asActionResult }
        if (ares == null) {
            if (ares == null) {
                arg1.a?.let { ares = (arg0.asMatrix.rdivi(it)).asActionResult }
                if (ares == null) {
                    val mat0 = arg0.asMatrix
                    val mat1 = arg1.asMatrix

                    if (mat0.columns != mat1.rows)
                        throw MishapInvalidIota.matrixWrongSize(image.stack.last(), 0, mat0.columns, null)
                    if (mat1.columns != mat1.rows)
                        throw MishapInvalidIota.matrixWrongSize(image.stack.last(), 0, mat1.rows, mat0.rows)
                    ares = (mat0.mmul(Solve.pinv(mat1))).asActionResult

                }
            }
        }
        val output = mutableListOf<Iota>()
        it.asSequence().toMutableList().reversed().forEach {output.add(it.value)}
        ares!!.forEach { output.add(it) }
        return OperationResult(
            image.copy(output),
            listOf(),
            continuation,
            HexEvalSounds.NORMAL_EXECUTE
        )
    }


}