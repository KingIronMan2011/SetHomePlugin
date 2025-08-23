
# SetHome Plugin

## About

SetHome is a lightweight, modern Minecraft plugin for Spigot servers that lets players set, teleport to, delete, and list their homes. It is highly configurable, supports multiple storage backends, and is designed for ease of use and server performance.

**Java 17 Required:** This plugin requires Java 17 or newer to build and run. Make sure your server and build environment use Java 17.

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
- No permissions required by default (easy for all playeryess)
- Integrated with [bStats](https://bstats.org/) for anonymous plugin usage metrics
- Public Java API for other plugins to interact with SetHome programmatically
- Importers for other home plugins (Essentials-style YAML importer available via `/shp import`)

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

## 🏗️ Building & Installing

Build the plugin with Java 17. From the project root you can use Gradle or the wrapper if present.

PowerShell (recommended on Windows):

```powershell
# if you have the Gradle wrapper
.\gradlew.bat clean build
# or with system Gradle
gradle clean build
```

After building, copy the generated JAR from `build/libs/` (or `target/` for Maven users) into your server's `plugins/` folder and start the server.

---

## 🗂️ Storage Backends

- **YAML** (default): Each player's homes are stored in a separate file in `plugins/SetHome/homes/`.
- **SQLite**: Homes are stored in a local SQLite database file.
- **MySQL**: Homes are stored in a remote or local MySQL database (see config example above).

---

## 🌐 Localization

The plugin supports multiple language files under `plugins/SetHome/languages/` at runtime. To change the language set `extra.language` in `config.yml` (for example `extra.language: fr`).

On first startup the plugin will migrate and create missing language keys automatically. If you add or update language files in `src/main/resources/languages/`, copy them to `plugins/SetHome/languages/` to customize messages.

---

## 📝 Commands

| Command      | Description                |
| ------------ | -------------------------- |
| /sethome     | Set your home              |
| /home        | Teleport to your home      |
| /deletehome  | Delete your home           |
| /listhome    | List all your homes        |
| /shp help    | Lists all commands         |
| `/shp import <type> <file>` | Import homes from another plugin export (admin only) |

---

## 💾 Backup & Restore

Use these commands from the server console or in-game (requires appropriate permission):

```text
/sethome backup              # creates a backup file under plugins/SetHome/backups/
/sethome restore <filename>  # restores homes from a backup file
```

## 🔁 Importing from other plugins

You can import homes from supported third-party exports. Currently supported:

- `essentials` — Imports Essentials-style YAML files exported from Essentials/EssentialsX.

Usage (upload the exported file to the plugin folder, then run as an OP):

```text
/shp import essentials <filename.yml>
```

The plugin will write imported homes into `plugins/SetHome/homes/<player-uuid>.yml` and report how many homes were imported.

Note: Name -> UUID resolution relies on server-known players; consider providing a UUID-keyed export for best results.

Backups include YAML and SQLite data where applicable. For MySQL consider using a database dump tool for full exports.

---

## 📊 Metrics & bStats

This plugin uses [bStats](https://bstats.org/) to collect anonymous usage statistics. These metrics help the developer understand plugin usage and improve future updates. You can opt out at any time by editing the `plugins/bStats/config.yml` file on your server.

---

## 🛠️ Support & Issues

- For help, bug reports, or feature requests, use the [GitHub Issues](https://github.com/KingIronMan2011/SetHomePlugin/issues) page.
- The latest version is intended for the latest Minecraft/Spigot release.

---

## 📋 License & Credits

Created by KingIronMan2011. See [LICENSE](/LICENSE.md) for details.
