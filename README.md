# GTA6 City Build Minecraft Plugin

This Minecraft plugin transforms your server into a GTA 6-inspired City Build experience, featuring a coin economy system, purchasable crates at the spawn, and a powerful admin panel. Players can earn and spend coins, buy basic, advanced, and legendary crates with unique rewards, and interact with a wanted level system that adds GTA-style gameplay elements including police chase effects and action bar notifications.

## Install

1. Download the plugin JAR file from the release page.
2. Place the JAR file in your server's `plugins` folder.
3. Restart your server or run `/reload confirm` (if supported by your server software).
4. Verify the plugin is enabled by checking the console for the message "CityBuild plugin enabled."

## Use

### Economy Commands

- `/coins` - Check your current coin balance.
- `/coinspay <player> <amount>` - Send coins to another player.
- `/coinsadmin <add|remove|set> <player> <amount>` - Admin: manage player coin balances (requires `citybuild.coins.admin` permission).

### Spawn and Crate Shop

- `/spawn` - Teleport to the world spawn location.
- `/crateshop` or `/buycrate` - Open the crate shop. Limited to players within 30 blocks of spawn.
- Purchase Basic Crate (20 coins), Advanced Crate (60 coins), or Legendary Crate (150 coins) from the shop GUI.
- Right-click any crate in your inventory to open it and receive a random reward.

### Admin Panel

- `/adminpanel` - Opens the admin panel shop GUI, where you can purchase an Admin Panel item for 200 coins.
- The Admin Panel item costs 200 coins and requires the `citybuild.admin` permission to use.
- Right-click the Admin Panel item to open the admin tools menu, which provides access to:
  - Give Coins: Instructions for using `/coinsadmin`
  - Give God Items: Grants a set of highly-enchanted netherite armor and sword
  - Set Wanted Level: Instructions for using `/setwanted`

### GTA 6 Wanted System

- `/wanted` - Display your current wanted level.
- `/setwanted <player> <level>` - Admin: Set a player's wanted level from 0 to 5 (requires `citybuild.admin` permission).
- Player kills trigger automatic wanted level increases for the killer.
- Wanted level 1-2 shows yellow star indicators; level 3-5 shows red stars with lightning effects at the player's location.

### Permissions

- `citybuild.coins.admin` - Allows use of `/coinsadmin` (default: OP).
- `citybuild.admin` - Allows use of the admin panel and `/setwanted` (default: OP).

The plugin uses a `coins.yml` file in the plugin folder to save player balances automatically when the server stops.

---

More Minecraft plugin projects by EnderDevelopment: https://github.com/EnderDevelopment

Project page: https://dash.enderdevelopment.com/community/5c50622f-9ea4-4d61-b795-c61b74483ce0?utm_source=github&utm_medium=readme&utm_campaign=auto_publish&utm_content=5c50622f-9ea4-4d61-b795-c61b74483ce0
