```markdown
# SessionTrackerCP

Антигрифер плагин для Minecraft серверов. Отслеживает действия игроков, автоматически обнаруживает подозрительные взаимодействия и выдаёт предупреждения.

## Как это работает

Плагин запоминает все действия игроков (установка/поломка блоков, открытие сундуков и других хранилищ). Если игрок взаимодействует с блоком, который недавно трогал другой игрок — это считается нарушением.

Проверяются:
- Расстояние между действиями (настраивается)
- Временной интервал (настраивается)
- Похожие имена игроков (для смягчения проверок)

## Команды

| Команда | Описание |
|---------|----------|
| `/stcp reload` | Перезагрузка плагина и конфигов |
| `/stcp notifications` | Включить/выключить уведомления о нарушениях |
| `/stcp view <ник>` | Показать все нарушения игрока |
| `/stcp warn add <число> <ник>` | Выдать предупреждения |
| `/stcp warn remove <число> <ник>` | Снять предупреждения |
| `/stcp info <ник>` | Информация об игроке (варны, нарушения, IP, UUID) |

## Права

| Право | Описание |
|-------|----------|
| `sessiontracker.user` | Доступ к `/stcp info` |
| `sessiontracker.staff` | Доступ к `/stcp view` и уведомления |
| `sessiontracker.admin` | Доступ ко всем командам |
| `stcp.bypass` | Игрок НЕ получает предупреждения |
| `stcp.protected` | Вещи игрока НЕ мониторятся |
| `stcp.maxwarns.5` | Лимит 5 предупреждений |
| `stcp.maxwarns.10` | Лимит 10 предупреждений |
| `stcp.maxwarns.20` | Лимит 20 предупреждений |

## Заполнители

### Внутренние (для конфига и команд)

| Заполнитель | Что заменяет |
|-------------|--------------|
| `{player}` | Имя нарушителя |
| `{victim}` | Имя жертвы |
| `{cause}` | Тип действия |
| `{reason}` | Причина нарушения |
| `{x} {y} {z}` | Координаты |
| `{world}` | Название мира |
| `{uuid}` | UUID нарушителя |
| `{ip}` | IP адрес |
| `{warns}` | Текущее количество варнов |
| `{maxwarns}` | Максимальный лимит варнов |
| `{time}` | Время нарушения |
| `{amount}` | Количество варнов при операции |
| `{total}` | Общее количество варнов |
| `{status}` | Статус (вкл/выкл) |

### Внешние (для PlaceholderAPI)

| Заполнитель | Что показывает |
|-------------|----------------|
| `%stcp_warns%` | Варны игрока |
| `%stcp_warns_{ник}%` | Варны указанного игрока |
| `%stcp_maxwarns%` | Лимит варнов игрока |
| `%stcp_maxwarns_{ник}%` | Лимит варнов указанного игрока |
| `%stcp_violations%` | Количество нарушений |
| `%stcp_violations_{ник}%` | Нарушения указанного игрока |
| `%stcp_player%` | Имя игрока |
| `%stcp_player_uuid%` | UUID игрока |
| `%stcp_player_ip%` | IP игрока |

## Конфигурация

Файл `plugins/SessionTrackerCP/config.yml`

```yaml
# Основные настройки проверок
checks:
  radius: 60                    # Радиус проверки в блоках
  time-window-seconds: 10800    # Временное окно (3 часа)

# Команды при нарушении
violation-commands:
  enabled: false
  cooldown-seconds: 120
  commands:
    - "asConsole! broadcast &c{player} &7попытался загриферить &c{victim}"
    - "asPlayer! msg {player} &cУ тебя {warns}/{maxwarns} предупреждений"

# Лимит предупреждений
max-warns:
  default: 5
  permission: "stcp.maxwarns."

# Команды при достижении лимита (с поддержкой условий)
max-warns-commands:
  enabled: true
  cooldown-seconds: 1
  commands:
    # Без условия - выполняется всегда
    - command: "asConsole! log {player} достиг {warns} предупреждений"
    
    # С простым условием
    - condition: "{warns} >= {maxwarns}"
      command: "asConsole! broadcast &e{player} &7достиг лимита!"
    
    # Сложное условие с && (И) - оба условия должны быть true
    - condition: "{warns} >= 3 && %player_time_hours% < 1"
      command: "asConsole! kick {player} Вы слишком новичок!"
    
    # Сложное условие с || (ИЛИ) - достаточно одного true
    - condition: "{warns} >= 5 || %player_kills% > 100"
      command: "asConsole! tempban {player} 1h"
    
    # С внешним заполнителем PlaceholderAPI
    - condition: "%vault_eco_balance% < 100"
      command: "asConsole! eco take {player} 50"

# Выдача предупреждений (вкл/выкл)
warn-settings:
  enabled: true

# Права
bypass-permission: "stcp.bypass"        # Не получает предупреждения
protected-permission: "stcp.protected"  # Его вещи не мониторятся

# Уведомления персонала
staff-notifications:
  default-enabled: false
  permission: "sessiontracker.staff"

# Консольные сообщения
console:
  violation-message-enabled: false      # true - показывать нарушения в консоли
```

Поддержка условий в командах

В разделе max-warns-commands можно использовать гибкие условия:

Оператор Значение Пример
> Больше {warns} > 5
< Меньше {warns} < 3
>= Больше или равно {warns} >= {maxwarns}
<= Меньше или равно {warns} <= 0
== Равно {warns} == 10
!= Не равно {warns} != 0
&& И (оба условия) {warns} >= 3 && %player_time_hours% < 1
\|\| ИЛИ (одно из условий) {warns} >= 5 \|\| %player_kills% > 100

Поддерживаются как внутренние заполнители ({warns}), так и внешние из PlaceholderAPI (%player_time_hours%).

Префиксы в командах

Префикс Что делает
asConsole! Выполняет команду от консоли
asPlayer! Выполняет команду от имени игрока

Установка

1. Скачай SessionTrackerCP.jar из раздела Releases
2. Положи в папку plugins/
3. Перезапусти сервер
4. Настрой config.yml под себя
5. Выдай права через LuckPerms

Требования

· Java 17+
· Spigot/Paper 1.20.4+
· PlaceholderAPI (опционально, для внешних заполнителей)


```

## Что добавлено нового:

1. **Права** — добавлено `stcp.protected`
2. **Заполнители** — добавлены `{time}`, `{amount}`, `{total}`, `{status}`
3. **Раздел "Поддержка условий"** — таблица операторов и примеры
4. **Новые настройки в конфиге**:
   - `warn-settings.enabled`
   - `console.violation-message-enabled`
   - `max-warns-commands` с условиями
5. **Примеры сложных условий** с `&&`, `||` и PlaceholderAPI
6. **Более понятное описание прав**
