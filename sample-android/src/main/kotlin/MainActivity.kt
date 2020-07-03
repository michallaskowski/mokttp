package dev.michallaskowski.kuiks.sample.android

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import androidx.lifecycle.lifecycleScope
import dev.michallaskowski.mockttp.*
import dev.michallaskowski.mockttp.sample.shared.MockServer

import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.serialization.builtins.list
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonConfiguration

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        make_call.setOnClickListener {
            val selectedEnv: Environment
            when(environment.checkedRadioButtonId) {
                environment_mocked.id -> {
                    selectedEnv = Environment.MOCKED
                    startMockServer()
                }
                environment_shared_mock.id -> {
                    selectedEnv = Environment.SHARED_MOCK
                    startCommonMockServer()
                }
                else -> selectedEnv = Environment.ORIGINAL
            }
            val goToListIntent = Intent(this, ContributorsActivity::class.java)
            goToListIntent.putExtra("environment", selectedEnv)
            startActivity(goToListIntent)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when(item.itemId) {
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }

    private var httpServer: HttpServer? = null
    private var commonHttpServer: MockServer? = null

    private fun startMockServer() {
        if (httpServer != null) {
            return
        }

        httpServer = HttpServer()
        httpServer?.router = MockingRouter()

        lifecycleScope.launch(Dispatchers.Default) {
            httpServer?.start(8080)
        }
    }

    private fun startCommonMockServer() {
        if (commonHttpServer != null) {
            return
        }

        commonHttpServer = MockServer()
        lifecycleScope.launch(Dispatchers.Default) {
            commonHttpServer?.start(8081)
        }
    }
}

private class MockingRouter: Router {
    override fun handleRequest(request: Request): Response {
        if (request.method == "GET" && request.path?.startsWith("/repos/") == true) {
            val data = Json(JsonConfiguration.Stable).stringify(
                Model.serializer().list,
                listOf(Model("test", 42)))
            return Response(200, emptyMap(), Data(data), "application/json")
        } else {
            return Response(404, emptyMap(), null, null)
        }
    }
}