package br.com.kwh_monitoring.models

class Portfolio {
    var portfolioId: String? = null
    var navs: List<Nav>? = null
    constructor(portfolioId: String?, navs: List<Nav>?) {
        this.portfolioId = portfolioId
        this.navs = navs
    }
}