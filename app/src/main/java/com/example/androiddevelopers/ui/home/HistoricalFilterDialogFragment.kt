package com.example.androiddevelopers.ui.home

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.setFragmentResult
import com.example.androiddevelopers.databinding.DialogHistoricalFilterBinding
import com.example.androiddevelopers.presentation.EventsViewModel
import com.example.androiddevelopers.ui.events.HistoricalPeriod
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.chip.Chip

class HistoricalFilterDialogFragment : BottomSheetDialogFragment() {

    // Claves de comunicación
    companion object {
        const val REQUEST_KEY_PERIODS = "period_selection_request"
        const val BUNDLE_KEY_PERIODS = "selected_periods"
        const val TAG = "HistoricalFilterDialog"
    }

    private var _binding: DialogHistoricalFilterBinding? = null
    private val binding get() = _binding!!
    private val viewModel: EventsViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding =
            DialogHistoricalFilterBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        val width = (resources.displayMetrics.widthPixels * 0.85).toInt()
        dialog?.window?.setLayout(width, ViewGroup.LayoutParams.WRAP_CONTENT)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // Para recuperar el orden de los eventos que escogió el usuario.
        val currentOrder = viewModel.currentSortOrder.value
        if (currentOrder == EventsViewModel.SortOrder.NEWEST) {
            binding.radioNewest.isChecked = true
        } else {
            binding.radioOldest.isChecked = true
        }
        setupChips()
        setupListeners()
    }

    private fun setupChips() {
        val activePeriods = viewModel.activePeriods.value

        HistoricalPeriod.entries.forEach { period ->
            val chip = Chip(requireContext()).apply {
                text = period.displayName
                isCheckable = true // ⭐️ Habilita la selección
                setChipBackgroundColorResource(period.colorResId)
                isChecked =
                    activePeriods.contains(period) // Cargar estado inicial
            }
            binding.chipGroupPeriods.addView(chip)
        }
    }

    private fun setupListeners() {

        binding.btnApplyFilters.setOnClickListener {
            val selectedPeriods = mutableSetOf<HistoricalPeriod>()
            binding.chipGroupPeriods.checkedChipIds.forEach { chipId ->
                val chip = binding.chipGroupPeriods.findViewById<Chip>(chipId)
                val period =
                    HistoricalPeriod.entries.find { it.displayName == chip.text.toString() }
                if (period != null) {
                    selectedPeriods.add(period)
                }
            }

            val selectedOrder = if (binding.radioNewest.isChecked) "NEWEST" else "OLDEST"

            val periodNames = ArrayList(selectedPeriods.map { it.name })

            setFragmentResult(
                REQUEST_KEY_PERIODS,
                bundleOf(
                    BUNDLE_KEY_PERIODS to periodNames,
                    "SORT_ORDER" to selectedOrder // <--- Aquí es donde se añade el orden
                )
            )

            dismiss()
        }
    }

    private fun sendResultToHomeFragment(selectedPeriods: Set<HistoricalPeriod>) {
        val periodNames = ArrayList(selectedPeriods.map { it.name })
        Log.d("FilterDebug", "$periodNames")
        setFragmentResult(
            REQUEST_KEY_PERIODS,
            bundleOf(BUNDLE_KEY_PERIODS to periodNames)
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}