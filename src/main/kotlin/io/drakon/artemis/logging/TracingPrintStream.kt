package io.drakon.artemis.logging

import org.apache.logging.log4j.Logger
import java.io.PrintStream

import io.drakon.artemis.util.Stackshot

class TracingPrintStream(val logger: Logger, original: PrintStream) : PrintStream(original) {

    override fun println(x: Any?) {
        val stack = Thread.currentThread().getStackTrace().toList()
        logger.info("[${Stackshot.getStackTag(stack)}]: $x")
    }

    override fun println(x: String?) { println(x as Any) }

}