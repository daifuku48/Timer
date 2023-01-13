package com.example.timer

import android.content.Intent
import android.content.SharedPreferences
import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.widget.SeekBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.Preference
import androidx.preference.PreferenceManager
import com.example.timer.databinding.ActivityMainBinding
import java.lang.Exception

class MainActivity :
    AppCompatActivity(),
    Runnable,
    SharedPreferences.OnSharedPreferenceChangeListener{
    private lateinit var binding: ActivityMainBinding
    lateinit var handler: Handler
    private var isClick : Boolean = false
    var countDown: Long = 0
    private var defaultInterval: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.timerSeekBar.max = 301
        isClick = false
        setIntervalSharedPreferences(PreferenceManager.getDefaultSharedPreferences(this))
        binding.timerSeekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(p0: SeekBar?, p1: Int, p2: Boolean) {
                if (p2) {
                    val formatted = setFormat(countDown.toInt())
                    binding.timerText.text = formatted
                    countDown = (p1.toLong())
                }
            }

            override fun onStartTrackingTouch(p0: SeekBar?) {

            }

            override fun onStopTrackingTouch(p0: SeekBar?) {

            }

        })
        handler = Handler()
        val runnable = Runnable {
            run()
        }
        binding.timerButton.setOnClickListener {
            if (!isClick) {
                binding.timerSeekBar.isEnabled = false
                binding.timerButton.text = "Stop"
                isClick = true
                handler.post(runnable)

            } else {
                binding.timerSeekBar.isEnabled = true
                binding.timerButton.text = "Start"
                isClick = false
                handler.removeCallbacks(this)
            }
        }
        PreferenceManager.getDefaultSharedPreferences(this).registerOnSharedPreferenceChangeListener(this)
    }

    override fun onDestroy() {
        super.onDestroy()
        PreferenceManager.getDefaultSharedPreferences(this).unregisterOnSharedPreferenceChangeListener(this)
    }

    override fun run() {
        handler.postDelayed(this, 1000)
        countDown--
        if (countDown.toInt() >= 0) {
            val formatted = setFormat(countDown.toInt())
            binding.timerSeekBar.progress = countDown.toInt()
            binding.timerText.text = formatted
        } else {
            handler.removeCallbacks(this)
            binding.timerSeekBar.isEnabled = true
            binding.timerButton.text = "Start"
            setIntervalSharedPreferences(PreferenceManager.getDefaultSharedPreferences(this))
            countDown = binding.timerSeekBar.progress.toLong()
            val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)
            if (sharedPreferences.getBoolean("enable_sound", true))
            {
                when(sharedPreferences.getString("timer_melody", "bell"))
                {
                    "bell" -> {
                        val mediaPlayer = MediaPlayer.create(this, R.raw.bell_sound)
                        mediaPlayer.start()
                    }
                    "alarm" -> {
                        val mediaPlayer = MediaPlayer.create(this, R.raw.alarm_siren_sound)
                        mediaPlayer.start()
                    }
                    "bip" -> {
                        val mediaPlayer = MediaPlayer.create(this, R.raw.bip_sound)
                        mediaPlayer.start()
                    }
                }
            }
        }
    }

    private fun setFormat(number: Int) =
        "${(number / 60).toString().padStart(2, '0')}:${(number % 60).toString().padStart(2, '0')}"

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val menuInflater = menuInflater
        menuInflater.inflate(R.menu.timer_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        if (id == R.id.action_settings4)
        {
            val intentSettings = Intent(this, SettingsActivity::class.java)
            startActivity(intentSettings)
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onResume() {
        super.onResume()
        setIntervalSharedPreferences(PreferenceManager.getDefaultSharedPreferences(this))
    }

    private fun setIntervalSharedPreferences(sharedPreferences: SharedPreferences){
        try{
            defaultInterval =Integer.valueOf(sharedPreferences.getString("default_interval", ""))
            binding.timerSeekBar.progress = defaultInterval
            binding.timerText.text = setFormat(defaultInterval)
        } catch(e: Exception){
            Toast.makeText(this, "Write normal number bidlo", Toast.LENGTH_LONG).show()
        }

    }

    override fun onSharedPreferenceChanged(p0: SharedPreferences?, p1: String?) {
        if (p1.equals("default_interval")){
            setIntervalSharedPreferences(PreferenceManager.getDefaultSharedPreferences(this))
        }
    }
}