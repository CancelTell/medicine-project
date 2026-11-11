# Medical Publications Catalog

Кроссплатформенный каталог медицинских публикаций на Kotlin Multiplatform и Compose Multiplatform. Один общий код запускается на Android, Desktop (JVM), iOS и в браузере (JS, Wasm).

Лабораторная работа 1 (веха В1): каhркас приложения — экраны, навигация, тема, локализация, данные на моках.

## Предметная область

Мое приложение — это каталог научных публикаций по медицине и биологии и тд. Пользователь:

1. видит список публикаций;
2. ищет публикацию по заголовку;
3. открывает публикацию и читает аннотацию;
4. переходит к связанным публикациям и возвращается назад.

### Публикация

| Поле | Тип | Описание |
| --- | --- | --- |
| `id` | `String` | Идентификатор в формате PMID (PubMed ID) |
| `title` | `String` | Заголовок |
| `description` | `String` | Аннотация |
| `author` | `String` | Автор |
| `category` | `String` | Категория: Medicine, Science, Genetics, Virology, Oncology, Neuroscience, Cardiology, Bioengineering |

### Связанные публикации

Связанными считаются публикации **того же автора** или **той же категории**, кроме самой статьи. Правило задано в слое `domain` функцией `getRelatedArticles`.

## Источник данных

**Сейчас (В1):** 20 мок-записей в `data/MockArticles.kt`, составленных вручную. Идентификаторы имеют формат PMID, но не взяты из PubMed.

**TODO (В2):** открытый API [NCBI E-utilities](https://www.ncbi.nlm.nih.gov/books/NBK25501/) базы PubMed, `https://eutils.ncbi.nlm.nih.gov/entrez/eutils/`:

## Возможности

- список публикаций с поиском по заголовку (без учёта регистра);
- состояния «загрузка» и «ничего не найдено»;
- экран публикации: категория, заголовок, автор, аннотация, связанные публикации;
- состояние «статья не найдена»;
- переход к связанной публикации; «Назад» (кнопка, системная кнопка и жест на Android) закрывает экраны по одному;
- анимация перехода вперед и назад;
- интерфейс на английском и русском.

## Стек

- Kotlin Multiplatform

## Архитектура

Код разделён на слои, зависимости направлены к `domain`:

- **domain** — модель `Article` и интерфейс `ArticleRepository`; чистый Kotlin без Compose;
- **data** — моки и `ArticleRepositoryImpl`;
- **логика экранов** — `list/`, `detail/`: состояние (`State`), действия пользователя (`Intent`), `ViewModel` и фабрики;
- **ui** — экраны, компоненты, UI-модели, тема, навигация.

Поток данных однонаправленный: ViewModel отдаёт экрану `state`, экран передаёт действия пользователя в `onIntent`. ViewModel целиком в экран не передаётся.

Зависимости (репозиторий, навигатор, фабрики ViewModel) создаются один раз в `App.kt` и передаются вниз через параметры.

## Структура

```
shared/src/commonMain/kotlin/com/example/medicineproject/
├── App.kt                  кборень: тема, репозиторий, навигатор, фабрики
├── domain/                    Article, ArticleRepository
├── data/                              MockArticles, ArticleRepositoryImpl
└── ui/
    ├── components/            ArticleCard, ArticleSearchBar, CenteredContent, AppScaffold
    ├── detail/              State, Intent, ViewModel и фабрика публикации
    ├── model/                 UI-модели и мапперы
    ├── list/                   State, Intent, ViewModel и фабрика списка
    ├── navigation/          Navigator, AppNavDisplay
    ├── screens/        ArticleListScreen, ArticleDetailScreen
    └── theme/         цветовая схема
shared/src/commonMain/composeResources/
├── values/strings.xml      английский, изначально по умолчании
└── values-ru/strings.xml   русский
```

## Навигация

Navigation 3. Маршруты — объекты `sealed interface Screen` (`ArticleList`, `ArticleDetail(id)`), а не строки. Стек экранов хранит `Navigator` в `StateFlow`, `NavDisplay` показывает верхний экран. Заданы все три анимации перехода: вперёд, назад и предиктивный возврат жестом.

## Локализация

Все надписи интерфейса — в строковых ресурсах на двух языках, ключи в обоих файлах совпадают. Язык выбирается по языку системы. Переводится только интерфейс: заголовки и аннотации публикаций остаются на языке источника.

## Запуск

```bash
# Desktop
./gradlew :desktopApp:run

# Desktop на русском
JAVA_TOOL_OPTIONS="-Duser.language=ru" ./gradlew :desktopApp:run

# Android
./gradlew :androidApp:assembleDebug
```

У Anoroid язык меняется в настройках устройства Settings → System → Languages (Google Pixel 9 Pro).

## Проверка

Отчёт компилятора Compose включён в `shared/build.gradle.kts`:

```bash
./gradlew :shared:compileKotlinJvm
```

## Документация(отчет)

[docs/lab-1/report.docx](docs/lab-1/Lab-1.pdf)