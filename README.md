```markdown
# SessionTrackerCP

Продвинутая система отслеживания сессий и защиты от гриферства для Minecraft серверов.

## Возможности
- Отслеживание сессий игроков (IP, UUID, координаты, время в игре)
- Автоматическое обнаружение гриферства с проверкой радиуса и времени
- Гибкие команды при нарушениях с префиксами `asConsole!` и `asPlayer!`
- Команды при достижении максимального количества предупреждений
- Уведомления для персонала с возможностью отключения
- Поддержка PlaceholderAPI
- Система предупреждений с настраиваемым лимитом через права
- Право обхода проверок для администраторов (`stcp.bypass`)
- Мультиязычность (поддержка lang.yml)
- Автоматическое обновление конфигурации без сброса настроек

## Команды
| Команда | Описание |
|---------|----------|
| `/stcp reload` | Перезагрузка плагина |
| `/stcp notifications` | Вкл/Выкл уведомления для персонала |
| `/stcp view <игрок>` | Просмотр нарушений игрока |
| `/stcp warn add <1-100> [игрок]` | Добавить предупреждения |
| `/stcp warn remove <1-100> [игрок]` | Снять предупреждения |
| `/stcp info [игрок]` | Информация об игроке |

## Права (Permissions)
| Право | Описание |
|-------|----------|
| `sessiontracker.user` | Базовые права |
| `sessiontracker.staff` | Получение уведомлений о нарушениях |
| `sessiontracker.admin` | Полный доступ к командам |
| `stcp.bypass` | Обход проверок (для админов) |
| `stcp.maxwarns.5` | Максимум 5 предупреждений |
| `stcp.maxwarns.10` | Максимум 10 предупреждений |
| `stcp.maxwarns.20` | Максимум 20 предупреждений |

## Заполнители (Placeholders)

**Внутренние (`{}`)** — используются в конфигах и командах:
```

{player}     - имя нарушителя
{victim}     - имя жертвы
{cause}      - тип нарушения
{reason}     - причина
{x}, {y}, {z} - координаты
{world}      - название мира
{uuid}       - UUID нарушителя
{ip}         - IP адрес
{warns}      - текущее количество предупреждений
{maxwarns}   - максимальное количество предупреждений

```

**Внешние (`%`)** — для PlaceholderAPI:
| Заполнитель | Описание |
|-------------|----------|
| `%stcp_warns%` | Предупреждения игрока |
| `%stcp_warns_{игрок}%` | Предупреждения указанного игрока |
| `%stcp_maxwarns%` | Макс. предупреждений игрока |
| `%stcp_maxwarns_{игрок}%` | Макс. предупреждений указанного игрока |
| `%stcp_violations%` | Нарушения игрока |
| `%stcp_violations_{игрок}%` | Нарушения указанного игрока |
| `%stcp_player%` | Имя текущего игрока |
| `%stcp_player_uuid%` | UUID текущего игрока |
| `%stcp_player_ip%` | IP текущего игрока |

## Конфигурация

После первого запуска отредактируйте `plugins/SessionTrackerCP/config.yml`

```yaml
# Команды при нарушении
violation-commands:
  enabled: false
  commands:
    - "asConsole! broadcast &c[!] &6{player} &7попытался загриферить &c{victim}"
    - "asPlayer! msg {player} &cУ тебя {warns}/{maxwarns} предупреждений!"

# Максимальное количество предупреждений
max-warns:
  default: 10
  permission: "stcp.maxwarns."

# Команды при достижении лимита предупреждений
max-warns-commands:
  enabled: true
  commands:
    - "asConsole! tempban {player} 1h Превышение лимита предупреждений"

# Право обхода проверок
bypass-permission: "stcp.bypass"
```

———————————

```markdown
# SessionTrackerCP

Advanced session tracking and grief prevention system for Minecraft servers.

## Features
- Track player sessions (IP, UUID, coordinates, playtime)
- Automatic grief detection with radius and time checks
- Flexible violation commands with `asConsole!` and `asPlayer!` prefixes
- Commands on reaching maximum warning limit
- Staff notifications with toggle
- PlaceholderAPI support
- Warning system with configurable limit via permissions
- Bypass permission for administrators (`stcp.bypass`)
- Multi-language support (lang.yml)
- Automatic configuration update without resetting settings

## Commands
| Command | Description |
|---------|-------------|
| `/stcp reload` | Reload plugin |
| `/stcp notifications` | Toggle staff notifications |
| `/stcp view <player>` | View player violations |
| `/stcp warn add <1-100> [player]` | Add warnings |
| `/stcp warn remove <1-100> [player]` | Remove warnings |
| `/stcp info [player]` | Player information |

## Permissions
| Permission | Description |
|------------|-------------|
| `sessiontracker.user` | Basic permissions |
| `sessiontracker.staff` | Receive violation notifications |
| `sessiontracker.admin` | Full command access |
| `stcp.bypass` | Bypass checks (for admins) |
| `stcp.maxwarns.5` | Maximum 5 warnings |
| `stcp.maxwarns.10` | Maximum 10 warnings |
| `stcp.maxwarns.20` | Maximum 20 warnings |

## Placeholders

**Internal (`{}`)** — used in configs and commands:
```

{player}     - violator name
{victim}     - victim name
{cause}      - violation type
{reason}     - reason
{x}, {y}, {z} - coordinates
{world}      - world name
{uuid}       - violator UUID
{ip}         - IP address
{warns}      - current warning count
{maxwarns}   - maximum warning count

```

**External (`%`)** — for PlaceholderAPI:
| Placeholder | Description |
|-------------|-------------|
| `%stcp_warns%` | Player's warnings |
| `%stcp_warns_{player}%` | Warnings of specific player |
| `%stcp_maxwarns%` | Player's max warnings |
| `%stcp_maxwarns_{player}%` | Max warnings of specific player |
| `%stcp_violations%` | Player's violations |
| `%stcp_violations_{player}%` | Violations of specific player |
| `%stcp_player%` | Current player name |
| `%stcp_player_uuid%` | Current player UUID |
| `%stcp_player_ip%` | Current player IP |

## Configuration

After first run, edit `plugins/SessionTrackerCP/config.yml`

```yaml
# Violation commands
violation-commands:
  enabled: false
  commands:
    - "asConsole! broadcast &c[!] &6{player} &7attempted to grief &c{victim}"
    - "asPlayer! msg {player} &cYou have {warns}/{maxwarns} warnings!"

# Maximum warnings limit
max-warns:
  default: 10
  permission: "stcp.maxwarns."

# Commands on reaching warning limit
max-warns-commands:
  enabled: true
  commands:
    - "asConsole! tempban {player} 1h Warning limit exceeded"

# Bypass permission
bypass-permission: "stcp.bypass"
```
