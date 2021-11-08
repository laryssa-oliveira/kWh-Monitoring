package br.com.kwh_monitoring.utils

import br.com.kwh_monitoring.models.Nav
import java.util.*

class SortByDateUtil : Comparator<Nav?> {
    override fun compare(nav1: Nav?, nav2: Nav?): Int {
        if (nav1 != null && nav2 != null) {
            val dateTimeUtil = DateTimeUtil
            val d1 = dateTimeUtil.convertStringDateToDate(nav1.date)
            val d2 = dateTimeUtil.convertStringDateToDate(nav2.date)
            if (d1 != null && d2 != null) {
                if (d1.after(d2)) {
                    return 1
                }
            }
            return 0
        }
        return 0
    }
}