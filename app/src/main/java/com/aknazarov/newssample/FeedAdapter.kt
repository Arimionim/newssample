package com.aknazarov.newssample

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.aknazarov.newssample.Utils.Companion.getTime
import com.prof.rssparser.Article
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.feed_item.view.*


class FeedAdapter(private val articles: List<Article>) :
    RecyclerView.Adapter<FeedAdapter.ArticleHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ArticleHolder =
        ArticleHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.feed_item, parent, false)
        )

    override fun getItemCount(): Int {
        return articles.size
    }

    override fun onBindViewHolder(holder: ArticleHolder, position: Int) =
        holder.bind(articles[position])

    inner class ArticleHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(article: Article) {
            itemView.title.text = article.title
            itemView.date.text = getTime(article.pubDate)
            itemView.author.text = article.author
            Picasso.get()
                .load(article.image)
                .into(itemView.image)

            itemView.setOnClickListener {
                val activity = itemView.context as AppCompatActivity
                val myFragment = ArticleFragment()
                myFragment.arguments = Bundle().also { args ->
                    args.putSerializable(ARTICLE_ARGUMENT, article)
                }
                activity.supportFragmentManager.beginTransaction().replace(
                    R.id.article_container,
                    myFragment
                ).addToBackStack(null).commit()
            }
        }
    }


    companion object {
        const val ARTICLE_ARGUMENT = "article"
    }
}