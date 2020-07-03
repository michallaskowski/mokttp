package dev.michallaskowski.kuiks.sample.android

import android.os.Bundle
import android.view.LayoutInflater
import androidx.appcompat.app.AppCompatActivity
import android.view.ViewGroup
import android.widget.TextView
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.activity_list.*
import kotlinx.android.synthetic.main.list_layout.*
import kotlinx.coroutines.launch

class ContributorsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list)

        val environment = intent.getSerializableExtra("environment") as Environment
        val repository = ContributorsRepository(environment)

        lifecycleScope.launch {
            val contributors = repository.getContributors()
            contributorsText.text = contributors.map { it.login }.joinToString(", ")
        }
    }
}
