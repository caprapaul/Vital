# Vital
Vital is a Spigot/Bukkit plugin which features vital commands for a minecraft server.

### Target version:
1.14

## Commands
Vital currently features the following commands
```
/tpa <player>     - Send a teleport request to a player on the server.
/tpahere <player> - Send a teleport invite to a player on the server.
/tpaccept         - Accept a teleport request.
/tpdeny           - Deny a teleport request.

/warp <name>      - Teleport to a warp.
/warps            - Display available warp locations.
/setwarp <name>   - Set a warp location.
/delwarp <name>   - Delete a warp location.

/back             - Teleport to the previous location.

/sethome          - Set a default home, with the name "default".
/sethome <name>   - Set a home with a specific name.
/home             - Teleport to default home.
/home <name>      - Teleport to a home with a specific name.
```

## Permissions
Permissions for the currently featured commands
```
vital.tpa                       - Allows you to use the /tpa command.
vital.tpa.overridecooldown:     - Allows you to bypass /tpa cooldown.
vital.tpahere                   - Allows you to use the /tpahere command.
vital.tpahere.overridecooldown: - Allows you to bypass /tpahere cooldown.
vital.tpaccept                  - Allows you to use the /tpaccept command.
vital.tpdeny                    - Allows you to use the /tpdeny command.

vital.warp:                     - Allows you to use the /warp command.
vital.warps:                    - Allows you to use the /warps command.
vital.setwarp:                  - Allows you to use the /setwarp command.
vital.delwarp:                  - Allows you to use the /delwarp command.

vital.back:                     - Allows you to use the /back command.
vital.back.overridecooldown:    - Allows you to bypass /back cooldown.

vital.homes.*:                  - Gives access to [home, homes, sethome, delhome] commands.
vital.home:                     - Allows you to use the /home command.
vital.homes:                    - Allows you to use the /homes command.
vital.loadhomes:                - Allows you to use the /loadhomes command.
vital.savehomes:                - Allows you to use the /savehomes command.
vital.sethome:                  - Allows you to use the /sethome command.
vital.delhome:                  - Allows you to use the /delhome command.
vital.heal:                     - Allows you to use the /heal command
vital.feed:                     - Allows you to use the /feed command
```
