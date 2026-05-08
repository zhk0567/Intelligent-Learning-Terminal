package com.guyunxinchuan.heritage.music

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView

class PlayerPlaylistAdapter(
    private val tracks: List<PlayerSyncState.SyncTrack>,
    private var selectedIndex: Int,
    private val onPick: (Int) -> Unit,
) : RecyclerView.Adapter<PlayerPlaylistAdapter.VH>() {

    override fun getItemCount(): Int = tracks.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val v = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_player_playlist_row, parent, false)
        return VH(v)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        val t = tracks[position]
        holder.title.text = t.title
        holder.artist.text = t.artist
        holder.duration.text = formatDuration(t.durationMs)
        val current = position == selectedIndex
        holder.playingIcon.visibility = if (current) View.VISIBLE else View.INVISIBLE
        holder.itemView.setBackgroundColor(
            if (current) ContextCompat.getColor(holder.itemView.context, R.color.bg_card_elevated)
            else Color.TRANSPARENT,
        )
        holder.itemView.setOnClickListener { onPick(position) }
    }

    class VH(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val playingIcon: ImageView = itemView.findViewById(R.id.playlistRowPlayingIcon)
        val title: TextView = itemView.findViewById(R.id.playlistRowTitle)
        val artist: TextView = itemView.findViewById(R.id.playlistRowArtist)
        val duration: TextView = itemView.findViewById(R.id.playlistRowDuration)
    }

    private fun formatDuration(milliseconds: Int): String {
        val totalSeconds = milliseconds / 1000
        val minutes = totalSeconds / 60
        val seconds = totalSeconds % 60
        return String.format("%02d:%02d", minutes, seconds)
    }
}
