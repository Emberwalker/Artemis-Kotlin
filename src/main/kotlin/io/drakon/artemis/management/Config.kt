package io.drakon.artemis.management

import java.io.File

import io.drakon.artemis.util.Stackshot
import net.minecraftforge.common.config.Configuration

data class RegexConfig(var useCustom:Boolean = false, var customRegex:String = Stackshot.DEFAULT_REGEX)
data class LogConfig(var stackDepth:Int = 1, var ignoreBuiltins:Boolean = false, var mapModIds:Boolean = true)

object Config {

    public var regex: RegexConfig = RegexConfig()
        private set
    public var logging: LogConfig = LogConfig()
        private set
    public var blamefile: Boolean = false
        private set

    public fun loadConfig(confFile:File) {
        val conf = Configuration(confFile)
        conf.load()

        regex.useCustom = conf.get("builtins", "useCustomRegex", regex.useCustom).getBoolean()
        regex.customRegex = conf.get("builtins", "customRegex", regex.customRegex).getString()

        logging.stackDepth = conf.get("logging", "stackDepth", logging.stackDepth).getInt()
        logging.mapModIds = conf.get("logging", "mapModIds", logging.mapModIds).getBoolean()

        if (logging.stackDepth <= 0) throw RuntimeException("Invalid stackdepth config value.")

        blamefile = conf.get("blame", "createBlamefile", blamefile).getBoolean()

        conf.save()
    }

}