package com.example.data

data class BedrockVersion(
    val id: String,
    val name: String,
    val releaseDate: String,
    val isLatest: Boolean = false,
    val isPreview: Boolean = false,
    val features: List<String>
)

data class BedrockServer(
    val id: String,
    val name: String,
    val host: String,
    val port: Int = 19132,
    val motd: String,
    val playersOnline: Int,
    val maxPlayers: Int = 20000,
    val pingMs: Int,
    val isOfficialFeatured: Boolean = true
)

data class TexturePack(
    val id: String,
    val name: String,
    val resolution: String,
    val description: String,
    val fpsGain: String,
    val isRecommendedFor2M: Boolean = false
)

data class SkinModel(
    val id: String,
    val name: String,
    val primaryColorHex: Long,
    val accentColorHex: Long,
    val hairColorHex: Long,
    val eyeColorHex: Long,
    val hasArmor: Boolean = true,
    val description: String
)

data class CapeModel(
    val id: String,
    val name: String,
    val colorHex: Long,
    val secondaryHex: Long,
    val description: String
)

object BedrockDefaults {
    val versions = listOf(
        BedrockVersion(
            id = "1.21.50",
            name = "v1.21.50 Tricky Trials",
            releaseDate = "Latest Stable",
            isLatest = true,
            features = listOf("Breeze Mob", "Crafter Block", "Mace Weapon", "Trial Chambers")
        ),
        BedrockVersion(
            id = "1.21.44",
            name = "v1.21.44 Bundles & Bravery",
            releaseDate = "October 2024",
            features = listOf("Bundles item", "Hardcore Mode", "RenderDragon v2")
        ),
        BedrockVersion(
            id = "1.21.0",
            name = "v1.21.0 Tricky Trials Initial",
            releaseDate = "June 2024",
            features = listOf("Ominous Vaults", "Wind Charge", "Bogged Skeleton")
        ),
        BedrockVersion(
            id = "1.20.80",
            name = "v1.20.80 Armadillo Update",
            releaseDate = "April 2024",
            features = listOf("Wolf Armor", "Armadillo", "Mace Prototype")
        ),
        BedrockVersion(
            id = "1.20.15",
            name = "v1.20.15 Trails & Tales",
            releaseDate = "2023",
            features = listOf("Cherry Blossom", "Archaeology", "Sniffer Mob")
        ),
        BedrockVersion(
            id = "preview_1.21.60",
            name = "Bedrock Preview 1.21.60",
            releaseDate = "Beta / Preview",
            isPreview = true,
            features = listOf("The Pale Garden", "Creaking Mob", "Pale Oak Wood")
        )
    )

    val servers = listOf(
        BedrockServer(
            id = "hive",
            name = "The Hive",
            host = "geo.hivebedrock.network",
            port = 19132,
            motd = "§e§lTHE HIVE §7- Bedrock's #1 Minigame Server!",
            playersOnline = 18450,
            pingMs = 18
        ),
        BedrockServer(
            id = "cubecraft",
            name = "CubeCraft Games",
            host = "play.cubecraft.net",
            port = 19132,
            motd = "§b§lCubeCraft §7- SkyWars, EggWars, Lucky Islands",
            playersOnline = 13200,
            pingMs = 24
        ),
        BedrockServer(
            id = "nethergames",
            name = "NetherGames Network",
            host = "play.nethergames.org",
            port = 19132,
            motd = "§c§lNetherGames §7- The Ultimate Bedrock PvP & Bedwars",
            playersOnline = 7890,
            pingMs = 29
        ),
        BedrockServer(
            id = "mineville",
            name = "Mineville",
            host = "play.inpvp.net",
            port = 19132,
            motd = "§a§lMineville §7- City Life, High School Roleplay & Prison",
            playersOnline = 5410,
            pingMs = 35
        ),
        BedrockServer(
            id = "lifeboat",
            name = "Lifeboat Network",
            host = "play.lbsg.net",
            port = 19132,
            motd = "§6§lLifeboat §7- Survival Games, Survival Mode, Skyblock",
            playersOnline = 6120,
            pingMs = 31
        ),
        BedrockServer(
            id = "galaxite",
            name = "Galaxite",
            host = "play.galaxite.net",
            port = 19132,
            motd = "§d§lGalaxite §7- Hyper Curated Sci-Fi Minigames & Core Wars",
            playersOnline = 4320,
            pingMs = 22
        )
    )

