package com.example.readcsv

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel


class QueryViewModel : ViewModel() {

    val curID: MutableLiveData<String> = MutableLiveData<String>("")

}