# TestServerMinecraft

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

- #### MC.Server must enforce whitelist for it to be managed (server.properties->enforce-white-list).
- #### MC.Server must iplement a whitelist for it to be managed (server.properties->white-list).
- #### MC.Server must have a bound IP (server.properties->server-ip).
- #### (Comming soon) Player can request to be registered via Discord® but MC.whitelist always have the last word for allowed connecions.
- #### Upon registeration player have 24h to log into the MC.server to confirm their identity and be whitelisted.
- #### Players Minecraft® UUID must be unique on the server.
- #### A Minecraft® account can only be linked to `1` Discord® account.
      - This could be updated in the future allowing player to update their Discord® association to MC.
      - Therefore if you have multiple MC.accounts you can register them with the same Discord® account.

## Prerequesites:
 - ## Be sure to update your database: 
 ```sql
CREATE TABLE `users` (
  `id` bigint unsigned NOT NULL AUTO_INCREMENT,
  `mc_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `discord_id` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `allowed` tinyint DEFAULT '0',
  `msg_id` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `updated_at` datetime DEFAULT CURRENT_TIMESTAMP,
  `created_at` datetime DEFAULT NULL,
  `accepted_by` bigint unsigned DEFAULT NULL,
  `revoked_by` bigint unsigned DEFAULT NULL,
  `mc_uuid` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL,
  `confirmed` tinyint DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `id_UNIQUE` (`id`),
  UNIQUE KEY `idx_users_discord_name` (`discord_id`,`mc_name`),
  UNIQUE KEY `idx_users_mc_uuid` (`mc_uuid`),
  KEY `accepted_by` (`accepted_by`),
  KEY `revoked_by` (`revoked_by`),
  CONSTRAINT `users_ibfk_1` FOREIGN KEY (`accepted_by`) REFERENCES `users` (`id`),
  CONSTRAINT `users_ibfk_2` FOREIGN KEY (`revoked_by`) REFERENCES `users` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci
 ```

## Configurations:

- ### Setup the `private vars` within the [ConfigurationManager](src/main/java/configs/ConfigManager.java) file.
- ### Setup and run [build-WJE.bat](build-WJE.bat) a backup of your conf. will `automaticly` be added to [backups folder](/backups).
