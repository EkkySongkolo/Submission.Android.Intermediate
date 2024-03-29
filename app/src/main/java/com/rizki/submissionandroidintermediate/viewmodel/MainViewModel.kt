package com.rizki.submissionandroidintermediate.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.ExperimentalPagingApi
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.rizki.submissionandroidintermediate.database.ListStoryDetail
import com.rizki.submissionandroidintermediate.dataclass.LoginDataAccount
import com.rizki.submissionandroidintermediate.dataclass.RegisterDataAccount
import com.rizki.submissionandroidintermediate.dataclass.ResponseLogin
import com.rizki.submissionandroidintermediate.repository.MainRepository
import okhttp3.MultipartBody
import okhttp3.RequestBody

class MainViewModel(private val mainRepository: MainRepository) : ViewModel() {

    val stories: LiveData<List<ListStoryDetail>> = mainRepository.stories

    val message: LiveData<String> = mainRepository.message

    val isLoading: LiveData<Boolean> = mainRepository.isLoading

    val userlogin: LiveData<ResponseLogin> = mainRepository.userlogin

    fun login(loginDataAccount: LoginDataAccount) {
        mainRepository.getResponseLogin(loginDataAccount)
    }

    fun register(registDataUser: RegisterDataAccount) {
        mainRepository.getResponseRegister(registDataUser)
    }

    fun upload(
        photo: MultipartBody.Part,
        des: RequestBody,
        lat: Double?,
        lng: Double?,
        token: String
    ) {
        mainRepository.upload(photo, des, lat, lng, token)
    }

    @ExperimentalPagingApi
    fun getPagingStories(token: String): LiveData<PagingData<ListStoryDetail>> {
        return mainRepository.getPagingStories(token).cachedIn(viewModelScope)
    }

    fun getStories(token: String) {
        mainRepository.getStories(token)
    }
}
