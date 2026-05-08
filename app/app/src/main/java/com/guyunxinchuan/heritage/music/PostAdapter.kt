package com.guyunxinchuan.heritage.music

import android.content.Intent
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView

class PostAdapter(
    private val posts: List<Post>,
    private val onItemClick: (Post) -> Unit
) : RecyclerView.Adapter<PostAdapter.PostViewHolder>() {

    inner class PostViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val postTitle: TextView = itemView.findViewById(R.id.postTitle)
        val postContent: TextView = itemView.findViewById(R.id.postContent)
        val postAuthor: TextView = itemView.findViewById(R.id.postAuthor)
        val postCategory: TextView = itemView.findViewById(R.id.postCategory)
        val postTime: TextView = itemView.findViewById(R.id.postTime)
        val likeButton: LinearLayout = itemView.findViewById(R.id.postLikeButton)
        val commentButton: LinearLayout = itemView.findViewById(R.id.postCommentButton)
        val shareButton: LinearLayout = itemView.findViewById(R.id.postShareButton)
        val shareIcon: ImageView = itemView.findViewById(R.id.postShareIcon)
        val likeIcon: ImageView = itemView.findViewById(R.id.postLikeIcon)
        val commentIcon: ImageView = itemView.findViewById(R.id.postCommentIcon)
        val likeCount: TextView = itemView.findViewById(R.id.postLikeCount)
        val commentCount: TextView = itemView.findViewById(R.id.postCommentCount)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_post, parent, false)
        return PostViewHolder(view)
    }

    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
        val post = posts[position]

        holder.postTitle.text = post.title
        holder.postContent.text = post.content
        holder.postAuthor.text = post.author
        holder.postCategory.text = post.category
        holder.postTime.text = post.timeText

        bindEngagement(holder, post)

        holder.itemView.setOnClickListener {
            onItemClick(post)
        }

        holder.likeButton.setOnClickListener { v ->
            v.animate().scaleX(0.92f).scaleY(0.92f).setDuration(100)
                .withEndAction {
                    v.animate().scaleX(1f).scaleY(1f).setDuration(120).start()
                }.start()
            val s = PostEngagementStore.stateForPost(post)
            if (s.liked) {
                s.liked = false
                s.likeCount = (s.likeCount - 1).coerceAtLeast(0)
            } else {
                s.liked = true
                s.likeCount += 1
            }
            bindEngagement(holder, post)
        }

        holder.commentButton.setOnClickListener {
            val s = PostEngagementStore.stateForPost(post)
            s.commentCount += 1
            holder.commentCount.text = s.commentCount.toString()
            UiFeedback.toast(holder.itemView.context, "评论数 +1（演示）")
        }

        holder.shareButton.setOnClickListener {
            val ctx = holder.itemView.context
            val share = Intent(Intent.ACTION_SEND).apply {
                type = "text/plain"
                putExtra(Intent.EXTRA_SUBJECT, post.title)
                putExtra(Intent.EXTRA_TEXT, "${post.title}\n\n${post.content}")
            }
            runCatching {
                ctx.startActivity(Intent.createChooser(share, "分享"))
            }
        }

        setupCommentInteraction(holder)
        setupShareInteraction(holder.shareButton, holder.shareIcon)
    }

    private fun bindEngagement(holder: PostViewHolder, post: Post) {
        val s = PostEngagementStore.stateForPost(post)
        val ctx = holder.itemView.context
        val muted = ContextCompat.getColor(ctx, R.color.shop_text_muted)
        holder.likeIcon.setImageResource(
            if (s.liked) R.drawable.ic_post_like_filled else R.drawable.ic_post_like_outline
        )
        holder.likeIcon.clearColorFilter()
        holder.likeCount.text = s.likeCount.toString()
        holder.likeCount.setTextColor(muted)
        holder.commentCount.text = s.commentCount.toString()
        holder.commentCount.setTextColor(muted)
    }

    private fun setupCommentInteraction(holder: PostViewHolder) {
        val ctx = holder.itemView.context
        val muted = ContextCompat.getColor(ctx, R.color.shop_text_muted)
        val pressed = ContextCompat.getColor(ctx, R.color.text_primary)
        holder.commentButton.setOnTouchListener { v, event ->
            when (event.actionMasked) {
                MotionEvent.ACTION_DOWN -> {
                    v.animate().scaleX(0.92f).scaleY(0.92f).setDuration(100).start()
                    holder.commentIcon.animate().rotationBy(3f).setDuration(80).start()
                    holder.commentCount.setTextColor(pressed)
                }
                MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                    v.animate().scaleX(1f).scaleY(1f).setDuration(100).start()
                    holder.commentIcon.rotation = 0f
                    holder.commentCount.setTextColor(muted)
                }
            }
            false
        }
    }

    private fun setupShareInteraction(sharePill: LinearLayout, shareIcon: ImageView) {
        sharePill.setOnTouchListener { v, event ->
            when (event.actionMasked) {
                MotionEvent.ACTION_DOWN -> {
                    v.animate().scaleX(0.92f).scaleY(0.92f).setDuration(100).start()
                    shareIcon.alpha = 0.85f
                }
                MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                    v.animate().scaleX(1f).scaleY(1f).setDuration(100).start()
                    shareIcon.alpha = 1f
                }
            }
            false
        }
    }

    override fun getItemCount(): Int = posts.size
}
