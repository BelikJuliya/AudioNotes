package com.example.audionotes.ui.records

import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.example.audionotes.R
import com.example.audionotes.databinding.ItemRecordBinding

import com.example.domain.model.Record
import com.example.domain.record.TrackStatus
import java.util.*

class RecordsAdapter(
    val play: (
        file: Record,
        onComplete: () -> Unit,
        onProgressChanged: (progress: Int) -> Unit,
        setDuration: (duration: Int) -> Unit
    ) -> Unit,
    val resume: (file: Record, onProgressChanged: (progress: Int) -> Unit) -> Unit,
    val pause: (model: Record) -> Unit,
    val stop: (model: Record) -> Unit
) : ListAdapter<Record, RecyclerView.ViewHolder>(ItemCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecordViewHolder {
        return RecordViewHolder(
            ItemRecordBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        holder as RecordViewHolder
        holder.bind(getItem(position) as Record)
    }

    inner class RecordViewHolder(binding: ItemRecordBinding) :
        RecyclerView.ViewHolder(binding.root) {

        private lateinit var binding: ItemRecordBinding

        fun bind(model: Record) {
            binding = ItemRecordBinding.bind(itemView)
            with(binding) {
                tvTitle.text = model.title
                tvDate.text = model.date
                if (model.playedLength.isNotEmpty()) {
                    tvTime.text = String.format(
                        binding.root.context.resources.getString(R.string.record_length),
                        model.playedLength,
                        model.audioLength
                    )
                } else {
                    tvTime.text = model.audioLength
                }

                seekbar.max = model.duration

                when (model.status) {
                    is TrackStatus.Playing -> {
                        ivPlay.setImageResource(R.drawable.ic_pause)
                    }
                    is TrackStatus.Paused -> {
                        ivPlay.setImageResource(R.drawable.ic_play)
                    }
                    is TrackStatus.Stopped -> {
                        ivPlay.setImageResource(R.drawable.ic_play)
                    }
                    is TrackStatus.Resumed -> {
                        ivPlay.setImageResource(R.drawable.ic_pause)
                    }
                }

                ivPlay.setOnClickListener { ivIcon ->
                    when (model.status) {
                        is TrackStatus.Playing -> {
                            pause(model)
                            model.status = TrackStatus.Paused
                            (ivIcon as ImageView).setImageResource(R.drawable.ic_play)
                        }
                        is TrackStatus.Paused -> {
                            resume(model) {
                                seekbar.progress = it
                            }
                            model.status = TrackStatus.Resumed
                            (ivIcon as ImageView).setImageResource(R.drawable.ic_pause)
                        }
                        is TrackStatus.Resumed -> {
                            pause(model)
                            model.status = TrackStatus.Paused
                            (ivIcon as ImageView).setImageResource(R.drawable.ic_play)
                        }
                        is TrackStatus.Stopped -> {
                            play(model, {
                                model.status = TrackStatus.Stopped
                                ivPlay.setImageResource(R.drawable.ic_play)
                            }, { progress ->
                                seekbar.progress = progress
                            },
                                { duration ->
                                    model.duration = duration
                                    seekbar.max = model.duration
                                })
                            model.status = TrackStatus.Playing
                            (ivIcon as ImageView).setImageResource(R.drawable.ic_pause)
                        }
                    }
                }
            }
        }
    }

    object ItemCallback : DiffUtil.ItemCallback<Record>() {

        override fun areItemsTheSame(
            oldItem: Record,
            newItem: Record
        ): Boolean {
            return oldItem.date == newItem.date
        }

        override fun areContentsTheSame(
            oldItem: Record,
            newItem: Record
        ): Boolean {
            return oldItem == newItem
        }
    }
}