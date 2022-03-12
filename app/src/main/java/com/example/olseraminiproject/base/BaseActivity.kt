package com.example.olseraminiproject.base

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity


abstract class BaseActivity : AppCompatActivity(){

    var width = 0
    var height = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(getLayoutResourceId())

        width = resources.displayMetrics.widthPixels
        height = resources.displayMetrics.heightPixels

        initView(savedInstanceState)
        initListener()
        initObserver()
    }

    abstract fun getLayoutResourceId(): Int

    abstract fun initView(savedInstanceState: Bundle?)

    abstract fun initListener()

    abstract fun initObserver()
}