package com.nextlayer3d.app.data

import com.amplifyframework.kotlin.core.Amplify
import com.amplifyframework.storage.StoragePath

/** Product/order images are stored as either a plain external URL or an S3
 * storage key, same ambiguity the web/iOS apps resolve via getUrl. This
 * picks the right path so screens can just ask for a URL string. */
object ImageResolver {

    suspend fun resolve(value: String?): String? {
        if (value.isNullOrEmpty()) return null
        if (value.startsWith("http")) return value
        return runCatching {
            Amplify.Storage.getUrl(StoragePath.fromString(value)).url.toString()
        }.getOrNull()
    }
}
