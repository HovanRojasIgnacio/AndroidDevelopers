package com.example.androiddevelopers.ui.home

import android.os.Bundle
import android.view.View
import androidx.compose.ui.text.intl.Locale
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.example.androiddevelopers.R
import com.example.androiddevelopers.databinding.FragmentHomeBinding
import com.example.androiddevelopers.ui.events.EventsViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.*

class HomeFragment : Fragment(R.layout.fragment_home) {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private val viewModel: EventsViewModel by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentHomeBinding.bind(view)

        setupUI()
        observeEvents()
    }

    private fun setupUI() {
        binding.txtDate.text = getCurrentDateSimple()
    }

    private fun observeEvents() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.events.collectLatest { events ->
                if (events.isNotEmpty()) {
                    val featuredEvent = events.first()
                    displayFeaturedEvent(featuredEvent)
                } else {
                    displayEmptyState()
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.isLoading.collect { isLoading ->
                if (isLoading) {
                    showLoadingState()
                }
            }
        }
    }

    private fun displayFeaturedEvent(event: com.example.androiddevelopers.ui.events.HistoricEvent) {
        binding.txtTitle.text = event.title
        binding.txtSubtitle.text = "${event.date} — ${event.shortDescription}"


        binding.txtBody.text = event.detailedDescription
    }

    private fun displayEmptyState() {
        binding.txtTitle.text = "No hay eventos destacados"
        binding.txtSubtitle.text = "Consulta la sección de efemérides"
        binding.txtBody.text = "No se han podido cargar los eventos históricos para hoy."
    }

    private fun showLoadingState() {
        binding.txtTitle.text = "Cargando..."
        binding.txtSubtitle.text = "Obteniendo eventos históricos"
        binding.txtBody.text = ""
    }

    private fun getCurrentDateSimple(): String {
        val calendar = Calendar.getInstance()
        val day = calendar.get(Calendar.DAY_OF_MONTH)
        val month = calendar.get(Calendar.MONTH)

        val monthNames = arrayOf(
            "enero", "febrero", "marzo", "abril", "mayo", "junio",
            "julio", "agosto", "septiembre", "octubre", "noviembre", "diciembre"
        )

        return "$day de ${monthNames[month]}"
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
