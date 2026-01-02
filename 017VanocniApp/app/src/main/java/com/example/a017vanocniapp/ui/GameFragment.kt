package com.example.a017vanocniapp.ui

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.a017vanocniapp.databinding.FragmentGameBinding
import com.example.a017vanocniapp.datastore.SettingsDataStore
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class GameFragment : Fragment() {
    private var _binding: FragmentGameBinding? = null
    private val binding get() = _binding!!

    private lateinit var settingsDataStore: SettingsDataStore
    private var currentQuestion = 0
    private var score = 0
    private var highScore = 0

    private val questions = listOf(
        Question("🎄 Kdy jsou Vánoce?", listOf("24. prosince", "25. prosince", "26. prosince", "31. prosince"), 0),
        Question("🎅 Jak se jmenuje Santa Klaus v Česku?", listOf("Mikuláš", "Ježíšek", "Děda Mráz", "Santa"), 1),
        Question("⭐ Co se dává na vrchol stromečku?", listOf("Hvězda", "Svíčka", "Anděl", "Kříž"), 0),
        Question("🎁 Odkud přišel zvyk dávat dárky?", listOf("Amerika", "Čechy", "Řím", "Severní Evropa"), 3),
        Question("❄️ Jaká je tradiční česká vánoční píseň?", listOf("Jingle Bells", "Narodil se Kristus Pán", "White Christmas", "Last Christmas"), 1)
    )

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentGameBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        settingsDataStore = SettingsDataStore(requireContext())

        loadHighScore()
        showQuestion()

        binding.btnAnswer1.setOnClickListener { checkAnswer(0) }
        binding.btnAnswer2.setOnClickListener { checkAnswer(1) }
        binding.btnAnswer3.setOnClickListener { checkAnswer(2) }
        binding.btnAnswer4.setOnClickListener { checkAnswer(3) }

        binding.btnRestart.setOnClickListener { restartGame() }
        binding.btnBackToSettings.setOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }



        observeDarkMode()
    }

    private fun loadHighScore() {
        viewLifecycleOwner.lifecycleScope.launch {
            settingsDataStore.quizHighScore.collectLatest { score ->
                highScore = score
                binding.textHighScore.text = "🏆 High Score: $highScore/5"
            }
        }
    }

    private fun showQuestion() {
        if (currentQuestion >= questions.size) {
            showResults()
            return
        }

        val q = questions[currentQuestion]
        binding.textQuestion.text = "Otázka ${currentQuestion + 1}/5\n\n${q.question}"
        binding.btnAnswer1.text = q.answers[0]
        binding.btnAnswer2.text = q.answers[1]
        binding.btnAnswer3.text = q.answers[2]
        binding.btnAnswer4.text = q.answers[3]

        binding.textScore.text = "Skóre: $score"

        binding.btnAnswer1.isEnabled = true
        binding.btnAnswer2.isEnabled = true
        binding.btnAnswer3.isEnabled = true
        binding.btnAnswer4.isEnabled = true
    }

    private fun checkAnswer(selectedIndex: Int) {
        val q = questions[currentQuestion]

        if (selectedIndex == q.correctAnswer) {
            score++
            Toast.makeText(requireContext(), "✅ Správně!", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(requireContext(), "❌ Špatně! Správná odpověď: ${q.answers[q.correctAnswer]}", Toast.LENGTH_LONG).show()
        }

        binding.btnAnswer1.isEnabled = false
        binding.btnAnswer2.isEnabled = false
        binding.btnAnswer3.isEnabled = false
        binding.btnAnswer4.isEnabled = false

        currentQuestion++

        binding.root.postDelayed({
            showQuestion()
        }, 2000)
    }

    private fun showResults() {
        binding.textQuestion.text = when (score) {
            5 -> "🎅 PERFEKTNÍ!\n\n🎁🎁🎁\n\nZískal jsi všechny body!"
            in 3..4 -> "⭐ SKVĚLÉ!\n\n🎁🎁\n\nSkóre: $score/5"
            else -> "🎄 POKUS SE ZNOVU!\n\n🎁\n\nSkóre: $score/5"
        }

        binding.btnAnswer1.visibility = View.GONE
        binding.btnAnswer2.visibility = View.GONE
        binding.btnAnswer3.visibility = View.GONE
        binding.btnAnswer4.visibility = View.GONE
        binding.btnRestart.visibility = View.VISIBLE

        if (score > highScore) {
            viewLifecycleOwner.lifecycleScope.launch {
                settingsDataStore.saveQuizHighScore(score)
            }
            Toast.makeText(requireContext(), "🏆 NOVÝ REKORD!", Toast.LENGTH_LONG).show()
        }
    }

    private fun restartGame() {
        currentQuestion = 0
        score = 0
        binding.btnAnswer1.visibility = View.VISIBLE
        binding.btnAnswer2.visibility = View.VISIBLE
        binding.btnAnswer3.visibility = View.VISIBLE
        binding.btnAnswer4.visibility = View.VISIBLE
        binding.btnRestart.visibility = View.GONE
        showQuestion()
    }

    private fun observeDarkMode() {
        viewLifecycleOwner.lifecycleScope.launch {
            settingsDataStore.isDarkMode.collectLatest { isDark ->
                if (isDark) {
                    binding.root.setBackgroundColor(Color.parseColor("#1a1a1a"))
                    binding.textTitle.setTextColor(Color.WHITE)
                    binding.textQuestion.setTextColor(Color.parseColor("#FF4444")) // ČERVENÁ
                    binding.textQuestion.setBackgroundColor(Color.parseColor("#2d2d2d"))
                    binding.textScore.setTextColor(Color.WHITE)
                    binding.textHighScore.setTextColor(Color.parseColor("#FFD700"))
                } else {
                    binding.root.setBackgroundColor(Color.parseColor("#f5f5f5"))
                    binding.textTitle.setTextColor(Color.parseColor("#c41e3a"))
                    binding.textQuestion.setTextColor(Color.parseColor("#c41e3a")) // ČERVENÁ
                    binding.textQuestion.setBackgroundColor(Color.parseColor("#FFE5E5"))
                    binding.textScore.setTextColor(Color.BLACK)
                    binding.textHighScore.setTextColor(Color.parseColor("#165B33"))
                }
            }
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    data class Question(
        val question: String,
        val answers: List<String>,
        val correctAnswer: Int
    )
}
