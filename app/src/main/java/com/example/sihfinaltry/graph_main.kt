package com.example.sihfinaltry

import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity


class GraphActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.graph_page)

        val bar1 = findViewById<Button>(R.id.bar1)
        val bar2 = findViewById<Button>(R.id.bar2)
        val bar3 = findViewById<Button>(R.id.bar3)
        val bar4 = findViewById<Button>(R.id.bar4)

        bar1.height= ;
        bar2.height=  ;
        bar3.height=  ;
        bar4.height=  ;

    }