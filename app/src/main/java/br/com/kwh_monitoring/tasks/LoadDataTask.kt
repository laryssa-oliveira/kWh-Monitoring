package br.com.kwh_monitoring.tasks
import android.os.AsyncTask
import br.com.kwh_monitoring.interfaces.ILoadDataListener
import br.com.kwh_monitoring.models.Portfolio


import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.io.InputStream
import java.util.ArrayList

class LoadDataTask(callback: ILoadDataListener?) :
    AsyncTask<InputStream?, Void?, List<Portfolio>?>() {
    private val mCallback: ILoadDataListener?
    override fun onPreExecute() {
        super.onPreExecute()
    }

    override fun doInBackground(vararg p0: InputStream?): List<Portfolio>? {
        val output = ByteArrayOutputStream()
        var index: Int
        try {
            index = p0[0]!!.read()
            while (index != -1) {
                output.write(index)
                index = p0[0]!!.read()
            }
            p0[0]!!.close()
        } catch (e: IOException) {
            try {
                p0[0]!!.close()
            } catch (e1: IOException) {
                e1.printStackTrace()
            }
            e.printStackTrace()
        }
        return Gson().fromJson(
            output.toString(),
            object : TypeToken<ArrayList<Portfolio?>?>() {}.type
        )
    }

    override fun onPostExecute(portfolioList: List<Portfolio>?) {
        super.onPostExecute(portfolioList)
        mCallback?.onComplete(portfolioList)
    }

    init {
        mCallback = callback
    }
}