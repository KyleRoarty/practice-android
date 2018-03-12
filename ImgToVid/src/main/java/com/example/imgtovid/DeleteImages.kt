package com.example.imgtovid

import android.os.AsyncTask
import java.io.File

/**
 * Created by Kyle on 3/11/2018.
 */

abstract class DeleteImages(private val path: String) : AsyncTask<Array<String>, Void, Int>(){
    //Returns date as int, Time in mins
    abstract fun getDateTime(file: String): Pair<Int, Int>

    override fun doInBackground(vararg p0: Array<String>): Int {
        val dlFL: Array<String> = p0.get(0)
        val dlFileList : Array<String> = dlFL.sortedArray()
        val dlFirst = getDateTime(dlFileList.first())

        val svFL: Array<String> = File(path).list()
        val svFileList: Array<String> = svFL.sortedArray()

        var loopIdx = 0
        while (true) {
            val cmpFile = getDateTime(svFileList[loopIdx])
            if (24*60*(dlFirst.first - cmpFile.first) + dlFirst.second - cmpFile.second <= 0){
                break
            }
            File(path,svFileList[loopIdx]).delete()
            loopIdx += 1
        }

        return 1
    }
}