package com.bignerdranch.android.noteyourday.Memory

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date
import java.util.UUID

@Entity
data class Memory(
    @PrimaryKey val id: UUID = UUID.randomUUID(),
    var date: Date = Date(),
    var detail: String = "",
    var picture: String = "",
    var recipient: String = ""
) {
    val photoFileName
        get() = "IMG_$id.jpg"
}