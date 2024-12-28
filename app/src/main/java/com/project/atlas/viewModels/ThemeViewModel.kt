import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

class ThemeViewModel(application: Application) : AndroidViewModel(application) {

    private val prefs: SharedPreferences = application.getSharedPreferences("theme_prefs", Context.MODE_PRIVATE)
    private val _isDarkTheme = MutableLiveData(loadThemePreference())
    val isDarkTheme: LiveData<Boolean> get() = _isDarkTheme

    // Carga la preferencia guardada, o usa el tema del sistema si no hay preferencia
    private fun loadThemePreference(): Boolean {
        return prefs.getBoolean("isDarkTheme", isSystemInDarkThemeDefault())
    }

    // Determina el tema del sistema por defecto
    private fun isSystemInDarkThemeDefault(): Boolean {
        val uiMode = getApplication<Application>().resources.configuration.uiMode
        return uiMode and android.content.res.Configuration.UI_MODE_NIGHT_MASK ==
                android.content.res.Configuration.UI_MODE_NIGHT_YES
    }

    // Cambia el tema y lo guarda en las preferencias
    fun toggleTheme() {
        val newTheme = !(_isDarkTheme.value ?: false)
        _isDarkTheme.value = newTheme
        saveThemePreference(newTheme)
    }

    private fun saveThemePreference(isDark: Boolean) {
        prefs.edit().putBoolean("isDarkTheme", isDark).apply()
    }

    companion object {
        private var instance: ThemeViewModel? = null

        fun getInstance(application: Application): ThemeViewModel {
            if (instance == null) {
                instance = ThemeViewModel(application)
            }
            return instance!!
        }
    }
}
