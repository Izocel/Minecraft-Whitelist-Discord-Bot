## This is the free (lite) version of this plugin:
 If you're interested in an `advanced` version or `custom` version you can email me at <a href="mailto:webdevteam@rvdprojects.com">webdevteam@rvdprojects.com</a>

# Minecraft-Whitelist-Discord-Bot <img src="https://mirrors.creativecommons.org/presskit/buttons/88x31/png/by-nc-nd.eu.png" alt="by-nc-nd" width="5%"> <img src="https://creativecommons.org/wp-content/uploads/2022/07/CCLogoColorPop1.gif" width="3%">

![rvdprojectBanner - Copy](https://user-images.githubusercontent.com/68454661/193481312-edd8840d-e046-4e50-bd4f-4cd88e87f597.jpg)


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

## Release Key-Features:
- MC.Command: `dig-player` -> Used to retrieves information of Minecraft® player with uuid or pseudo.
- MC.Server must enforce whitelist for it to be managed (server.properties->enforce-white-list).
- MC.Server must have a bound IP for textual purpose (server.properties->server-ip).
- Player can request to be registered via Discord® but MC.whitelist always have the last word for allowed connecions.
- Upon registeration player have 24h to log into the MC.server to confirm their identity and be whitelisted else the player will have to make a new registration.
- Players Minecraft® UUID and pseudo must be unique on the server.

- ### A Minecraft® account can only be linked to `1` Discord® account.
      - This could be updated in the future allowing player to update their Discord® association to MC.
      - Therefore if you have multiple MC.accounts you can register them with the same Discord® account.

## Prerequesites:
 - ## Be sure to create a table wtih this schema in your database: 
 ```sql
CREATE DATABASE IF NOT EXISTS whitelist_je;
USE whitelist_je;

CREATE TABLE `wje_users` (
  `id` bigint unsigned NOT NULL AUTO_INCREMENT,
  `discord_id` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL,
  `discord_tag` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL,
  `lang` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT 'fr',
  `updated_at` datetime DEFAULT CURRENT_TIMESTAMP,
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `id_UNIQUE` (`id`),
  UNIQUE KEY `discord_id_UNIQUE` (`discord_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin

CREATE TABLE IF NOT EXISTS `wje_java_data` (
  `id` bigint unsigned NOT NULL AUTO_INCREMENT,
  `user_id` bigint unsigned NOT NULL,
  `pseudo` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL,
  `uuid` varchar(45) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL,
  `allowed` tinyint DEFAULT NULL,
  `confirmed` tinyint DEFAULT NULL,
  `msg_id` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL,
  `accepted_by` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL,
  `revoked_by` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL,
  `updated_at` datetime DEFAULT CURRENT_TIMESTAMP,
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `id_UNIQUE` (`id`),
  UNIQUE KEY `user_id_UNIQUE` (`user_id`),
  UNIQUE KEY `java_uuid_UNIQUE` (`uuid`),
  KEY `accepted_by` (`accepted_by`),
  KEY `revoked_by` (`revoked_by`),
  FOREIGN KEY (user_id) REFERENCES wje_users(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin;

CREATE TABLE IF NOT EXISTS `wje_bedrock_data` (
  `id` bigint unsigned NOT NULL AUTO_INCREMENT,
  `user_id` bigint unsigned NOT NULL,
  `pseudo` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL,
  `uuid` varchar(45) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL,
  `allowed` tinyint DEFAULT NULL,
  `confirmed` tinyint DEFAULT NULL,
  `msg_id` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL,
  `accepted_by` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL,
  `revoked_by` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL,
  `updated_at` datetime DEFAULT CURRENT_TIMESTAMP,
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `id_UNIQUE` (`id`),
  UNIQUE KEY `user_id_UNIQUE` (`user_id`),
  UNIQUE KEY `bedrock_uuid_UNIQUE` (`uuid`),
  KEY `accepted_by` (`accepted_by`),
  KEY `revoked_by` (`revoked_by`),
  FOREIGN KEY (user_id) REFERENCES wje_users(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin;
 ```
## Configurations:

- ### Setup the `private vars` within the [ConfigurationManager](src/main/java/configs/ConfigManager.java) file.
- ### Setup and run [build-WJE.bat](build-WJE.bat) a backup of your conf. will `automaticly` be added to [backups folder](/backups).

## How to change your command names in the various messages sent to users (aliases won't overide textual commands names):

**Those neeed to be done before building**

- ### Before Setup the `commands section` within the [Plugin.yml](src/main/resources/plugin.yml) file.
- Plugin.yml example:

      commands:
            <cmdName>:
                  description: This is a demo command.
                  usage: !! I recommend not changing this except the name part !!

- ### Setup the associated `private vars` within the [ConfigurationManager](src/main/java/configs/ConfigManager.java) file.

- ### Setup any aliases you want in your server `commands.yml` file.
- commands example:

      command-block-overrides: []
      ignore-vanilla-permissions: false
      aliases:
            icanhasbukkit:
            - version $1-
            link:
            - wje-link (or your designated cmd name in previous steps)


- ### Run [build-WJE.bat](build-WJE.bat).

## Environement setup:

- ### Comming soon...

 
# Project License: [license.md](license.md)
