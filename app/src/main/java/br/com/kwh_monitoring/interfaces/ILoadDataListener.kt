package br.com.kwh_monitoring.interfaces
import br.com.kwh_monitoring.models.Portfolio

interface ILoadDataListener {
    fun onComplete(portfolioList: List<Portfolio?>?)
    fun onError()
}