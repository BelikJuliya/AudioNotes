package com.example.audionotes.ui.records

import android.Manifest
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.*
import android.content.pm.PackageManager.PERMISSION_GRANTED
import android.os.Build
import android.os.Bundle
import android.os.IBinder
import android.view.MotionEvent
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.audionotes.*
import com.example.audionotes.databinding.FragmentRecordsListBinding
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class RecordsListFragment : Fragment(R.layout.fragment_records_list) {

    private val viewModel: RecordsViewModel by viewModels()

    private val binding: FragmentRecordsListBinding by viewBinding()

    private var isServiceBound = false

    private var audioService: AudioService? = null

    private lateinit var serviceConnection: ServiceConnection

    private val permissionsList = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
        arrayOf(
            Manifest.permission.FOREGROUND_SERVICE,
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE
        )
    } else {
        arrayOf(
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE
        )
    }

    private val requestMultiplePermissions =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
            if (permissions.containsValue(false)) {
                Toast.makeText(
                    requireActivity(),
                    requireActivity().resources.getString(R.string.require_permission),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

    private val recordsAdapter by lazy {
        RecordsAdapter(
            play = { model, onComplete, onProgressChanged, duration ->
                viewModel.play(file = model.mediaFile, onComplete, onProgressChanged, duration)
            },
            resume = { model, onProgressChanged ->
                viewModel.resume(file = model.mediaFile, onProgressChanged = onProgressChanged)
            },
            pause = {
                viewModel.pause(it.mediaFile)
            },
            stop = {
                viewModel.stop(it.mediaFile)
            }
        )
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (hasPermissions().first) {
            viewModel.initRecorder()
            viewModel.getRecords()
            with(binding) {
                tvTimer.bringToFront()
                rvRecords.adapter = recordsAdapter
                rvRecords.layoutManager = LinearLayoutManager(context)
            }
        } else {
            if (hasPermissions().second) {
                showNeedPermissionDialog()
            } else {
                askForPermissions()
            }
        }
        with(binding) {
            btnRecord.setOnTouchListener { _, event ->
                if (hasPermissions().first) {
                    onRecordTouched(event)
                } else {
                    if (hasPermissions().second) {
                        showNeedPermissionDialog()
                    } else {
                        askForPermissions()
                    }
                }
                true
            }

            viewModel.recordsLiveData.observe(viewLifecycleOwner) {
                recordsAdapter.submitList(it)
            }

            viewModel.recordingTimeLiveData.observe(viewLifecycleOwner) {
                tvTimer.text = it
            }

            viewModel.errorLiveData.observe(viewLifecycleOwner) {
                Toast.makeText(requireActivity(), it, Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        view?.lightStatusBar(requireActivity())
    }

    private fun askForPermissions() {
        requestMultiplePermissions.launch(permissionsList)
    }

    private fun hasPermissions(): Pair<Boolean, Boolean> {
        val grantResult = mutableListOf<Boolean>()
        val rationaleResults = mutableListOf<Boolean>()
        permissionsList.forEach { permission ->
            if (ContextCompat.checkSelfPermission(
                    requireActivity(),
                    permission
                ) == PERMISSION_GRANTED
            ) {
                grantResult.add(true)
            } else {
                grantResult.add(false)
            }

            if (shouldShowRequestPermissionRationale(permission)) {
                rationaleResults.add(true)
            } else {
                rationaleResults.add(false)
            }
        }
        val isGranted = !grantResult.contains(false)
        val isShowRationale = rationaleResults.contains(true)
        return Pair(isGranted, isShowRationale)
    }

    private fun onRecordTouched(event: MotionEvent) {
        when (event.action) {
            // нажатие
            MotionEvent.ACTION_DOWN -> {
                viewModel.recordAudio()
            }
            // отпускание
            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                viewModel.stopRecording()
                viewModel.resetTimer()
                showSaveRecordDialog()
            }
        }
    }

    private fun showSaveRecordDialog() {
        val builder = AlertDialog.Builder(requireActivity())
        val editText = layoutInflater.inflate(R.layout.view_edit_title, null) as EditText
        builder
            .setTitle(R.string.create_title)
            .setPositiveButton(R.string.save) { _, _ ->
                viewModel.saveRecord(
                    editText.text.toString()
                        .ifEmpty { requireActivity().resources.getString(R.string.default_name) })
            }
            .setNegativeButton(R.string.delete) { dialog, _ ->
                dialog.dismiss()
            }
            .setView(editText)
        builder.create().show()
    }

    private fun showNeedPermissionDialog() {
        val builder = AlertDialog.Builder(requireActivity())
        builder.setTitle(requireActivity().resources.getString(R.string.request_assess))
        builder.setMessage(requireActivity().resources.getString(R.string.denied))
        builder.setPositiveButton(R.string.cancel_request) { dialog, _ -> dialog.dismiss() }
        builder.setNegativeButton(R.string.settings) { dialog, _ ->
            dialog.dismiss()
            (activity as MainActivity).goToPermission()
        }
        builder.setOnCancelListener { dialog: DialogInterface? -> dialog?.dismiss() }
        builder.show()
    }

    private fun showAskAgainDialog() {
        val builder = AlertDialog.Builder(requireActivity())
        builder.setTitle(requireActivity().resources.getString(R.string.request_assess))
        builder.setMessage(requireActivity().resources.getString(R.string.give_permissions))
        builder.setNegativeButton(R.string.request_again) { dialog, _ ->
            dialog.dismiss()
            askForPermissions()
        }
        builder.setOnCancelListener { dialog: DialogInterface? -> dialog?.dismiss() }
        builder.show()
    }


    ////////////////////////////////////////////////////////////////////////////////////////////////
    //  Пытался сделать воспроизведение аудио через сервис, но не хватило времени довести до ума  //
    ////////////////////////////////////////////////////////////////////////////////////////////////

    private fun startService(intent: Intent) {
        val serviceIntent = Intent(requireActivity(), AudioService::class.java)
        ContextCompat.startForegroundService(requireActivity(), serviceIntent)
    }

    private fun stopService(v: Intent) {
        val serviceIntent = Intent(requireActivity(), AudioService::class.java)
        stopService(serviceIntent)
    }

    private fun connectService() {
        serviceConnection = object : ServiceConnection {
            override fun onServiceDisconnected(name: ComponentName) {
                Toast.makeText(
                    requireActivity(),
                    "Service is disconnected",
                    Toast.LENGTH_SHORT
                )
                    .show()
                isServiceBound = false
                audioService = null
            }

            override fun onServiceConnected(name: ComponentName, service: IBinder) {
                Toast.makeText(
                    requireActivity(),
                    "Service is connected",
                    Toast.LENGTH_SHORT
                )
                    .show()
                isServiceBound = true
                val mLocalBinder = service as AudioService.LocalBinder
                audioService = mLocalBinder.getServerInstance()
            }
        }

        val startIntent = Intent(requireActivity(), AudioService::class.java)
        requireActivity().bindService(
            startIntent,
            serviceConnection as ServiceConnection,
            Context.BIND_AUTO_CREATE
        )
    }

    override fun onDestroy() {
        super.onDestroy()
        if (isServiceBound) {
            requireActivity().unbindService(serviceConnection)
            //service is active
            audioService?.stopSelf()
        }
    }
}
