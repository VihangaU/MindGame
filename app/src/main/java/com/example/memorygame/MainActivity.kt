package com.example.memorygame

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.View.OnClickListener
import android.widget.Button
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.forEach
import androidx.lifecycle.lifecycleScope
import com.example.memorygame.databinding.ActivityMainBinding
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var binding: ActivityMainBinding
    private var score = 0
    private var result : String = ""
    private var userAnswer : String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.apply {
            panel1.setOnClickListener(this@MainActivity)
            panel2.setOnClickListener(this@MainActivity)
            panel3.setOnClickListener(this@MainActivity)
            panel4.setOnClickListener(this@MainActivity)
            startGame()
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun disableButtons() {
        binding.root.forEach {view ->
            if (view is Button) {
                view.isEnabled = false
            }
        }
    }

    private fun enableButtons() {
        binding.root.forEach {view ->
            if (view is Button) {
                view.isEnabled = true
            }
        }
    }

    private fun startGame() {
        result = ""
        userAnswer = ""
        disableButtons()
        lifecycleScope.launch {
            val round = (3..5).random()
            repeat(round) {
                delay(400)
                val randomPanel = (1..4).random()
                result += randomPanel
                val panel = when(randomPanel) {
                    1 -> binding.panel1
                    2 -> binding.panel2
                    3 -> binding.panel3
                    else -> binding.panel4
                }
                val drawableYellow = ActivityCompat.getDrawable(this@MainActivity, R.drawable.btn_yellow)
                val drawableDefault = ActivityCompat.getDrawable(this@MainActivity, R.drawable.btn_state)
                panel.background = drawableYellow
                delay(1000)
                panel.background = drawableDefault
            }
            enableButtons()
        }
    }
    private fun loseAnimation() {
        binding.apply {
            //score = 0
            tvScore.text = "$score"
            disableButtons()
            val drawableLose = ActivityCompat.getDrawable(this@MainActivity, R.drawable.btn_lose)
            val drawableDefault = ActivityCompat.getDrawable(this@MainActivity, R.drawable.btn_state)
            lifecycleScope.launch {
                // Delay before showing the image
                delay(1000)

                // Set background image
                root.setBackgroundResource(R.drawable.joker)

                // Delay before showing the score
                delay(2000)

                // Show the score
                tvScore.visibility = View.VISIBLE

                // Change buttons background
                root.forEach { view ->
                    if (view is Button) {
                        view.background = drawableLose
                        delay(1000)
                        view.background = drawableDefault
                    }
                }
            }
        }

        val sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        val highScore = sharedPreferences.getInt("highScore", 0)
        if (score > highScore) {
            editor.putInt("highScore", score)
            editor.apply()
        }

        val intent = Intent(this, StartActivity::class.java)
        intent.putExtra("score", score)
        startActivity(intent)
        // Finish MainActivity so that it's removed from the stack
        Toast.makeText(this, "You LOSE", Toast.LENGTH_SHORT).show()
        finish()


    }

    override fun onClick(v: View?) {
        v?.let {
            userAnswer += when(it.id) {
                R.id.panel1 -> "1"
                R.id.panel2 -> "2"
                R.id.panel3 -> "3"
                R.id.panel4 -> "4"
                else -> ""
            }
            if (userAnswer == result) {
                Toast.makeText(this, "WIN", Toast.LENGTH_SHORT).show()
                score++
                binding.tvScore.text = score.toString()
                startGame()
            }
            else if (userAnswer.length >= result.length){
                loseAnimation()
            }
        }
    }
}