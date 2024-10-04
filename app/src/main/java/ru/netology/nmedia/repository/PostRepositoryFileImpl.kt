package ru.netology.nmedia.repository

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import ru.netology.nmedia.dto.Post
import java.io.File
import java.io.FileReader
import java.io.FileWriter
import java.io.IOException
import java.lang.reflect.Type

class PostRepositoryFileImpl(private val context: Context) : PostRepository {

    private val gson: Gson = GsonBuilder().create()
    private val type: Type = TypeToken.getParameterized(List::class.java, Post::class.java).type
    private val file = File(context.filesDir, "posts.json")

    override fun getAll(): LiveData<List<Post>> {
        val data = MutableLiveData<List<Post>>()
        try {
            val reader = FileReader(file)
            val posts = gson.fromJson<List<Post>>(reader, type)
            reader.close()
            data.value = posts
        } catch (e: IOException) {
            data.value = emptyList()
        }
        return data
    }

    override fun likeByID(id: Long) {
        val posts = getAll().value
        if (posts != null) {
            val updatedPosts = posts.map {
                if (it.id != id) it else it.copy(
                    likedByMe = !it.likedByMe,
                    likes = it.likes + (if (it.likedByMe) -1 else 1)
                )
            }
            savePosts(updatedPosts)
        }
    }

    override fun shareByID(id: Long) {
        val posts = getAll().value
        if (posts != null) {
            val updatedPosts = posts.map {
                if (it.id != id) it else it.copy(sharedByMe = true, shares = it.shares + 1)
            }
            savePosts(updatedPosts)
        }
    }

    override fun removeByID(id: Long) {
        val posts = getAll().value
        if (posts != null) {
            val updatedPosts = posts.filter { it.id != id }
            savePosts(updatedPosts)
        }
    }

    override fun save(post: Post) {
        val posts = getAll().value
        if (posts != null) {
            val updatedPosts = if (post.id == 0L) {
                listOf(post.copy(id = System.currentTimeMillis())) + posts
            } else {
                posts.map {
                    if (it.id != post.id) it else it.copy(content = post.content)
                }
            }
            savePosts(updatedPosts)
        }
    }

    private fun savePosts(posts: List<Post>) {
        try {
            val writer = FileWriter(file)
            gson.toJson(posts, writer)
            writer.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }
}