package com.example.data

data class BedrockMod(
    val id: String,
    val name: String,
    val author: String,
    val version: String,
    val category: String, // "OPTIMIZATION", "PVP", "VISUAL", "UTILITY"
    val description: String,
    val fpsImpact: String,
    var isEnabled: Boolean = false
)

data class BedrockSeed(
    val id: String,
    val name: String,
    val seedValue: String,
    val version: String,
    val description: String,
    val coordinates: String,
    val structures: List<String>
)

object BedrockModDefaults {
    val mods = listOf(
        BedrockMod(
            id = "optidragon_boost",
            name = "OptiDragon 2M FPS Shader",
            author = "RenderDragon Team",
            version = "v3.4.1",
            category = "OPTIMIZATION",
            description = "Overrides internal Bedrock render passes, dynamic shadow culling, and 0.0005ms frame rate unlimiter.",
            fpsImpact = "+850,000 FPS",
            isEnabled = true
        ),
        BedrockMod(
            id = "fullbright_gamma",
            name = "Fullbright & Night Vision",
            author = "BedrockClient",
            version = "v2.1",
            category = "VISUAL",
            description = "Increases ambient brightness to 1000% so caves, nether, and night vision are crystal clear without torches.",
            fpsImpact = "+120,000 FPS",
            isEnabled = true
        ),
        BedrockMod(
            id = "nofog_clearwater",
            name = "No Fog & Transparent Water",
            author = "PvP Essentials",
            version = "v1.8",
            category = "VISUAL",
            description = "Removes underwater tint, distance fog, and weather rain particles for maximum visibility.",
            fpsImpact = "+340,000 FPS",
            isEnabled = true
        ),
        BedrockMod(
            id = "damage_indicators",
            name = "Damage Indicators & Health Bars",
            author = "ToroHealth Port",
            version = "v4.0",
            category = "PVP",
            description = "Displays floating damage numbers and entity health hearts when attacking mobs or players in Bedrock.",
            fpsImpact = "Negligible",
            isEnabled = false
        ),
        BedrockMod(
            id = "auto_sprint",
            name = "Toggle Sprint & Sneak",
            author = "Bedrock Tweaks",
            version = "v1.5",
            category = "UTILITY",
            description = "One-tap permanent sprint toggle without needing double-tap forward, perfect for mobile touch controls.",
            fpsImpact = "0% Impact",
            isEnabled = true
        ),
        BedrockMod(
            id = "hitbox_display",
            name = "Hitboxes & Chunk Borders (F3+B)",
            author = "JavaParity Bedrock",
            version = "v2.0",
            category = "UTILITY",
            description = "Renders precise white bounding boxes around entities and yellow chunk boundaries.",
            fpsImpact = "+50,000 FPS",
            isEnabled = false
        ),
        BedrockMod(
            id = "low_fire_shield",
            name = "Low Fire & Transparent Shield",
            author = "Velocity PvP",
            version = "v3.0",
            category = "PVP",
            description = "Lowers the screen-covering flame animation when burning, and renders shields semi-transparent.",
            fpsImpact = "+180,000 FPS",
            isEnabled = true
        )
    )

    val seeds = listOf(
        BedrockSeed(
            id = "seed_trial_spawn",
            name = "Spawn on Trial Chamber & Village",
            seedValue = "-45812958471928419",
            version = "1.21+ Tricky Trials",
            description = "Spawn right inside an acacia village with a Trial Chamber buried directly beneath the town well!",
            coordinates = "X: 120, Y: 72, Z: -240",
            structures = listOf("Trial Chambers", "Acacia Village", "Mace Vault", "Breeze Spawner")
        ),
        BedrockSeed(
            id = "seed_cherry_ancient",
            name = "Cherry Grove Mountain & Ancient City",
            seedValue = "794218590123984",
            version = "1.20 - 1.21",
            description = "Surrounded by a giant pink circular cherry blossom mountain crater with an Ancient City directly underneath.",
            coordinates = "X: 0, Y: 140, Z: 64",
            structures = listOf("Cherry Blossom Crater", "Ancient City", "Deep Dark", "Snowy Peaks")
        ),
        BedrockSeed(
            id = "seed_stronghold_village",
            name = "Triple Blacksmith & Stronghold",
            seedValue = "548192847192301",
            version = "1.21 Bedrock",
            description = "Plains village with 3 blacksmith chests full of diamonds and obsidian, with the End Portal room 20 blocks down.",
            coordinates = "X: -320, Y: 68, Z: 512",
            structures = listOf("Plains Village", "End Stronghold", "Triple Blacksmith", "Nether Fortress (0, 0)")
        ),
        BedrockSeed(
            id = "seed_mansion_spawn",
            name = "Dark Forest Woodland Mansion Spawn",
            seedValue = "-1948201948201",
            version = "1.21 Bedrock",
            description = "Spawn directly on the roof of a giant Woodland Mansion with Allays, Vindicators, and Totems of Undying.",
            coordinates = "X: 34, Y: 85, Z: -12",
            structures = listOf("Woodland Mansion", "Dark Oak Forest", "Allay Cages", "Lush Caves")
        )
    )
}
