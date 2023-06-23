# Minecraft-Whitelist-Discord-Bot <img src="https://mirrors.creativecommons.org/presskit/buttons/88x31/png/by-nc-nd.eu.png" alt="by-nc-nd" width="5%"> <img src="https://creativecommons.org/wp-content/uploads/2022/07/CCLogoColorPop1.gif" width="3%">

## By using this product your `agree` to this end-user license agreement: [(EULA)](https://github.com/Izocel/Minecraft-Whitelist-Discord-Bot/files/9796398/EULA.pdf)

If you're interested in an `advanced` version or `custom` version you can email me at <a href="mailto:webdevteam@rvdprojects.com">webdevteam@rvdprojects.com</a>

## License: [license.md](license.md)

```
 __       __  __        __    __                __  __              __               _____
/  |  _  /  |/  |      /  |  /  |              /  |/  |            /  |             /     |
$$ | / \ $$ |$$ |____  $$/  _$$ |_     ______  $$ |$$/   _______  _$$ |_            $$$$$ |  ______
$$ |/$  \$$ |$$      \ /  |/ $$   |   /      \ $$ |/  | /       |/ $$   |  ______      $$ | /      \
$$ /$$$  $$ |$$$$$$$  |$$ |$$$$$$/   /$$$$$$  |$$ |$$ |/$$$$$$$/ $$$$$$/  /      |__   $$ |/$$$$$$  |
$$ $$/$$ $$ |$$ |  $$ |$$ |  $$ | __ $$    $$ |$$ |$$ |$$      \   $$ | __$$$$$$//  |  $$ |$$    $$ |
$$$$/  $$$$ |$$ |  $$ |$$ |  $$ |/  |$$$$$$$$/ $$ |$$ | $$$$$$  |  $$ |/  |      $$ \__$$ |$$$$$$$$/
$$$/    $$$ |$$ |  $$ |$$ |  $$  $$/ $$       |$$ |$$ |/     $$/   $$  $$/       $$    $$/ $$       |
$$/      $$/ $$/   $$/ $$/    $$$$/   $$$$$$$/ $$/ $$/ $$$$$$$/     $$$$/         $$$$$$/   $$$$$$$/
     ______     __   __   _____        ______   ______     ______       __     ______     ______     ______   ______
    /\  == \   /\ \ / /  /\  __-.     /\  == \ /\  == \   /\  __ \     /\ \   /\  ___\   /\  ___\   /\__  _\ /\  ___\
    \ \  __<   \ \ \'/   \ \ \/\ \    \ \  _-/ \ \  __<   \ \ \/\ \   _\_\ \  \ \  __\   \ \ \____  \/_/\ \/ \ \___  \
     \ \_\ \_\  \ \__|    \ \____-     \ \_\    \ \_\ \_\  \ \_____\ /\_____\  \ \_____\  \ \_____\    \ \_\  \/\_____\
      \/_/ /_/   \/_/      \/____/      \/_/     \/_/ /_/   \/_____/ \/_____/   \/_____/   \/_____/     \/_/   \/_____/

```

## Supported Versions

| Version | Supported          |
| ------- | ------------------ |
| 2023.^  | :white_check_mark: |
| 2022.3  | :white_check_mark: |
| 2022.2  | :x:                |
| 2022.1  | :x:                |

## Prerequesites:

- maven
- java-18
- minecraft server (local or remote)
- mysql or mariadb server (local or remote)
- discord developper account (bot tokken)

#

## Environement setup:

- ### Comming soon...

#

## QuickStart:

- ### Setup the `private vars` within the [ConfigManager.java](src/main/java/configs/ConfigManager.java) file.
- ### Setup and run [build-WJE.bat](build-WJE.bat) a backup of your conf. will `automaticly` be added to [backups folder](/backups).
- ### Put the `${workspacefolder}\target\WJE-shaded.jar` into you server plugins folder.

#

## Changing commands name:

**Those neeed to be done before building**

- ### Setup the `commands section` within the [plugin.yml](src/main/resources/plugin.yml) file.
- Plugin.yml example:

      commands:
            <cmdName>:
                  description: This is a demo command.
                  usage: !! I recommend not changing this except the name part !!

- ### Setup any aliases you want in your server `commands.yml` file.
- commands.yml example:

      command-block-overrides: []
      ignore-vanilla-permissions: false
      aliases:
            icanhasbukkit:
            - version $1-
            link:
            - wje-link (or your designated cmd name in the ConfigManager.java)


#

## Developper environement setup:

- ### Comming soon...

#


## Npc Citizen
```
/npc2 create Whitelist-Warden --registry Whitelist-Warden
/template apply Whitelist-Warden

# For first time creation
/npc2 skin --url https://www.minecraftskins.com/uploads/skins/2023/05/08/warden-boy-21586715.png?v577
/npc2 hologram add Talk to me to confirm your account
/npc2 command add wje-link --permissions essentials.msg -p
/template create Whitelist-Warden
/citizens save
```
