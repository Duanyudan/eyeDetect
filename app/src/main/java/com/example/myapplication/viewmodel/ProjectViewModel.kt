package com.example.myapplication.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.bean.ProjectTree
import com.example.myapplication.repo.ProjectRepo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ProjectViewModel : ViewModel() {
    val mProjectTreeLiveData = MutableLiveData<List<ProjectTree>>()
    val mRepo=ProjectRepo()

    fun loadProjectTree() {
//        viewModelScope.launch(Dispatchers.IO) {
//            val data=mRepo.loadProjectTree()
//            mProjectTreeLiveData.postValue(data)
//        }

    }
}