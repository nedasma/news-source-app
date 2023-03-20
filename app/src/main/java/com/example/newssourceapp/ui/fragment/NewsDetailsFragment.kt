package com.example.newssourceapp.ui.fragment

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebViewClient
import androidx.fragment.app.Fragment
import coil.load
import coil.transform.RoundedCornersTransformation
import com.example.newssourceapp.R
import com.example.newssourceapp.data.model.Article
import com.example.newssourceapp.databinding.FragmentNewsDetailsBinding
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.ParametersBuilder
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.analytics.ktx.logEvent
import com.google.firebase.ktx.Firebase

/**
 * The fragment of the details of the specific news item - contains all the UI methods in order to
 * display the data properly for the user.
 */
class NewsDetailsFragment : Fragment() {

    private var _binding: FragmentNewsDetailsBinding? = null
    private val binding get() = _binding!!

    private var firebaseAnalytics: FirebaseAnalytics = Firebase.analytics

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentNewsDetailsBinding.inflate(inflater, container, false)
        return binding.root
    }

    @SuppressLint("SetJavaScriptEnabled")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val article = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            arguments?.getSerializable("article", Article::class.java)
        } else {
            arguments?.getSerializable("article") as Article
        }

        binding.webview.visibility = View.INVISIBLE
        binding.articleImage.load(article?.urlToImage) {
            crossfade(true)
            placeholder(R.drawable.ic_launcher_foreground)
            transformations(RoundedCornersTransformation())
        }
        binding.articleTitle.text = article?.title
        binding.articleText.text = article?.description
        binding.articleAuthor.text = article?.author
        binding.date.text = article?.publishedAt
        binding.readMoreBtn.setOnClickListener {
            firebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_ITEM) {
                ParametersBuilder().apply {
                    param(FirebaseAnalytics.Param.ITEM_ID, id.toString())
                    param(FirebaseAnalytics.Param.ITEM_NAME, article?.title ?: "No title")
                    param(FirebaseAnalytics.Param.CONTENT_TYPE, "webview")
                }
            }
            binding.webview.apply {
                visibility = View.VISIBLE
                webViewClient = WebViewClient()
                loadUrl(article?.url ?: "")
                settings.javaScriptEnabled = true
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        @JvmStatic
        fun newInstance() = NewsDetailsFragment()
    }
}