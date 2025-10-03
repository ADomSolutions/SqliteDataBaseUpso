package com.example.sqlitedatabase

data class DataSet(
    val studentID:Int,
    val studentName:String,
    val studentAddress:String,
    val studentClass:String,
    val age: Int,
    val img:ByteArray

) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as DataSet

        return img.contentEquals(other.img)
    }

    override fun hashCode(): Int {
        return img.contentHashCode()
    }
}
