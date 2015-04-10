package io.drakon.artemis.logging

import net.minecraftforge.fml.common.Loader
import org.apache.logging.log4j.LogManager
import org.jetbrains.kotlin.utils.join
import java.util.HashMap

object ModMapper {

    private val logger = LogManager.getLogger("Artemis/Mapper")
    private val mapping = HashMap<String, String>()

    public fun init() {
        logger.info("Compiling package -> mod mappings...")

        val modList = Loader.instance().getModList()
        for (mod in modList) {
            val modId = mod.getModId()
            for (pack in mod.getOwnedPackages()) mapping.put(pack, modId)
        }

        logger.info("Mod package map built (${mapping.size()} entries)")
    }

    public fun getModId(clName: String): String? {
        val pkgs = clName.split('.')
        val pkg = pkgs.take(pkgs.size()-2).joinToString(".")
        val mod = mapping.get(pkg)

        if (mod == null) logger.warn("Missing mapping for package $pkg (original: $clName)")
        return mod
    }

}
