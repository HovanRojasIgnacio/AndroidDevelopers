package com.example.androiddevelopers.ui.home

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.androiddevelopers.R
import com.example.androiddevelopers.databinding.FragmentHomeBinding
import com.example.androiddevelopers.ui.events.EventsViewModel

class HomeFragment : Fragment(R.layout.fragment_home) {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private val viewModel: EventsViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentHomeBinding.bind(view)

        val featured = viewModel.getEventById(1)

        if (featured != null) {
            binding.txtTitle.text = featured.title
        }
        if (featured != null) {
            binding.txtSubtitle.text = "${featured.date} — ${featured.shortDescription}"
        }

        val descripcion:String = "La caída de Babilonia culminó el avance del Imperio aqueménida bajo Ciro II, “el Grande”. Según las crónicas babilónicas y fuentes griegas, el ejército persa derrotó previamente a Nabónido y, tras desviar el curso del río Éufrates, penetró en la ciudad prácticamente sin resistencia significativa. La toma de Babilonia puso fin al Imperio neobabilónico y abrió una nueva etapa para la región de Mesopotamia. El cambio de poder tuvo profundas consecuencias políticas y religiosas. Ciro proclamó edictos de tolerancia, reorganizó la administración imperial y permitió el regreso de poblaciones deportadas por los babilonios, entre ellas comunidades judías exiliadas. Este gesto, atestiguado en el llamado “Cilindro de Ciro” y en tradiciones posteriores, consolidó su fama de monarca pragmático y benevolente, capaz de integrar diversos pueblos dentro de una estructura imperial amplia.Babilonia, célebre por su urbanismo, murallas y templos, conservó relevancia como centro administrativo y cultural bajo dominio persa. La transición marcó el comienzo de una era en la que el Imperio aqueménida se convirtió en la mayor potencia del Próximo Oriente, con una administración eficiente, red de caminos y una política de respeto relativo a las tradiciones locales. La fecha se recuerda como un hito que reconfiguró el equilibrio político de la Antigüedad y anticipó prácticas de gobierno que influirían en imperios posteriores"
        binding.txtBody.text = descripcion

        binding.txtDate.text="29 de octubre"
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
