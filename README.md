# Каталог медицинских публикаций

Приложение на Kotlin Multiplatform и Compose Multiplatform для поиска публикаций PubMed. Общий код используется на Android, Desktop, iOS и в браузере (JS/Wasm).

Лабораторная работа 4, веха В2: живые данные, корутины, состояния экрана, поиск с отменой и постраничная загрузка.

## Возможности

- Список публикаций по запросу `medicine` при запуске.
- Поиск на сервере через 400 мс после последнего ввода. Новый запрос отменяет предыдущую загрузку.
- Загрузка страниц по 20 записей при прокрутке. Ошибка следующей страницы сохраняет загруженные статьи и позволяет повторить запрос.
- Деталь публикации: название, все авторы, журнал, дата, запись PubMed с аннотацией при её наличии.
- До 10 связанных публикаций из PubMed. Их загрузку можно повторить отдельно от основной статьи.
- Переходы к связанным статьям и возврат по стеку.
- Сообщения об отсутствии связи, таймауте, ошибке запроса, ошибке сервера и некорректном ответе.
- Интерфейс на русском и английском, язык выбирается по системе. Публикации остаются на языке источника.

Данные на диск не сохраняются. После перезапуска приложение загружает их заново.

## Источник данных

[NCBI E-utilities](https://www.ncbi.nlm.nih.gov/books/NBK25501/), база PubMed:

| Метод | Назначение |
| --- | --- |
| `esearch.fcgi` | Поиск PMID, количество результатов, `retstart` и `retmax` |
| `esummary.fcgi` | Название, авторы, журнал и дата по списку PMID |
| `efetch.fcgi` | Запись публикации в текстовом формате `abstract` |
| `elink.fcgi` | Связанные PMID, связь `pubmed_pubmed` |

JSON разбирается через `kotlinx.serialization`, неизвестные поля игнорируются. Текстовая запись `efetch` отображается в формате источника. Отсутствие аннотации не заменяется выдуманным текстом.

Клиент работает без API-ключа. Запросы разделены интервалом не менее 350 мс, чтобы соблюдать лимит NCBI в три запроса в секунду. Поиск PubMed позволяет просмотреть первые 10 000 результатов; для более узкой выдачи нужно уточнить запрос.

## Настройка

Нужны JDK 21 и Android SDK, указанный в настройках проекта. Для сборки iOS нужен macOS с Xcode. Версии Kotlin, Compose и библиотек заданы в `gradle/libs.versions.toml`.

Добавьте в локальный файл `local.properties`, сохранив имеющуюся строку `sdk.dir`:

```properties
PUBMED_BASE_URL=https://eutils.ncbi.nlm.nih.gov/entrez/eutils/
```

В конце адреса нужен `/`. Файл `local.properties` исключён из Git. BuildKonfig передаёт настройку в общий код во время сборки. Для CI можно использовать параметр `-PPUBMED_BASE_URL=...`.

## Запуск и сборка

```bash
# Desktop
./gradlew :desktopApp:run

# Desktop на русском
JAVA_TOOL_OPTIONS="-Duser.language=ru" ./gradlew :desktopApp:run

# Android APK
./gradlew :androidApp:assembleDebug

# Браузер, Wasm
./gradlew :webApp:wasmJsBrowserDevelopmentRun

# Браузер, JS
./gradlew :webApp:jsBrowserDevelopmentRun

# Готовая браузерная сборка Wasm
./gradlew :webApp:wasmJsBrowserDistribution
```

Для iOS откройте `iosApp/iosApp.xcodeproj` в Xcode. Проверка компиляции общего кода для симулятора:

```bash
./gradlew :shared:compileKotlinIosSimulatorArm64
```

При намеренном изменении браузерных зависимостей Gradle может попросить обновить Yarn lock-файл. Используйте предложенную в сообщении задачу `kotlinWasmUpgradeYarnLock` или `kotlinUpgradeYarnLock`, затем повторите сборку. Изменения lock-файла проверяются и сохраняются вместе с зависимостями.

## Архитектура

```text
HttpClient -> PubMedApi -> NetworkArticleDataSource -> ArticleRepositoryImpl
                                                       |
                                              LoadArticlesUseCase
                                                       |
                                                   ViewModel
                                                       |
                                                  StateFlow
                                                       |
                                                state + onIntent
```

- `data/network`: общий клиент, таймаут, логирование и движки через `expect/actual`. На Android/JVM используется CIO, на JS/Wasm - Js, на iOS - Darwin.
- `data/api`: запросы и разбор ответов. Крупный разбор JSON и текста выполняется на `Dispatchers.Default`.
- `data/datasource`: сетевые исключения переводятся в `LoadException`. `CancellationException` пробрасывается дальше.
- `data/dto` и `data/mappers`: модели сервера и преобразование в доменные `Article`/`ArticlePage`.
- `domain`: модели, интерфейс репозитория и `LoadArticlesUseCase`. Пагинация защищена `Mutex.tryLock()`, offset меняется после успешной загрузки.
- `ui/list`, `ui/detail`: ViewModel, состояния и действия пользователя. Ошибка догрузки отличается от ошибки всего экрана.
- `ui/screens`, `ui/components`, `ui/model`, `ui/theme`: отображение, общие компоненты, UI-модели и тема.
- `navigation`: маршруты, стек и Navigation 3. ViewModel привязана к записи стека и очищается при её удалении. Повторное открытие одной статьи создаёт отдельную запись навигации.

На web `Dispatchers.Default` использует цикл событий браузера, а не отдельный пул потоков. Страницы ограничены 20 публикациями, блокирующих операций в общем коде нет.

## Проверка

```bash
./gradlew :shared:jvmTest
./gradlew :androidApp:assembleDebug :webApp:wasmJsBrowserDistribution :desktopApp:jar
./gradlew :shared:compileKotlinJs :shared:compileKotlinIosSimulatorArm64
```

Тесты проверяют запросы и DTO, ошибки HTTP и JSON, передачу отмены, debounce поиска, повтор неудачной страницы, защиту от одновременной загрузки и сохранение детали при ошибке связей. Автоматические тесты не зависят от доступности PubMed.

Отчёты компилятора Compose включены в `shared/build.gradle.kts`:

```bash
./gradlew :shared:compileKotlinJvm --rerun-tasks
```

Результат находится в `shared/build/compose_compiler/`. При проверке смотрим `restartable skippable` у `ArticleListScreen`, `ArticleDetailScreen` и `ArticleCard`.

Для ручной проверки: найти статью, открыть деталь, перейти к связанной статье, вернуться дважды; быстро изменить запрос; выключить сеть и повторить загрузку; закрыть деталь во время запроса; дойти до следующей страницы и проверить повтор после сбоя.

Отчёт по первой лабораторной сохранён в [docs/lab-1/Lab-1.pdf](docs/lab-1/Lab-1.pdf).
