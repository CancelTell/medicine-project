## Medical Publications Catalog ##

## Запуск

### Desktop
```bash
./gradlew :desktopApp:run
```

### Android
Откройте проект в Android Studio / IntelliJ IDEA и запустите конфигурацию `androidApp` на эмуляторе или устройстве.

## Язык интерфейса

Приложение поддерживает английский (по умолчанию) и русский. Язык берётся из системы.

**Desktop** — запуск на нужном языке:
```bash
# английский
JAVA_TOOL_OPTIONS="-Duser.language=en" ./gradlew :desktopApp:run
# русский
JAVA_TOOL_OPTIONS="-Duser.language=ru" ./gradlew :desktopApp:run
```

**Android** — сменить язык устройства: Settings → System → Languages.
