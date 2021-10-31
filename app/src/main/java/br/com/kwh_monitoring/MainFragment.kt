package br.com.kwh_monitoring

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import br.com.kwh_monitoring.databinding.FragmentMainBinding
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class MainFragment : Fragment() {

    private lateinit var binding: FragmentMainBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMainBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val database = Firebase.database

        binding.off.setOnClickListener {
            val myRef = database.getReference("LED_STATUS")
            myRef.setValue("OFF")
        }

        binding.on.setOnClickListener {
            val myRef = database.getReference("LED_STATUS")
            myRef.setValue("ON")
        }
    }

}