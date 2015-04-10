package io.drakon.artemis

import io.drakon.artemis.logging.ExitLogThread
import io.drakon.artemis.logging.ModMapper
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger

import net.minecraftforge.fml.common.Mod as mod
import net.minecraftforge.fml.common.Mod.EventHandler as eventHandler

import net.minecraftforge.fml.common.event.FMLPreInitializationEvent
import net.minecraftforge.fml.common.event.FMLInitializationEvent
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent

import io.drakon.artemis.logging.TracingPrintStream
import io.drakon.artemis.management.Config

/**
 * Artemis
 *
 * STDOUT/STDERR Redirection
 *
 * @author Arkan <arkan@drakon.io>
 */

[mod(modid = "Artemis", name = "Artemis (Kotlin Experimental)", dependencies = "before:*", acceptableRemoteVersions="*", modLanguage = "kotlin")]
class Artemis {

    companion object {
        public val logger: Logger = LogManager.getLogger("Artemis/Core")
        public val outLog: Logger = LogManager.getLogger("Artemis/STDOUT")
        public val errLog: Logger = LogManager.getLogger("Artemis/STDERR")
    }

    eventHandler fun preInit(evt: FMLPreInitializationEvent) {
        logger.info("Beginning preflight.")

        logger.info("Loading config...")
        Config.loadConfig(evt.getSuggestedConfigurationFile())

        logger.info("Injecting print streams...")
        System.setOut(TracingPrintStream(outLog, System.out))
        System.setErr(TracingPrintStream(errLog, System.err))

        if (Config.blamefile) {
            logger.info("Injecting JVM shutdown hook...")
            Runtime.getRuntime().addShutdownHook(ExitLogThread())
        }

        if (Config.logging.mapModIds) ModMapper.init()

        println("Artemis test.")
        System.err.println("Artemis error.")

        logger.info("Preflight complete.")
    }

}
