package br.com.kwh_monitoring.helpers

import android.util.Log
import br.com.kwh_monitoring.interfaces.ISaveDataListener
import br.com.kwh_monitoring.models.Portfolio
import br.com.kwh_monitoring.tasks.SaveDataTask


class FirebaseDataHelper : ISaveDataListener {
    fun savePortfolios(portfolioList: List<Portfolio?>?) {
        if (portfolioList != null) {
            SaveDataTask(this).execute(portfolioList)
        }
    }

    override fun onSuccess() {
        Log.d(TAG, "Dados salvos no Firebase com sucesso")
    }

    override fun onError() {}

    companion object {
        private val TAG = FirebaseDataHelper::class.java.simpleName
    }
}