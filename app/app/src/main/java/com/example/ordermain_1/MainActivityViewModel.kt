package com.example.ordermain_1

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.ordermain_1.Item.*

class MainActivityViewModel : ViewModel() {
    private val _bannerItemList: MutableLiveData<List<BannerItem>> = MutableLiveData()
    private val _menuinformationList : MutableLiveData<List<Menu_informationItem>> = MutableLiveData()
    private val _realmenuList : MutableLiveData<List<RealMenuItem>> = MutableLiveData()
    private val _sidemenuList : MutableLiveData<List<SidemenuItem>>  = MutableLiveData()
    private val _drinkmenuList : MutableLiveData<List<DrinkmenuItem>> = MutableLiveData()



    val bannerItemList: LiveData<List<BannerItem>> get() = _bannerItemList
    val menuinformationitemList : LiveData<List<Menu_informationItem>> get() = _menuinformationList
    val realmenuList : LiveData<List<RealMenuItem>> get() = _realmenuList
    val sidemenuList : LiveData<List<SidemenuItem>> get() = _sidemenuList
    val drinkmenuList : LiveData<List<DrinkmenuItem>> get() = _drinkmenuList




    fun setBannerItems(list: List<BannerItem>){
        _bannerItemList.value = list
    }

    fun setmenuinformationItems(list:List<Menu_informationItem>){
        _menuinformationList.value = list
    }

    fun setrealmenuItems(list:List<RealMenuItem>){
        _realmenuList.value = list
    }

    fun setsidemenuItems(list:List<SidemenuItem>){
        _sidemenuList.value = list
    }

    fun setdrinkmenuItems(list:List<DrinkmenuItem>){
        _drinkmenuList.value = list
    }



}