# CraftDumper [![CurseForge Downloads](http://cf.way2muchnoise.eu/full_354281_downloads.svg)](https://www.curseforge.com/minecraft/mc-mods/craftdumper)
[![Discord](https://img.shields.io/discord/136085738151346176?color=7289DA&logo=discord&logoColor=white)](https://discord.gg/tkDUmpQ)
[![Build Status](https://ci.pearx.net/job/pearxteam/job/craftdumper/job/1.14-master/badge/icon?subject=1.14-master)](https://ci.pearx.net/job/pearxteam/job/craftdumper/job/1.14-master/)
[![Build Status](https://ci.pearx.net/job/pearxteam/job/craftdumper/job/1.14-develop/badge/icon?subject=1.14-develop)](https://ci.pearx.net/job/pearxteam/job/craftdumper/job/1.14-develop/)

A Minecraft Forge mod that allows you to dump game information such as blocks, items and many more.

## Currently Supported Stuff
* Advancements (`advancements`)
* Attributes (`attributes`)
* Biomes (`biomes`)
* Blasting Recipes (`blasting_recipes`)
* Block Tags (`tags_block`)
* Blocks (`blocks`)
* Campfire Cooking Recipes (`campfire_cooking_recipes`)
* Capabilities (`capabilities`)
* Containers (`containers`)
* Dimensions (`dimensions`)
* Enchantments (`enchantments`)
* Entities (`entities`)
* Entity Tags (`tags_entity`)
* Fluid Tags (`tags_fluid`)
* Fluids (`fluids`)
* Food (`food`)
* Item Stacks (`item_stacks`)
* Item Tags (`tags_item`)
* Loot Tables (`loot_tables`)
* Models (`models`)
* Paintings (`paintings`)
* Particles (`particles`)
* Potion Effects (`potion_effects`)
* Potions (`potions`)
* Shaped Recipes (`shaped_recipes`)
* Shapeless Recipes (`shapeless_recipes`)
* Smelting Recipes (`smelting_recipes`)
* Smoking Recipes (`smoking_recipes`)
* Sound Events (`sound_events`)
* Stats (`stats`)
* Stonecutting Recipes (`stonecutting_recipes`)
* Tile Entities (`tile_entities`)
* Villager Activities (`villager_activities`)
* Villager Memory Modules (`villager_memory_modules`)
* Villager Points of Interest (`villager_points_of_interest`)
* Villager Professions (`villager_professions`)
* Villager Schedules (`villager_schedules`)
* Villager Sensors (`villager_sensors`)

You can submit a feature request [here](https://github.com/pearxteam/craftdumper/issues) :).

## Usage Guide
[> Demonstration Video <](https://streamable.com/uw4n5)

To create a dump, use the `/craftdumper <dumper name> [full|amounts|data]` command, where
* `<dumper name>` - a specific dumper name (see [Currently Supported Stuff](#Currently-Supported-Stuff)) or `all` if you want to dump all the things.
* `[full|amounts|data]` - choose `data` to save only dump data, `amounts` to save only dump amounts, `full` to save both dump data and amounts.

All the dumper outputs will be placed in the `.minecraft/craftdumper` directory.

## Dependencies
This mod requires [Kottle](https://www.curseforge.com/minecraft/mc-mods/kottle).

## Usage in Modpacks
You can use this mod in any modpack!