package com.example.laboratory_3

import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.tabs.TabLayout
import androidx.viewpager.widget.ViewPager


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val tabLayout = findViewById<TabLayout>(R.id.tabLayout)
        val viewPager = findViewById<ViewPager>(R.id.viewPager)

        val adapter = ViewPagerAdapter(supportFragmentManager)
        adapter.addFragment(Task1Fragment(), "Завдання 1")

        viewPager.adapter = adapter
        tabLayout.setupWithViewPager(viewPager)
    }
}

class Task1Fragment : androidx.fragment.app.Fragment(R.layout.fragment_task1) {

    override fun onViewCreated(view: android.view.View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val inputPc = view.findViewById<EditText>(R.id.inputPc)
        val inputSigma1 = view.findViewById<EditText>(R.id.inputSigma1)
        val inputSigma2 = view.findViewById<EditText>(R.id.inputSigma2)
        val inputV = view.findViewById<EditText>(R.id.inputV)

        val calculateButton = view.findViewById<Button>(R.id.calculateButton)
        val resultsTextView = view.findViewById<TextView>(R.id.resultsTextView)

        fun errorFunctionApprox(x: Double): Double {
            val t = 1.0 / (1.0 + 0.5 * kotlin.math.abs(x))
            val tau = t * kotlin.math.exp(
                -x * x - 1.26551223 +
                        1.00002368 * t +
                        0.37409196 * t * t +
                        0.09678418 * t * t * t -
                        0.18628806 * t * t * t * t +
                        0.27886807 * t * t * t * t * t -
                        1.13520398 * t * t * t * t * t * t +
                        1.48851587 * t * t * t * t * t * t * t -
                        0.82215223 * t * t * t * t * t * t * t * t +
                        0.17087277 * t * t * t * t * t * t * t * t * t
            )
            return if (x >= 0) 1.0 - tau else tau - 1.0
        }

        fun normalCdf(x: Double): Double {
            return 0.5 * (1 + errorFunctionApprox(x / kotlin.math.sqrt(2.0)))
        }

        fun calculateDeltaW(Pc: Double, lowerBound: Double, upperBound: Double, sigma: Double): Double {
            val cdfUpper = normalCdf((upperBound - Pc) / sigma)
            val cdfLower = normalCdf((lowerBound - Pc) / sigma)
            return cdfUpper - cdfLower
        }

        calculateButton.setOnClickListener {
            try {
                val Pc = inputPc.text.toString().toDouble()
                val sigma1 = inputSigma1.text.toString().toDouble()
                val sigma2 = inputSigma2.text.toString().toDouble()
                val V = inputV.text.toString().toDouble()

                val delta = 0.05
                val lowerBound = Pc - Pc * delta
                val upperBound = Pc + Pc * delta

                val deltaW1 = calculateDeltaW(Pc, lowerBound, upperBound, sigma1)
                val deltaW2 = calculateDeltaW(Pc, lowerBound, upperBound, sigma2)

                val hours = 24

                val W1Profit = Pc * hours * deltaW1 * V
                val W1Penalty = Pc * hours * (1 - deltaW1) * V
                val netProfit1 = W1Profit - W1Penalty

                val W2Profit = Pc * hours * deltaW2 * V
                val W2Penalty = Pc * hours * (1 - deltaW2) * V
                val netProfit2 = W2Profit - W2Penalty

                val df = java.text.DecimalFormat("#.##")
                df.roundingMode = java.math.RoundingMode.HALF_UP

                resultsTextView.text = """
                    Результати розрахунків:

                    До вдосконалення:
                    Прибуток: ${df.format(W1Profit)} грн
                    Штрафи: ${df.format(W1Penalty)} грн
                    Чистий прибуток: ${df.format(netProfit1)} грн

                    Після вдосконалення:
                    Прибуток: ${df.format(W2Profit)} грн
                    Штрафи: ${df.format(W2Penalty)} грн
                    Чистий прибуток: ${df.format(netProfit2)} грн
                """.trimIndent()

            } catch (e: Exception) {
                resultsTextView.text = "Помилка вхідних даних. Перевірте введені значення."
            }
        }
    }
}


class ViewPagerAdapter(fm: androidx.fragment.app.FragmentManager) : androidx.fragment.app.FragmentPagerAdapter(fm) {
    private val fragmentList = mutableListOf<androidx.fragment.app.Fragment>()
    private val fragmentTitleList = mutableListOf<String>()

    override fun getItem(position: Int): androidx.fragment.app.Fragment = fragmentList[position]

    override fun getCount(): Int = fragmentList.size

    override fun getPageTitle(position: Int): CharSequence? = fragmentTitleList[position]

    fun addFragment(fragment: androidx.fragment.app.Fragment, title: String) {
        fragmentList.add(fragment)
        fragmentTitleList.add(title)
    }
}
