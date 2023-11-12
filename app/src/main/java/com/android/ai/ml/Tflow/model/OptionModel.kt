package com.android.ai.ml.Tflow.model

import android.content.Context
import android.content.Intent
import com.android.ai.ml.Tflow.ui.activities.ScanImageActivity

data class OptionModel (
    val text:String,
    val intent: Intent
){


    companion object{
        fun getOptions(context: Context):List<OptionModel>{
            return listOf(
                OptionModel(
                    text = "Scan Image",
                    intent = Intent(context,ScanImageActivity::class.java)
                )
            )
        }
    }
}