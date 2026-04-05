# SessionTrackerCP

## Русский

Продвинутая система отслеживания сессий и защиты от гриферства для Minecraft серверов.

### Возможности
- Отслеживание сессий игроков (IP, UUID, координаты)
- Автоматическое обнаружение гриферства
- Гибкие команды при нарушениях с префиксами `asConsole!` и `asPlayer!`
- Уведомления для персонала с возможностью отключения
- Поддержка PlaceholderAPI
- Система предупреждений
- Мультиязычность

### Команды
- `/stcp reload` - Перезагрузка плагина
- `/stcp notifications` - Вкл/Выкл уведомления для персонала
- `/stcp view <игрок>` - Просмотр нарушений игрока
- `/stcp warn add/remove <1-100> [игрок]` - Управление предупреждениями
- `/stcp info [игрок]` - Информация об игроке

### Заполнители (Placeholders)
- `%stcp_warn_count%` - Количество предупреждений игрока
- `%stcp_warn_count_{игрок}%` - Количество предупреждений указанного игрока
- `%stcp_violations%` - Количество нарушений игрока
- `%stcp_violations_{игрок}%` - Количество нарушений указанного игрока

---

## English

Advanced session tracking and grief prevention system for Minecraft servers.

### Features
- Track player sessions (IP, UUID, coordinates)
- Automatic grief detection
- Flexible violation commands with `asConsole!` and `asPlayer!` prefixes
- Staff notifications with toggle
- PlaceholderAPI support
- Warning system
- Multi-language support

### Commands
- `/stcp reload` - Reload plugin
- `/stcp notifications` - Toggle staff notifications
- `/stcp view <player>` - View player violations
- `/stcp warn add/remove <1-100> [player]` - Manage warnings
- `/stcp info [player]` - Player information

### Placeholders
- `%stcp_warn_count%` - Player's warning count
- `%stcp_warn_count_{player}%` - Warning count of specific player
- `%stcp_violations%` - Player's violation count
- `%stcp_violations_{player}%` - Violation count of specific player
