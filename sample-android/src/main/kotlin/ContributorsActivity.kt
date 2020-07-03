package dev.michallaskowski.kuiks.sample.android

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.android.synthetic.main.contributors.*
import kotlinx.coroutines.launch

class ContributorsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.contributors)

        val environment = intent.getSerializableExtra("environment") as Environment
        val repository = ContributorsRepository(environment)

        lifecycleScope.launch {
            val contributors = repository.getContributors()
            contributorsText.text = contributors.map { it.login }.joinToString(", ")
        }
    }
}
