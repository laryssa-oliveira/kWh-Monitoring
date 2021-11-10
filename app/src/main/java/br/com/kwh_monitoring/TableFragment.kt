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
    private var databaseRef: DatabaseReference = database.reference.child("chartTable")

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

        val list = ArrayList<HistoryConsumption>()

        databaseRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                list.clear()
                for (snapshot in dataSnapshot.children) {
                    val consumption: Consumption? = snapshot.getValue(Consumption::class.java)
                    val kWh = consumption?.getkWh()
                    val day = consumption?.getDay()
                    list.add(HistoryConsumption(day.toString(), kWh.toString()))
                    adapter.setItems(list)
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {

            }
        })
        binding.recyclerView.adapter = adapter

    }



}