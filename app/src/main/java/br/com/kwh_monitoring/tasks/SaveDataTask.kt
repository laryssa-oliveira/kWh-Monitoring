package br.com.kwh_monitoring.tasks
import android.os.AsyncTask
import android.util.Log
import br.com.kwh_monitoring.models.Portfolio

import br.com.kwh_monitoring.interfaces.ISaveDataListener
import com.google.firebase.database.FirebaseDatabase

import java.util.ArrayList

class SaveDataTask(private val mCallback: ISaveDataListener?) :
    AsyncTask<List<Portfolio?>, Void?, Void?>() {
    override fun onPreExecute() {
        super.onPreExecute()
        Log.d(TAG, "Salvar dados")
    }

    override fun doInBackground(vararg p0: List<Portfolio?>?): Void? {
        val databaseReference = FirebaseDatabase.getInstance()
            .getReference("portfolios_by_day")
        if (databaseReference != null) {
            // just store all portfolio except the total
            val needSavingPortfolioList = ArrayList<Portfolio>()
            for (i in 0 until p0[0]!!.size - 1) {
                needSavingPortfolioList.add(
                    Portfolio(
                        p0[0]?.get(i)!!.portfolioId,
                        p0[0]?.get(i)!!.navs
                    )
                )
            }
            databaseReference.setValue(needSavingPortfolioList)
        }
        mCallback?.onSuccess()
        return null
    }

    companion object {
        private val TAG = SaveDataTask::class.java.simpleName
    }


}