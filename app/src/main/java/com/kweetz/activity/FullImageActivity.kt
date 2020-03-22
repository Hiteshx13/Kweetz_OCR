package com.kweetz.activity

import android.os.Bundle
import android.os.PersistableBundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.kweetz.R
import com.kweetz.databinding.ActivityFullImageBinding

class FullImageActivity : BaseActivity() {

    lateinit var binding: ActivityFullImageBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_full_image)
//        binding.rlBackground
    }
}