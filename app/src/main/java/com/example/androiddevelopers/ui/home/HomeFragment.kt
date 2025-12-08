package com.example.androiddevelopers.ui.home

import android.os.Bundle
import android.util.Log
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
import androidx.recyclerview.widget.RecyclerView
import com.example.androiddevelopers.R
import com.example.androiddevelopers.databinding.FragmentHomeBinding
import com.example.androiddevelopers.ui.events.EventType
import com.example.androiddevelopers.ui.events.EventsViewModel
import com.example.androiddevelopers.ui.events.HistoricEventAdapter
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.tabs.TabLayout
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.util.Calendar

class HomeFragment : Fragment(R.layout.fragment_home) {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private lateinit var tabLayout: TabLayout
    private val viewModel: EventsViewModel by activityViewModels()
    private lateinit var homeRecyclerView: RecyclerView
    private lateinit var homeEventsAdapter: HistoricEventAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentHomeBinding.bind(view)
        tabLayout = view.findViewById(R.id.tab_event_types)
        setupUI()
        setupHomeRecyclerView(view)
        observeEvents()
        observeDate()
        setupMenu()
    }

    private fun setupHomeRecyclerView(view: View) {
        homeRecyclerView = view.findViewById(R.id.home_events_recycler_list)
        homeEventsAdapter = HistoricEventAdapter().apply {
            onItemClick = { event ->
                Log.d("HomeFragment", "Clicked event ID: ${event.id}")
            }
        }
        homeRecyclerView.adapter = homeEventsAdapter
    }

    private fun setupUI() {
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
        setupTabLayout()
    }

    private fun setupTabLayout() {
        EventType.entries.forEachIndexed { index, eventType ->
            tabLayout.addTab(
                tabLayout.newTab().setText(eventType.typeName).setTag(eventType)
            )
        }

        tabLayout.addOnTabSelectedListener(object :
            TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                val eventType = tab.tag as? EventType
                if (eventType != null) {
                    // Llamar al ViewModel para cambiar el filtro y recargar los datos
                    viewModel.setEventType(eventType)
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab) {}
            override fun onTabReselected(tab: TabLayout.Tab) {}
        })

        val initialType = EventType.EVENTS
        val initialTab =
            tabLayout.getTabAt(EventType.entries.indexOf(initialType))
        initialTab?.select()
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
                homeEventsAdapter.updateList(events)
                homeRecyclerView.isVisible = events.isNotEmpty()
                if (events.isEmpty()) {
                    showStatusMessage("No se han encontrado eventos para este día y filtro.")
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.isLoading.collect { isLoading ->
                if (isLoading && viewModel.events.value.isEmpty()) {
                    showStatusMessage("No se han encontrado eventos para este día y filtro.")
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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun showStatusMessage(message: String) {
        Log.d("HomeFragment", "STATUS: $message")
    }
}