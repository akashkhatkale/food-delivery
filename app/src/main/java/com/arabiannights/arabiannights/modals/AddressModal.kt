package com.arabiannights.arabiannights.modals

import android.os.Parcelable
import com.arabiannights.arabiannights.utils.constants.LOCATION_HOME
import kotlinx.android.parcel.Parcelize

@Parcelize
data class AddressModal(
    var uid : String = "",
    var locationName : String = "",
    var completeAddress : String = "",
    var latitude : Double = 0.0,
    var longitude : Double = 0.0,
    var landmark : String = "",
    var tag : String = LOCATION_HOME
) : Parcelable

