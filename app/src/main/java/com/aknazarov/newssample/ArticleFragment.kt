package com.aknazarov.newssample

import android.annotation.SuppressLint
import android.app.ActionBar
import android.graphics.Point
import android.os.Bundle
import android.transition.TransitionInflater
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.Transformation
import android.webkit.WebChromeClient
import androidx.fragment.app.Fragment
import com.aknazarov.newssample.Utils.Companion.getTime
import com.prof.rssparser.Article
import kotlinx.android.synthetic.main.article_fragment.*

class ArticleFragment : Fragment() {
    var titleExpanded = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val inflater = TransitionInflater.from(requireContext())
        enterTransition = inflater.inflateTransition(R.transition.slight_right)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.article_fragment, container, false)
    }

    @SuppressLint("SetJavaScriptEnabled")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val article = arguments?.get(FeedAdapter.ARTICLE_ARGUMENT) as Article
        title.text = article.title
        date.text = getTime(article.pubDate)
        author.text = article.author
        tags.text = article.categories.joinToString(", ")

        if (article.content != null) {
            webview.webChromeClient = WebChromeClient()
            webview.settings.javaScriptEnabled = true
            webview.loadDataWithBaseURL(
                null,
                "<style>img{display: inline; height: auto; max-width: 100%;} " +
                        "</style>\n" + "<style>iframe{ height: auto; width: auto;}" + "</style>\n" + article.content,
                null,
                "utf-8",
                null
            )
        }

        webview.setOnScrollChangeListener { _, _, scrollY, _, _ ->
            val display = requireActivity().windowManager.defaultDisplay
            val size = Point()
            display.getRealSize(size)

            if (scrollY >= size.y / 3 && titleExpanded) {
                divider.visibility = View.GONE
                collapse(title)
                collapse(tags)
            } else if (scrollY <= size.y / 6 && !titleExpanded) {
                expand(title)
                expand(tags)
                divider.visibility = View.VISIBLE
            }
        }

        back_button.setOnClickListener { requireActivity().onBackPressed() }
    }

    private fun expand(v: View) {
        val matchParentMeasureSpec =
            View.MeasureSpec.makeMeasureSpec((v.parent as View).width, View.MeasureSpec.EXACTLY)
        val wrapContentMeasureSpec =
            View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
        v.measure(matchParentMeasureSpec, wrapContentMeasureSpec)
        val targetHeight = v.measuredHeight

        v.layoutParams.height = 1

        v.visibility = View.VISIBLE
        val a: Animation = object : Animation() {
            override fun applyTransformation(interpolatedTime: Float, t: Transformation?) {
                v.layoutParams.height =
                    if (interpolatedTime == 1f) ActionBar.LayoutParams.WRAP_CONTENT else 1.coerceAtLeast(
                        (targetHeight * interpolatedTime).toInt()
                    )
                v.requestLayout()
            }

            override fun willChangeBounds(): Boolean {
                return true
            }
        }

        a.duration = 150
        v.startAnimation(a)
        titleExpanded = true
    }

    private fun collapse(v: View) {
        val initialHeight = v.measuredHeight
        val a: Animation = object : Animation() {
            override fun applyTransformation(interpolatedTime: Float, t: Transformation?) {
                if (interpolatedTime == 1f) {
                    v.visibility = View.GONE
                } else {
                    v.layoutParams.height =
                        initialHeight - (initialHeight * interpolatedTime).toInt()
                    v.requestLayout()
                }
            }

            override fun willChangeBounds(): Boolean {
                return true
            }
        }

        // Collapse speed of 1dp/ms
        a.duration = 200
        v.startAnimation(a)
        titleExpanded = false
    }
}