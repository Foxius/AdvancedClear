### AdvancedClear Plugin
![logo](https://cdn.modrinth.com/data/cached_images/ad277b25e300cd6fa1440312e5ba4bdde7aa4daa.png)
**Overview:**
AdvancedClear is a Minecraft plugin designed to help server administrators keep their worlds clean by automatically removing dropped items at configurable intervals. The plugin also allows players to cancel upcoming cleanups and provides detailed feedback on the cleanup process, including which items were removed and who canceled the cleanup.

**Features:**

- **Scheduled Cleanups:**
  - Automatically clears dropped items at configurable intervals.
  - Sends warnings to players before the cleanup occurs, allowing them to cancel if necessary.

- **Custom Messages:**
  - Customizable messages in multiple languages (English and Russian).
  - In-game notifications using `tellraw` commands for enhanced message formatting and clickable events.

- **Player Interaction:**
  - Players can cancel the upcoming cleanup by clicking a special message or using the `/cancelcleanup` command.
  - Logs the player who canceled the cleanup and provides this information via placeholders.

- **Logging:**
  - Logs all cleanup actions and cancellations to a file for later review.
  
![logs in chat](https://cdn.modrinth.com/data/cached_images/9c7853d607f66eb1984484bd3b2a1d13ecfc4f57.jpeg)

![logs in chat](https://cdn.modrinth.com/data/cached_images/8e3e2c303f36ae55a8a61fb9e580f0abef6184f2.jpeg)

- **PlaceholderAPI Integration:**
  - Integrates with PlaceholderAPI to provide placeholders for time until the next cleanup and the last player who canceled a cleanup.

**Installation:**
1. Download the AdvancedClear plugin.
2. Place the downloaded file into your server's `plugins` directory.
3. Start or restart your server to load the plugin.
4. Configure the plugin as needed in the generated `config.yml` file.

**Configuration:**
- The `config.yml` file allows server administrators to set the interval for item cleanups and customize messages in different languages.

**Usage:**
- The plugin automatically schedules item cleanups based on the configured interval.
- Players receive warnings before each cleanup and can cancel it by clicking the provided message or using the `/cancelcleanup` command.
- The plugin logs all cleanup actions and cancellations for review.

**Commands:**
- `/cancelcleanup`: Cancels the upcoming item cleanup if the player has the necessary permission (`advancedclear.cancel`).

**Placeholders:**
- `%advancedclear_time_until_next_cleanup%`: Displays the time remaining until the next cleanup.
- `%advancedclear_last_skipper%`: Displays the name of the last player who canceled a cleanup.

**Support:**
- For any issues or suggestions, please visit the plugin's support page or repository.

**Metrics:**
- This plugin uses bStats to collect anonymous usage statistics to help improve the plugin.

**Example Config (`config.yml`):**
```yaml
# AdvancedClear configuration
interval: 600  # Time in seconds between each cleanup

messages:
  en:
    message: "Item cleanup will happen soon. Click to cancel."
    item_list_header: "Items removed during cleanup:"
    removed_message: "Items have been cleared. Total removed: "
    cancel_message: "Cleanup has been canceled."
  ru:
    message: "Очистка предметов произойдёт скоро. Нажмите, чтобы отменить."
    item_list_header: "Удалённые предметы во время очистки:"
    removed_message: "Предметы были очищены. Всего удалено: "
    cancel_message: "Очистка была отменена."
```

AdvancedClear helps keep your server clean and organized by automating item cleanups while providing players with the ability to manage and interact with the cleanup process.
