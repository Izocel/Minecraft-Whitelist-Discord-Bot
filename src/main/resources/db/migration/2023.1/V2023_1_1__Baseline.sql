CREATE TABLE IF NOT EXISTS `wje_users` (
 `id` bigint unsigned NOT NULL AUTO_INCREMENT,
 `discord_id` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL,
 `discord_tag` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL,
 `lang` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT 'fr',
 `updated_at` datetime DEFAULT CURRENT_TIMESTAMP,
 `created_at` datetime DEFAULT CURRENT_TIMESTAMP,
 PRIMARY KEY (`id`),
 UNIQUE KEY `id_UNIQUE` (`id`),
 UNIQUE KEY `discord_id_UNIQUE` (`discord_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin;

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
   UNIQUE KEY `bedrock_uuid_UNIQUE` (`uuid`),
 KEY `accepted_by` (`accepted_by`),
 KEY `revoked_by` (`revoked_by`),
 FOREIGN KEY (user_id) REFERENCES wje_users(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin;