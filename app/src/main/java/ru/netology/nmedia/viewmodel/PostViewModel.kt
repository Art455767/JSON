package ru.netology.nmedia.viewmodel

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.repository.PostRepository
import ru.netology.nmedia.repository.PostRepositoryFileImpl
import ru.netology.nmedia.repository.PostRepositoryInMemoryImpl

private val empty = Post(
    id = 0L,
    author = "",
    content = "",
    published = "",
    likedByMe = false,
    sharedByMe = false
)
class PostViewModel(private val context: Context) : ViewModel() {
    private val repository: PostRepository = PostRepositoryFileImpl(context)

    val data = repository.getAll()
    val edited = MutableLiveData(empty)
    fun applyChangesAndSave(newText: String) {
        edited.value?.let {
            val text = newText.trim()
            if(text!= it.content){
                repository.save(it.copy(content = text))
            }
        }
        edited.value = empty
    }
    fun likeByID(id: Long) = repository.likeByID(id)
    fun shareByID(id: Long) = repository.shareByID(id)
    fun removeByID(id: Long) = repository.removeByID(id)
    fun edit(post: Post){
        edited.value = post
    }

    fun changeContent(content: String) {
        val text = content.trim()
        if (edited.value?.content == text) {
            return
        }
        edited.value = edited.value?.copy(content = text)
    }


    fun save() {
        edited.value?.let {
            repository.save(it)
        }
        edited.value = empty
    }

}