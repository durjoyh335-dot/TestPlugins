package com.example

import com.lagradost.cloudstream3.*
import com.lagradost.cloudstream3.utils.*

class HiAnimeProvider : MainAPI() {
    override var mainUrl = "https://hianime.to"
    override var name = "HiAnime"
    override val hasMainPage = true
    override val supportedTypes = setOf(TvType.Anime, TvType.AnimeMovie)

    override suspend fun getMainPage(page: Int, request: MainPageRequest): HomePageResponse? {
        val document = app.get(mainUrl).document
        val homeItems = ArrayList<HomePageList>()
        
        val items = document.select("div.flw-item")
        val animeList = items.mapNotNull { 
            val title = it.selectFirst("h3.film-name a")?.text() ?: return@mapNotNull null
            val href = it.selectFirst("h3.film-name a")?.attr("href") ?: return@mapNotNull null
            val poster = it.selectFirst("img.film-poster-img")?.attr("data-src")
            
            newAnimeSearchResponse(title, fixUrl(href), TvType.Anime) {
                this.posterUrl = poster
            }
        }
        
        if (animeList.isNotEmpty()) {
            homeItems.add(HomePageList("Trending Anime", animeList))
        }
        return HomePageResponse(homeItems)
    }

    override suspend fun search(query: String): List<SearchResponse> {
        val searchUrl = "$mainUrl/search?keyword=$query"
        val document = app.get(searchUrl).document
        
        return document.select("div.flw-item").mapNotNull {
            val title = it.selectFirst("h3.film-name a")?.text() ?: return@mapNotNull null
            val href = it.selectFirst("h3.film-name a")?.attr("href") ?: return@mapNotNull null
            val poster = it.selectFirst("img.film-poster-img")?.attr("data-src")
            
            newAnimeSearchResponse(title, fixUrl(href), TvType.Anime) {
                this.posterUrl = poster
            }
        }
    }
}
