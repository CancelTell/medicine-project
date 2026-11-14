package com.example.medicineproject.domain

import kotlinx.coroutines.sync.Mutex

class LoadArticlesUseCase(
    private val repository: ArticleRepository,
    private val query: String,
    private val pageSize: Int = 20,
) {
    private val mutex = Mutex()
    private var nextOffset: Int? = 0

    suspend fun loadNext(): ArticlePage? {
        if (!mutex.tryLock()) return null
        try {
            val offset = nextOffset ?: return null
            val page = repository.getArticles(query, offset, pageSize)
            // При ошибке offset остаётся прежним: повторим ту же страницу.
            nextOffset = page.nextOffset
            return page
        } finally {
            mutex.unlock()
        }
    }
}