    val texturePacks = listOf(
        TexturePack(
            id = "potato_2m",
            name = "Bedrock 2M FPS Turbo Pack",
            resolution = "4x / 8x Ultra Low",
            description = "Stripped mipmaps, simplified block textures and particle bypass for reaching maximum 2,000,000 FPS.",
            fpsGain = "+1,800,000 FPS",
            isRecommendedFor2M = true
        ),
        TexturePack(
            id = "bare_bones",
            name = "Bare Bones 16x",
            resolution = "16x Clean",
            description = "Iconic clean flat colors seen in official Mojang trailers and promotional animations.",
            fpsGain = "+650,000 FPS"
        ),
        TexturePack(
            id = "faithful_32",
            name = "Faithful 32x HD",
            resolution = "32x Enhanced",
            description = "Crisp vanilla high definition textures with sharp edges and realistic lighting.",
            fpsGain = "+200,000 FPS"
        ),
        TexturePack(
            id = "pvp_short",
            name = "PvP 2M Velocity Pack",
            resolution = "16x Short Swords",
            description = "Clear water, low fire, short swords, and highlight ores designed for competitive Bedrock PvP.",
            fpsGain = "+950,000 FPS"
        ),
        TexturePack(
            id = "rtx_renderdragon",
            name = "RenderDragon RTX Ray Tracing",
            resolution = "PBR 64x",
            description = "Physically based rendering with real-time reflections, volumetric fog, and specular highlights.",
            fpsGain = "RTX Active (Needs Overclock)"
        )
    )

    val skins = listOf(
        SkinModel(
            id = "cyber_creeper",
            name = "Cyber Creeper 2M",
            primaryColorHex = 0xFF00FF66,
            accentColorHex = 0xFF0D1B12,
            hairColorHex = 0xFF003311,
            eyeColorHex = 0xFF00E5FF,
            hasArmor = true,
            description = "Neon green cybernetic Creeper infused with overclocked 2M FPS energy."
        ),
        SkinModel(
            id = "diamond_overdrive",
            name = "Diamond Overdrive",
            primaryColorHex = 0xFF00E5FF,
            accentColorHex = 0xFF0B3B48,
            hairColorHex = 0xFF1E293B,
            eyeColorHex = 0xFF00FF66,
            hasArmor = true,
            description = "Polished diamond battle armor with glowing neon trim."
        ),
        SkinModel(
            id = "netherite_titan",
            name = "Netherite Titan",
            primaryColorHex = 0xFF4A3E4E,
            accentColorHex = 0xFFFF7700,
            hairColorHex = 0xFF18181B,
            eyeColorHex = 0xFFFF3333,
            hasArmor = true,
            description = "Forged in ancient debris with molten fire highlights."
        ),
        SkinModel(
            id = "ender_lord",
            name = "Ender Lord",
            primaryColorHex = 0xFF180B26,
            accentColorHex = 0xFFA855F7,
            hairColorHex = 0xFF000000,
            eyeColorHex = 0xFFE879F9,
            hasArmor = false,
            description = "Void entity draped in galactic purple ender mist."
        ),
        SkinModel(
            id = "steve_classic",
            name = "Classic Steve",
            primaryColorHex = 0xFF0088AA,
            accentColorHex = 0xFF2B3A7A,
            hairColorHex = 0xFF4A2B15,
            eyeColorHex = 0xFF253B80,
            hasArmor = false,
            description = "The timeless Minecraft Bedrock legend."
        ),
        SkinModel(
            id = "alex_classic",
            name = "Classic Alex",
            primaryColorHex = 0xFF5B8731,
            accentColorHex = 0xFF553C26,
            hairColorHex = 0xFFB35928,
            eyeColorHex = 0xFF356133,
            hasArmor = false,
            description = "Adventurous trailblazer ready for any Bedrock seed."
        )
    )

    val capes = listOf(
        CapeModel(
            id = "turbo_elytra_2m",
            name = "2,000,000 FPS Turbo Elytra",
            colorHex = 0xFFFFB703,
            secondaryHex = 0xFFFF5400,
            description = "Legendary golden jet-stream cape awarded to 2M FPS speedrunners."
        ),
        CapeModel(
            id = "anniversary_15",
            name = "15th Anniversary Creeper",
            colorHex = 0xFF22C55E,
            secondaryHex = 0xFF14532D,
            description = "Celebratory Mojang 15-year edition with glowing pixel face."
        ),
        CapeModel(
            id = "migrator",
            name = "Migrator Cape",
            colorHex = 0xFF831843,
            secondaryHex = 0xFFBE123C,
            description = "Exclusive royal crimson cape with golden crest."
        ),
        CapeModel(
            id = "bedrock_founder",
            name = "Founder's Bedrock Cape",
            colorHex = 0xFF0284C7,
            secondaryHex = 0xFF0F172A,
            description = "Deep cobalt cape honoring early Bedrock Edition explorers."
        ),
        CapeModel(
            id = "mojang_studios",
            name = "Mojang Studios Red",
            colorHex = 0xFFDC2626,
            secondaryHex = 0xFF7F1D1D,
            description = "Official red cape with white Mojang Studios insignia."
        )
    )
}
