package io.drakon.artemis.util

import io.drakon.artemis.Artemis
import io.drakon.artemis.logging.ExitLogThread
import io.drakon.artemis.logging.ModMapper
import io.drakon.artemis.management.Config
import java.util.regex.Pattern

// Because I am a lazy bugger
public fun StackTraceElement.startsWith(str: String): Boolean {
    return this.getClassName().startsWith(str)
}

public fun StackTraceElement.toPrintable(): String {
    return "${this.getClassName()}:${this.getMethodName()}:${this.getLineNumber()}"
}

object Stackshot {

    public val DEFAULT_REGEX: String = "^paulscode..*|^java.lang..*|^com.intellij..*|^sun..*|^kotlin..*|net.minecraftforge..*"
    private var internalRegex: String? = null

    inline private val INDENTS = "\t\t\t\t"  // TODO: Clean this...

    private fun setup() {
        when(Config.regex.useCustom) {
            true -> internalRegex = Config.regex.customRegex
            false -> internalRegex = DEFAULT_REGEX
        }
    }

    public fun getStackTag(st:List<StackTraceElement>): String {
        internalRegex ?: setup() // Setup if regex is null

        // Drop Artemis and Kotlin IO, filter internals
        var stack = st dropWhile {ent -> !ent.startsWith("io.drakon.artemis")
                     } dropWhile {ent -> !ent.startsWith("kotlin.")
                     } filter {ent -> !ent.getClassName().matches(internalRegex!!)}
        if (stack.size() <= 0) return "...internal..."

        stack = stack.subList(0, Config.logging.stackDepth)

        addToBlame(stack.first())

        if (Config.logging.mapModIds) {
            val modId = ModMapper.getModId(stack.first().getClassName())
            if (modId != null) return "Mod/$modId"
        }

        var out = stack.first().toPrintable()
        for (ent in stack.drop(1)) out += "\n$INDENTS${ent.toPrintable()}"
        return out
    }

    private fun addToBlame(elem:StackTraceElement) {
        ExitLogThread.mapLock.lock()
        try {
            var cnt = ExitLogThread.failMap.getOrPut(elem.getClassName(), {0})
            ExitLogThread.failMap.put(elem.getClassName(), cnt + 1)
        } catch (ex: Exception) {
            Artemis.logger.warn("Failed to add entry to failmap.")
        } finally {
            ExitLogThread.mapLock.unlock()
        }
    }

}
