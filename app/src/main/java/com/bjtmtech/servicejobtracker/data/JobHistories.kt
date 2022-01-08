package com.bjtmtech.servicejobtracker.data

import com.google.firebase.database.Exclude

data class JobHistoryData(
//    let it match firestore name convension
    var id: String? = null,
    var customerName: String? = null,
    var startDate: String? = null,
    var stopDate: String? = null
)

