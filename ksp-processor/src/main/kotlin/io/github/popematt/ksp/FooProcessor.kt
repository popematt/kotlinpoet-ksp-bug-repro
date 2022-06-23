package io.github.popematt.ksp

import io.github.popematt.annotation.FooAnnotation
import com.google.devtools.ksp.processing.*
import com.google.devtools.ksp.symbol.KSAnnotated
import com.google.devtools.ksp.symbol.KSValueParameter
import com.squareup.kotlinpoet.ksp.toTypeName

class FooProcessorProvider: SymbolProcessorProvider {
    override fun create(environment: SymbolProcessorEnvironment): SymbolProcessor {
        return FooProcessor(environment.logger)
    }
}

class FooProcessor(private val logger: KSPLogger) : SymbolProcessor {
    override fun process(resolver: Resolver): List<KSAnnotated> {
        resolver.getSymbolsWithAnnotation(FooAnnotation::class.qualifiedName!!)
            .filterIsInstance<KSValueParameter>()
            .forEach { ksValueParameter ->
                    val name = ksValueParameter.name?.asString()
                    val type = ksValueParameter.type.toTypeName()
                    logger.info(" Found annotated param - $name: $type")
                }

        return emptyList()
    }
}
