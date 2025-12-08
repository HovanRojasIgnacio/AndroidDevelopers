package com.example.androiddevelopers.ui.home

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.LinearLayout
import androidx.core.view.MenuProvider
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import coil.load
import com.example.androiddevelopers.R
import com.example.androiddevelopers.databinding.FragmentHomeBinding
import com.example.androiddevelopers.ui.events.EventsViewModel
import com.example.androiddevelopers.ui.events.HistoricEvent
import com.google.android.material.datepicker.MaterialDatePicker
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
        observeDate()
        setupMenu()
    }

    private fun setupUI() {
        // 1. Vincular los botones de navegación
        binding.btnPreviousDay.setOnClickListener {
            val newDate = viewModel.currentDate.value.clone() as Calendar
            newDate.add(Calendar.DAY_OF_YEAR, -1)
            viewModel.setDate(newDate)
        }
        binding.btnNextDay.setOnClickListener {
            val newDate = viewModel.currentDate.value.clone() as Calendar
            newDate.add(Calendar.DAY_OF_YEAR, +1)
            viewModel.setDate(newDate)
        }

    }

    // Configuración del menú de la Toolbar
    private fun setupMenu() {

        requireActivity().addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.toolbar_menu_home, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                return when (menuItem.itemId) {
                    R.id.action_calendar -> {
                        showMaterialDatePicker()
                        true
                    }

                    else -> false
                }
            }
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)
    }

    private fun showMaterialDatePicker() {

        val initialSelectionTime = viewModel.currentDate.value.timeInMillis

        val datePicker = MaterialDatePicker.Builder.datePicker()
            .setTitleText("Selecciona el día histórico")
            .setSelection(initialSelectionTime)
            .build()

        datePicker.addOnPositiveButtonClickListener { selectionTime ->
            val selectedCalendar = Calendar.getInstance().apply {
                timeInMillis = selectionTime
                set(Calendar.YEAR, Calendar.getInstance().get(Calendar.YEAR))
            }
            viewModel.setDate(selectedCalendar)
        }
        datePicker.show(childFragmentManager, datePicker.toString())

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

    private fun observeDate() {
        // Necesitas una referencia al contenedor de la fecha para animarlo
        val dateContainer =
            binding.root.findViewById<LinearLayout>(R.id.date_info) // Si le diste un ID

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.currentDate.collectLatest { calendar ->

                // 1. Obtener los componentes de fecha
                val (day, month) = viewModel.getFormattedDateComponents(calendar)

                // 2. Aplicar animación al TextView del día
                val anim =
                    AnimationUtils.loadAnimation(context, R.anim.slide_down)

                // 3. Actualizar el texto
                binding.txtDay.text = day
                binding.txtMonth.text = month

                // 4. Iniciar la animación
                binding.txtDay.startAnimation(anim)
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