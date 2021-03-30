package tk.lorddarthart.githubuserfinder.application

import android.app.Application
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.auth.FirebaseUser
import org.kodein.di.*
import org.kodein.di.android.x.androidXModule
import tk.lorddarthart.githubuserfinder.di.ViewModelFactory
import tk.lorddarthart.githubuserfinder.di.utilityModule
import tk.lorddarthart.githubuserfinder.di.viewModelsModule

class App : Application(), DIAware {
    override val di by DI.lazy {
        import(utilityModule)
        import(viewModelsModule)
    }
}