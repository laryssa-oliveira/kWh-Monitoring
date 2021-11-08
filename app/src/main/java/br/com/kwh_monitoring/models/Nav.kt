package br.com.kwh_monitoring.models

import com.google.firebase.database.IgnoreExtraProperties

@IgnoreExtraProperties
class Nav {
    var date: String? = null
    var amount = 0f

    constructor() {}
    constructor(date: String?, amount: Float) {
        this.date = date
        this.amount = amount
    }
}