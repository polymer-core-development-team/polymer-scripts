package com.teampolymer.polymer.scripts

import com.teampolymer.polymer.scripts.common.scripting.kts.KtsScriptLoader
import net.minecraftforge.fml.common.Mod

@Mod("polymer-scripts")
object PolymerScripts {
    init {

        KtsScriptLoader().load()
    }
}
