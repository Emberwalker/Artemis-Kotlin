package io.drakon.artemis.logging

import org.apache.commons.io.FileUtils
import org.apache.logging.log4j.LogManager
import java.io.File
import java.util.HashMap
import java.util.concurrent.locks.ReentrantLock

class ExitLogThread : Thread(), Runnable {

    companion object {
        public val mapLock: ReentrantLock = ReentrantLock()
        public val failMap: HashMap<String, Int> = HashMap<String, Int>()
    }

    private val logger = LogManager.getLogger()

    init {
        this.setName("ArtemisExitHook")
        logger.info("Exit hook created. Ready.")
    }

    override fun run() {
        mapLock.lock()
        var fmap = failMap.toList()
        mapLock.unlock()

        fmap = fmap sortDescendingBy { ent -> ent.second }

        if (fmap.size() <= 0) return

        var out = ""
        for (ent in fmap) {
            out += "${ent.first}: ${ent.second}\n"
        }

        try {
            val f = File("ArtemisBlamefile.log")
            FileUtils.writeStringToFile(f, out)
        } catch (ex: Exception) {
            // Ignore
        }
    }
}
