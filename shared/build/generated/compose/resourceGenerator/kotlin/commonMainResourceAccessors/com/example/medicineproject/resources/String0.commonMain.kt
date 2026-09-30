@file:OptIn(InternalResourceApi::class)

package com.example.medicineproject.resources

import kotlin.OptIn
import kotlin.String
import kotlin.collections.MutableMap
import org.jetbrains.compose.resources.InternalResourceApi
import org.jetbrains.compose.resources.LanguageQualifier
import org.jetbrains.compose.resources.ResourceContentHash
import org.jetbrains.compose.resources.ResourceItem
import org.jetbrains.compose.resources.StringResource

private const val MD: String = "composeResources/com.example.medicineproject.resources/"

@delegate:ResourceContentHash(-1_802_595_255)
internal val Res.string.article_author: StringResource by lazy {
      StringResource("string:article_author", "article_author", setOf(
        ResourceItem(setOf(LanguageQualifier("ru"), ), "${MD}values-ru/strings.commonMain.cvr", 10, 46),
        ResourceItem(setOf(), "${MD}values/strings.commonMain.cvr", 10, 34),
      ))
    }

@delegate:ResourceContentHash(974_162_228)
internal val Res.string.article_not_found: StringResource by lazy {
      StringResource("string:article_not_found", "article_not_found", setOf(
        ResourceItem(setOf(LanguageQualifier("ru"), ), "${MD}values-ru/strings.commonMain.cvr", 57, 69),
        ResourceItem(setOf(), "${MD}values/strings.commonMain.cvr", 45, 49),
      ))
    }

@delegate:ResourceContentHash(-189_979_491)
internal val Res.string.back: StringResource by lazy {
      StringResource("string:back", "back", setOf(
        ResourceItem(setOf(LanguageQualifier("ru"), ), "${MD}values-ru/strings.commonMain.cvr", 127, 28),
        ResourceItem(setOf(), "${MD}values/strings.commonMain.cvr", 95, 20),
      ))
    }

@delegate:ResourceContentHash(2_029_745_591)
internal val Res.string.nothing_found: StringResource by lazy {
      StringResource("string:nothing_found", "nothing_found", setOf(
        ResourceItem(setOf(LanguageQualifier("ru"), ), "${MD}values-ru/strings.commonMain.cvr", 156, 65),
        ResourceItem(setOf(), "${MD}values/strings.commonMain.cvr", 116, 41),
      ))
    }

@delegate:ResourceContentHash(356_403_819)
internal val Res.string.search_placeholder: StringResource by lazy {
      StringResource("string:search_placeholder", "search_placeholder", setOf(
        ResourceItem(setOf(LanguageQualifier("ru"), ), "${MD}values-ru/strings.commonMain.cvr", 222, 42),
        ResourceItem(setOf(), "${MD}values/strings.commonMain.cvr", 158, 34),
      ))
    }

@InternalResourceApi
internal fun _collectCommonMainString0Resources(map: MutableMap<String, StringResource>) {
  map.put("article_author", Res.string.article_author)
  map.put("article_not_found", Res.string.article_not_found)
  map.put("back", Res.string.back)
  map.put("nothing_found", Res.string.nothing_found)
  map.put("search_placeholder", Res.string.search_placeholder)
}
