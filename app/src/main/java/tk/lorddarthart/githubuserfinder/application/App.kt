package tk.lorddarthart.githubuserfinder.application

import android.app.Application
import com.google.firebase.auth.FirebaseUser

class App : Application() {

    override fun onCreate() {
        super.onCreate()
        instance = this
    }

    companion object {
        lateinit var instance: App
        var user: FirebaseUser? = null
    }
}