package br.cassio.devmedia.firebase_messenger.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

/**
 * classe que representa o usuario
 *
 * @property uid
 * @property username
 * @property profileImageUrl
 */

@Parcelize
class User(val uid: String, val username: String, val profileImageUrl: String):Parcelable{
    constructor(): this("","","" )
}