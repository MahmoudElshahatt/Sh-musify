package com.shahtott.sh_musify.common.core


interface BaseCacheStrategy<Remote, Local> {

    companion object {
        private const val FIRST_PAGE = 1
        private const val PAGE_SIZE = 20
        private const val DELAY_TIME = 3 * 60 * 60 * 1000L // 3 hours
    }

    suspend fun getFromCache(page: Int, pageSize: Int): List<Local>

    suspend fun clearCachedData()

    suspend fun saveToCache(page: Int, pageSize: Int, data: List<Local>)

    fun mapFromRemoteToLocal(remoteData: List<Remote>): List<Local>

    suspend fun fetchFromRemote(page: Int, pageSize: Int): List<Remote>

    suspend fun getLastSaveTime(): Long

    suspend fun updateLastSaveTime(timeStamp: Long)

    suspend fun forceToRefresh(): Boolean = false


    suspend fun getData(
        page: Int = FIRST_PAGE,
        pageSize: Int = PAGE_SIZE,
        delayTimeToRefreshInMilli: Long = DELAY_TIME,
    ): List<Local> {
        val cachedData = getFromCache(page, pageSize)
        val currentTime = System.currentTimeMillis()
        val lastSaveTime = getLastSaveTime()

        return if (cachedData.isEmpty()
            || currentTime - lastSaveTime >= delayTimeToRefreshInMilli
            || forceToRefresh()
        ) {
            if (page == 1) {
                clearCachedData()
            }
            val remoteData = fetchFromRemote(page, pageSize)
            saveToCache(page, pageSize, mapFromRemoteToLocal(remoteData))
            val newCachedData = getFromCache(page, pageSize)
            if (newCachedData.isNotEmpty()) {
                updateLastSaveTime(System.currentTimeMillis())
            }
            newCachedData
        } else {
            cachedData
        }

    }
}