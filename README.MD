# CraftDumper [![CurseForge Downloads](http://cf.way2muchnoise.eu/full_354281_downloads.svg)](https://www.curseforge.com/minecraft/mc-mods/craftdumper)
[![Discord](https://img.shields.io/discord/136085738151346176?color=7289DA&logo=discord&logoColor=white)](https://discord.gg/tkDUmpQ)
[![Build Status](https://ci.pearx.net/job/pearxteam/job/craftdumper/job/1.12-master/badge/icon?subject=1.12-master)](https://ci.pearx.net/job/pearxteam/job/craftdumper/job/1.12-master/)
[![Build Status](https://ci.pearx.net/job/pearxteam/job/craftdumper/job/1.12-develop/badge/icon?subject=1.12-develop)](https://ci.pearx.net/job/pearxteam/job/craftdumper/job/1.12-develop/)

A Minecraft Forge mod that allows you to dump game information such as blocks, items and many more.

## Currently Supported Stuff
* Advancements (`advancements`)
* Attributes (`attributes`)
* Biomes (`biomes`)
* Blocks (`blocks`)
* Capabilities (`capabilities`)
* Enchantments (`enchantments`)
* Entities (`entities`)
* Fluids (`fluids`)
* Food (`food`)
* Item Stacks (`item_stacks`)
* Loot Tables (`loot_tables`)
* Models (`models`)
* Ore Dictionary (`ore_dictionary`)
* Potions (`potions`)
* Shaped Recipes (`shaped_recipes`)
* Shapeless Recipes (`shapeless_recipes`)
* Smelting Recipes (`smelting_recipes`)
* Sound Events (`sound_events`)
* Tile Entities (`tile_entities`)
* Villager Professions (`villager_professions`)
* World Generators (`world_generators`)

You can submit a feature request [here](https://github.com/pearxteam/craftdumper/issues) :).

## Usage Guide
[> Demonstration Video <](https://streamable.com/uw4n5)

To create a dump, use the `/craftdumper <dumper name> [full|amounts|data]` command, where
* `<dumper name>` - a specific dumper name (see [Currently Supported Stuff](#Currently-Supported-Stuff)) or `all` if you want to dump all the things.
* `[full|amounts|data]` - choose `data` to save only dump data, `amounts` to save only dump amounts, `full` to save both dump data and amounts.

All the dumper outputs will be placed in the `.minecraft/craftdumper` directory.

## Dependencies
This mod requires [Shadowfacts' Forgelin](https://www.curseforge.com/minecraft/mc-mods/shadowfacts-forgelin).

## Usage in Modpacks
You can use this mod in any modpack!