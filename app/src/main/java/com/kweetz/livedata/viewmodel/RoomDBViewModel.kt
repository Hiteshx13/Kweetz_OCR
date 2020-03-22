package com.kweetz.livedata.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.kweetz.database.model.Receipt

class RoomDBViewModel: ViewModel() {

    val products:MutableLiveData<List<Receipt>> by lazy {
        MutableLiveData<List<Receipt>>()
//        products.value=roomDB.productsDao().getAllProducts().size
    }


}