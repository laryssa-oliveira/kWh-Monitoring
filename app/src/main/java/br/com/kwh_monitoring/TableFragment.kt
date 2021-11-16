package br.com.kwh_monitoring

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import br.com.kwh_monitoring.databinding.FragmentTableBinding
import com.github.mikephil.charting.data.Entry
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class TableFragment : Fragment() {

    private lateinit var binding: FragmentTableBinding
    private val adapter by lazy { TableAdapter() }
    private val database = Firebase.database
    private var powerRef: DatabaseReference = database.reference.child("ap_power")
    private val referenceDate: DatabaseReference = database.reference.child("date_hour")

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentTableBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val listConsumptionKW = ArrayList<ConsumptionKW>()
        powerRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                listConsumptionKW.clear()
                for (snapshot in dataSnapshot.children) {
                    val apPower: Float? = snapshot.getValue(Float::class.java)
                    val power = apPower?.div(1000)
                    listConsumptionKW.add(ConsumptionKW(power))
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {}
        })

        val listDate = ArrayList<DateHour>()
        referenceDate.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                listDate.clear()
                for (snapshot in dataSnapshot.children) {
                    val date: String? = snapshot.getValue(String::class.java)
                    listDate.add(DateHour(date))
                    adapter.setItems(listConsumptionKW, listDate)
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {}
        })

        binding.recyclerView.adapter = adapter

    }
}
