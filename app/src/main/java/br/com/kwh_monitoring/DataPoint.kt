package br.com.kwh_monitoring

class DataPoint {
    var xValue = 0
    var yValue = 0.0f

    constructor() {}
    constructor(xValue: Int?, yValue: Float?) {

        this.xValue = xValue!!
        this.yValue = yValue!!
    }

    fun getxValue(): Int {
        return xValue
    }

    fun getyValue(): Float {
        return yValue
    }
}
