package com.example.androiddevelopers.ui.home

import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.animation.AnimationUtils
import androidx.core.view.MenuProvider
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.setFragmentResultListener
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.example.androiddevelopers.R
import com.example.androiddevelopers.databinding.FragmentHomeBinding
import com.example.androiddevelopers.presentation.EventsViewModel
import com.example.androiddevelopers.ui.events.EventType
import com.example.androiddevelopers.ui.events.HistoricEventAdapter
import com.example.androiddevelopers.ui.events.HistoricalPeriod
import com.google.android.material.chip.Chip
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
        setupFragmentResultListener()
        setupUI()
        setupHomeRecyclerView(view)
        observeEvents()
        observeDate()
        setupMenu()
        observeActivePeriods()
        observeCurrentEventType()
    }

    private fun setupFragmentResultListener() {
        setFragmentResultListener(HistoricalFilterDialogFragment.REQUEST_KEY_PERIODS) { _, bundle ->

            val selectedNames =
                bundle.getStringArrayList(HistoricalFilterDialogFragment.BUNDLE_KEY_PERIODS)

            Log.d(
                "FilterDebug",
                "Nombres de períodos recibidos: $selectedNames"
            )

            if (selectedNames != null) {
                val selectedPeriods = selectedNames.mapNotNull { name ->
                    HistoricalPeriod.entries.find { it.name == name }
                }.toSet()
                Log.d(
                    "FilterDebug",
                    "Objetos HistoricalPeriod mapeados: $selectedPeriods"
                )
                viewModel.updatePeriods(selectedPeriods)
            }
        }
    }

    private fun showHistoricalPeriodFilterDialog() {
        HistoricalFilterDialogFragment().show(
            parentFragmentManager,
            HistoricalFilterDialogFragment.TAG
        )
    }

    private fun setupHomeRecyclerView(view: View) {
        homeRecyclerView = view.findViewById(R.id.home_events_recycler_list)
        homeEventsAdapter = HistoricEventAdapter().apply {
            onItemClick = { event ->
                Log.d("HomeFragment", "Clicked event ID: ${event.id}")
                navigateToEventDetail(event.id)
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

    private fun observeActivePeriods() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.activePeriods.collectLatest { periods ->
                binding.chipActivePeriods.removeAllViews()

                if (periods.isNotEmpty()) {
                    periods.forEach { period ->
                        val chip = Chip(requireContext()).apply {
                            text = period.displayName
                            isCloseIconVisible = true
                            setChipBackgroundColorResource(period.colorResId)
                            setTextColor(
                                resources.getColor(
                                    R.color.white,
                                    null
                                )
                            )
                            setOnCloseIconClickListener {
                                val current =
                                    viewModel.activePeriods.value.toMutableSet()
                                current.remove(period)
                                viewModel.updatePeriods(current)
                            }
                        }
                        binding.chipActivePeriods.addView(chip)
                    }
                    binding.chipActivePeriods.isVisible = true
                } else {
                    binding.chipActivePeriods.isVisible = false
                }
            }
        }
    }

    /*private fun setupTabLayout() {
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
    }*/

    private fun observeCurrentEventType() {
        viewLifecycleOwner.lifecycleScope.launch {
            // Recoge el estado actual del tipo de evento del ViewModel
            viewModel.currentEventType.collectLatest { eventType ->

                // Convierte el EventType (enum) en el índice de la pestaña
                val tabIndex = EventType.entries.indexOf(eventType)

                // ⭐️ SINCRONIZAR UI: Si el índice es válido y la pestaña no está seleccionada, selecciónala.
                if (tabIndex != -1 && tabLayout.selectedTabPosition != tabIndex) {
                    val tab = tabLayout.getTabAt(tabIndex)
                    tab?.select()
                }
            }
        }
    }

    private fun setupTabLayout() {
        // 1. ⚙️ Añadir todas las pestañas según los tipos de evento
        // (Este bucle se mantiene igual, ya que construye la estructura visual)
        EventType.entries.forEach { eventType ->
            tabLayout.addTab(
                tabLayout.newTab()
                    .setText(eventType.typeName)
                    .setTag(eventType) // Usamos el enum como etiqueta para fácil referencia
            )
        }

        // 2. 👂 Configurar el Listener para manejar los clics del usuario
        tabLayout.addOnTabSelectedListener(object :
            TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                val eventType = tab.tag as? EventType
                if (eventType != null) {
                    // ⭐️ Llama al ViewModel para cambiar el filtro y cargar los datos
                    viewModel.setEventType(eventType)
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab) {}

            // 💡 Opcional: Útil para forzar la recarga si el usuario toca la misma pestaña
            override fun onTabReselected(tab: TabLayout.Tab) {
                val eventType = tab.tag as? EventType
                if (eventType != null) {
                    viewModel.setEventType(eventType)
                }
            }
        })

        // 3. 🔄 Sincronizar con el Estado Persistente del ViewModel

        // Obtener el tipo de evento actual persistido en el ViewModel (ej: Fallecimientos)
        val currentType = viewModel.currentEventType.value

        // Encontrar la posición de esa pestaña
        val initialTabIndex = EventType.entries.indexOf(currentType)

        // Seleccionar la pestaña persistida. Si el índice no es -1 y la pestaña existe, la seleccionamos.
        if (initialTabIndex != -1) {
            val initialTab = tabLayout.getTabAt(initialTabIndex)
            initialTab?.select() // ⭐️ Esta línea asegura que la UI refleje el estado
        }
    }

    // Configuración del menú de la Toolbar (barra superior)
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

                    R.id.action_filter_period -> {
                        showHistoricalPeriodFilterDialog()
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
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.currentDate.collectLatest { calendar ->
                val (day, month) = viewModel.getFormattedDateComponents(calendar)
                val anim =
                    AnimationUtils.loadAnimation(context, R.anim.slide_down)
                binding.txtDay.text = day
                binding.txtMonth.text = month
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

    private fun navigateToEventDetail(eventId: Int) {
        val bundle = Bundle().apply {
            putInt("eventId", eventId)
        }
        findNavController().navigate(
            R.id.action_navigation_home_to_eventDetailFragment,
            bundle
        )
    }
}