package br.com.kwh_monitoring.tasks

import br.com.kwh_monitoring.Constants
import br.com.kwh_monitoring.models.Portfolio
import br.com.kwh_monitoring.interfaces.ILoadDataListener
import com.google.firebase.database.*
import java.util.ArrayList


class LoadDataTask2(private val mCallback: ILoadDataListener?) {
    fun load() {
        val databaseReference = FirebaseDatabase.getInstance()
            .getReference(Constants.PORTFOLIOS_BY_DAY)
        if (databaseReference != null) {
            databaseReference.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    val portfolioList: MutableList<Portfolio?> = ArrayList()
                    for (data in dataSnapshot.children) {
                        val portfolio = data.getValue(Portfolio::class.java)
                        portfolioList.add(portfolio)
                    }
                    mCallback?.onComplete(portfolioList)
                }

                override fun onCancelled(databaseError: DatabaseError) {}
            })
        }
    }
}