package com.example.androiddevelopers.ui.home

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import coil.load
import com.example.androiddevelopers.R
import com.example.androiddevelopers.databinding.FragmentHomeBinding
import com.example.androiddevelopers.ui.events.EventsViewModel
import com.example.androiddevelopers.ui.events.HistoricEvent
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.util.Calendar

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
                if (isLoading && viewModel.events.value.isEmpty()) {
                    showLoadingState()
                }
            }
        }
    }

    private fun displayFeaturedEvent(event: HistoricEvent) {
        binding.txtYear.text = event.date
        binding.txtTitle.text = event.title
        binding.txtSubtitle.text = "${event.shortDescription}"
        binding.txtBody.text = event.detailedDescription

        val imageUrl = event.imageUrl?.trim()
        if (!imageUrl.isNullOrEmpty()) {
            binding.imgFeatured.isVisible = true
            binding.imgFeatured.load(imageUrl) {
                crossfade(true)
                placeholder(android.R.drawable.ic_menu_gallery)
                error(android.R.drawable.ic_menu_report_image)
            }
        } else {
            binding.imgFeatured.isVisible = false
        }
    }

    private fun displayEmptyState() {
        binding.txtTitle.text = "No hay eventos destacados"
        binding.txtSubtitle.text = "Consulta la sección de efemérides"
        binding.txtBody.text =
            "No se han podido cargar los eventos históricos para hoy."
        binding.imgFeatured.isVisible = false
    }

    private fun showLoadingState() {
        binding.txtTitle.text = "Cargando..."
        binding.txtSubtitle.text = "Obteniendo eventos históricos"
        binding.txtBody.text = ""
        binding.imgFeatured.isVisible = false
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