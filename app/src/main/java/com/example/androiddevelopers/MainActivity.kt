package com.example.androiddevelopers

import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {

    // --- CÓDIGO CLAVE PARA FIJAR ICONO Y TÍTULO ---
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val navView = findViewById<BottomNavigationView>(R.id.nav_view)

        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment_activity_main) as NavHostFragment
        val navController = navHostFragment.navController

        // 1. Configuración de los destinos de nivel superior (BottomNavigationView items)
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_home,
                R.id.nested_events,
                R.id.navigation_game,
                R.id.navigation_settings // Asumo que este ID existe
            )
        )

        // 2. Vincular la barra inferior y la barra superior al NavController
        // NOTA: setupActionBarWithNavController ES NECESARIO AQUÍ
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

        // 3. Aplicar la personalización Fija
        NombreEIconoEnBarraSuperior(navController)
    }

    private fun NombreEIconoEnBarraSuperior(navController: androidx.navigation.NavController) {
        navController.addOnDestinationChangedListener { _, _, _ ->
            // Se ejecuta CADA VEZ que el NavController cambia el destino.
            supportActionBar?.apply {

                // ⚠️ Tengo que poner aquí el margen ya que aunque sea una chapuza como hemos utilizado la toolBar por defecto
                // de android no permite modificar le layout del icono y el texto
                title = "  HistoDay"

                setDisplayShowHomeEnabled(true)
                setDisplayUseLogoEnabled(true)
                val resizedIcon = getResizedIcon(R.drawable.icon, 32, 32)
                setLogo(resizedIcon)
            }
        }
    }


    /**
     * Función para redimensionar el icono y que se vea bien en la ToolBar
     */
    private fun getResizedIcon(
        drawableResId: Int,
        widthDp: Int,
        heightDp: Int
    ): Drawable? {
        val drawable =
            ContextCompat.getDrawable(this, drawableResId) ?: return null
        val metrics = resources.displayMetrics
        val widthPx = (widthDp * metrics.density).toInt()
        val heightPx = (heightDp * metrics.density).toInt()

        val bitmap: Bitmap = if (drawable is BitmapDrawable) {
            drawable.bitmap
        } else {
            val tempBitmap = Bitmap.createBitmap(
                drawable.intrinsicWidth,
                drawable.intrinsicHeight,
                Bitmap.Config.ARGB_8888
            )
            val canvas = android.graphics.Canvas(tempBitmap)
            drawable.setBounds(0, 0, canvas.width, canvas.height)
            drawable.draw(canvas)
            tempBitmap
        }

        val scaledBitmap =
            Bitmap.createScaledBitmap(bitmap, widthPx, heightPx, true)
        return BitmapDrawable(resources, scaledBitmap)
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController =
            findNavController(R.id.nav_host_fragment_activity_main)
        return navController.navigateUp() || super.onSupportNavigateUp()
    }
}