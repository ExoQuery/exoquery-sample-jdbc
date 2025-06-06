package io.exoquery.example

import io.exoquery.capture

class Header {
}

data class Post(val id: Int, val content: String)
data class Tag(val id: Int, val postId: Int, val hashtag: String)

val posts = capture { Table<Post>() }
val tags = capture { Table<Tag>() }

fun one() {


val tweets = capture.select {
    val p = from(posts)
    val t = join(tags) { it.postId == p.id }
    where { t.hashtag == "#ExoQuery 🚀" }
    p to t
}




}
