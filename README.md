
# SetHome Plugin

![SetHome Screenshot](https://i.imgur.com/GK3eEFD.png)

## About

SetHome is a lightweight, modern Minecraft plugin for Spigot servers that lets players set, teleport to, delete, and list their homes. It is highly configurable, supports multiple storage backends, and is designed for ease of use and server performance.

---

## ✨ Features

- Set, teleport to, delete, and list named homes
- Per-player home files or database storage (YAML, SQLite, or MySQL)
- Configurable maximum number of homes per player
- Cooldowns and warmups for commands
- Cancel teleport on player movement (warmup)
- Respawn at home on death (optional)
- Enderman warp sound on teleport (optional)
- Fully customizable messages (config.yml)
- Autocompletion for all commands
- Console notifications for plugin updates
- Automatic config migration/updating
- No permissions required by default (easy for all players)

---

## ⚙️ Configuration Highlights

All options are in `config.yml`. Example:

```yaml
extra:
 storage-type: yaml # yaml, sqlite, or mysql
 max-homes-per-player: 3
 mysql:
  host: localhost
  port: 3306
  database: sethome
  username: root
  password: password
```

---

## 🗂️ Storage Backends

- **YAML** (default): Each player's homes are stored in a separate file in `plugins/SetHome/homes/`.
- **SQLite**: Homes are stored in a local SQLite database file.
- **MySQL**: Homes are stored in a remote or local MySQL database (see config example above).

---

## 📝 Commands

| Command      | Description                |
| ------------ | -------------------------- |
| /sethome     | Set your home              |
| /home        | Teleport to your home      |
| /deletehome  | Delete your home           |
| /listhome    | List all your homes        |

---

## 🛠️ Support & Issues

- For help, bug reports, or feature requests, use the [GitHub Issues](https://github.com/KingIronMan2011/SetHomePlugin/issues) page.
- The latest version is intended for the latest Minecraft/Spigot release.

---

## 📋 License & Credits

Created by KingIronMan2011. See LICENSE for details.
